/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright 2019 The cert-manager authors.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * This source code was copied from the following git repository:
 * https://github.com/smallstep/step-issuer
 * The source code was modified for usage in the ONAP project.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package certservice_controller

import (
	"context"
	"fmt"
	"onap.org/oom-certservice/k8s-external-provider/src/api"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/certservice-provisioner"

	"github.com/go-logr/logr"
	apiutil "github.com/jetstack/cert-manager/pkg/api/util"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1alpha2"
	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"
	core "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// CertificateRequestReconciler reconciles a CertServiceIssuer object.
type CertificateRequestReconciler struct {
	client.Client
	Log      logr.Logger
	Recorder record.EventRecorder
}

// Reconcile will read and validate a CertServiceIssuer resource associated to the
// CertificateRequest resource, and it will sign the CertificateRequest with the
// provisioner in the CertServiceIssuer.
func (reconciler *CertificateRequestReconciler) Reconcile(req ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := reconciler.Log.WithValues("certificaterequest", req.NamespacedName)

	// Fetch the CertificateRequest resource being reconciled.
	// Just ignore the request if the certificate request has been deleted.
	certificateRequest := new(cmapi.CertificateRequest)
	if err := reconciler.Client.Get(ctx, req.NamespacedName, certificateRequest); err != nil {
		if apierrors.IsNotFound(err) {
			return ctrl.Result{}, nil
		}

		log.Error(err, "failed to retrieve CertificateRequest resource")
		return ctrl.Result{}, err
	}

	// Check the CertificateRequest's issuerRef and if it does not match the api
	// group name, log a message at a debug level and stop processing.
	if certificateRequest.Spec.IssuerRef.Group != "" && certificateRequest.Spec.IssuerRef.Group != api.GroupVersion.Group {
		log.V(4).Info("resource does not specify an issuerRef group name that we are responsible for", "group", certificateRequest.Spec.IssuerRef.Group)
		return ctrl.Result{}, nil
	}

	// If the certificate data is already set then we skip this request as it
	// has already been completed in the past.
	if len(certificateRequest.Status.Certificate) > 0 {
		log.V(4).Info("existing certificate data found in status, skipping already completed CertificateRequest")
		return ctrl.Result{}, nil
	}

	// Fetch the CertServiceIssuer resource
	issuer := api.CertServiceIssuer{}
	issuerNamespaceName := types.NamespacedName{
		Namespace: req.Namespace,
		Name:      certificateRequest.Spec.IssuerRef.Name,
	}
	if err := reconciler.Client.Get(ctx, issuerNamespaceName, &issuer); err != nil {
		log.Error(err, "failed to retrieve CertServiceIssuer resource", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
		_ = reconciler.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "Failed to retrieve CertServiceIssuer resource %s: %v", issuerNamespaceName, err)
		return ctrl.Result{}, err
	}

	// Check if the CertServiceIssuer resource has been marked Ready
	if !certServiceIssuerHasCondition(issuer, api.CertServiceIssuerCondition{Type: api.ConditionReady, Status: api.ConditionTrue}) {
		err := fmt.Errorf("resource %s is not ready", issuerNamespaceName)
		log.Error(err, "failed to retrieve CertServiceIssuer resource", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
		_ = reconciler.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "CertServiceIssuer resource %s is not Ready", issuerNamespaceName)
		return ctrl.Result{}, err
	}

	// Load the provisioner that will sign the CertificateRequest
	provisioner, ok := provisioners.Load(issuerNamespaceName)
	if !ok {
		err := fmt.Errorf("provisioner %s not found", issuerNamespaceName)
		log.Error(err, "failed to provisioner for CertServiceIssuer resource")
		_ = reconciler.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "Failed to load provisioner for CertServiceIssuer resource %s", issuerNamespaceName)
		return ctrl.Result{}, err
	}

	// Sign CertificateRequest
	signedPEM, trustedCAs, err := provisioner.Sign(ctx, certificateRequest)
	if err != nil {
		log.Error(err, "failed to sign certificate request")
		return ctrl.Result{}, reconciler.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonFailed, "Failed to sign certificate request: %v", err)
	}
	certificateRequest.Status.Certificate = signedPEM
	certificateRequest.Status.CA = trustedCAs

	return ctrl.Result{}, reconciler.setStatus(ctx, certificateRequest, cmmeta.ConditionTrue, cmapi.CertificateRequestReasonIssued, "Certificate issued")
}

// SetupWithManager initializes the CertificateRequest controller into the
// controller runtime.
func (reconciler *CertificateRequestReconciler) SetupWithManager(manager ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(manager).
		For(&cmapi.CertificateRequest{}).
		Complete(reconciler)
}

// certServiceIssuerHasCondition will return true if the given CertServiceIssuer resource has
// a condition matching the provided CertServiceIssuerCondition. Only the Type and
// Status field will be used in the comparison, meaning that this function will
// return 'true' even if the Reason, Message and LastTransitionTime fields do
// not match.
func certServiceIssuerHasCondition(issuer api.CertServiceIssuer, condition api.CertServiceIssuerCondition) bool {
	existingConditions := issuer.Status.Conditions
	for _, cond := range existingConditions {
		if condition.Type == cond.Type && condition.Status == cond.Status {
			return true
		}
	}
	return false
}

func (reconciler *CertificateRequestReconciler) setStatus(ctx context.Context, certificateRequest *cmapi.CertificateRequest, status cmmeta.ConditionStatus, reason, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	apiutil.SetCertificateRequestCondition(certificateRequest, cmapi.CertificateRequestConditionReady, status, reason, completeMessage)

	// Fire an Event to additionally inform users of the change
	eventType := core.EventTypeNormal
	if status == cmmeta.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	reconciler.Recorder.Event(certificateRequest, eventType, reason, completeMessage)

	return reconciler.Client.Status().Update(ctx, certificateRequest)
}
