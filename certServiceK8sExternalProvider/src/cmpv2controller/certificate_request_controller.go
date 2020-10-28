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

package cmpv2controller

import (
	"context"
	"fmt"

	"github.com/go-logr/logr"
	apiutil "github.com/jetstack/cert-manager/pkg/api/util"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"
	core "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/logger"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner"
	x509utils "onap.org/oom-certservice/k8s-external-provider/src/x509"
)

const (
	privateKeySecretNameAnnotation = "cert-manager.io/private-key-secret-name"
	privateKeySecretKey = "tls.key"
)

// CertificateRequestController reconciles a CMPv2Issuer object.
type CertificateRequestController struct {
	client.Client
	Log      logr.Logger
	Recorder record.EventRecorder
}

// Reconcile will read and validate a CMPv2Issuer resource associated to the
// CertificateRequest resource, and it will sign the CertificateRequest with the
// provisioner in the CMPv2Issuer.
func (controller *CertificateRequestController) Reconcile(k8sRequest ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := controller.Log.WithValues("certificate-request-controller", k8sRequest.NamespacedName)

	// 1. Fetch the CertificateRequest resource being reconciled.
	certificateRequest := new(cmapi.CertificateRequest)
	log.Info("Registered new certificate sign request: ", "cert-name", certificateRequest.Name)
	if err := controller.Client.Get(ctx, k8sRequest.NamespacedName, certificateRequest); err != nil {
		err = handleErrorResourceNotFound(log, err)
		return ctrl.Result{}, err
	}

	// 2. Check if CertificateRequest is meant for CMPv2Issuer (if not ignore)
	if !isCMPv2CertificateRequest(certificateRequest) {
		log.Info("Certificate request is not meant for CMPv2Issuer (ignoring)",
			"group", certificateRequest.Spec.IssuerRef.Group,
			"kind", certificateRequest.Spec.IssuerRef.Kind)
		return ctrl.Result{}, nil
	}

	// 3. If the certificate data is already set then we skip this request as it
	// has already been completed in the past.
	if len(certificateRequest.Status.Certificate) > 0 {
		log.Info("Existing certificate data found in status, skipping already completed CertificateRequest")
		return ctrl.Result{}, nil
	}

	// 4. Fetch the CMPv2Issuer resource
	issuer := cmpv2api.CMPv2Issuer{}
	issuerNamespaceName := types.NamespacedName{
		Namespace: k8sRequest.Namespace,
		Name:      certificateRequest.Spec.IssuerRef.Name,
	}
	if err := controller.Client.Get(ctx, issuerNamespaceName, &issuer); err != nil {
		controller.handleErrorGettingCMPv2Issuer(ctx, log, err, certificateRequest, issuerNamespaceName, k8sRequest)
		return ctrl.Result{}, err
	}

	// 5. Check if CMPv2Issuer is ready to sing certificates
	if !isCMPv2IssuerReady(issuer) {
		err := controller.handleErrorCMPv2IssuerIsNotReady(ctx, log, issuerNamespaceName, certificateRequest, k8sRequest)
		return ctrl.Result{}, err
	}

	// 6. Load the provisioner that will sign the CertificateRequest
	provisioner, ok := provisioners.Load(issuerNamespaceName)
	if !ok {
		err := controller.handleErrorCouldNotLoadCMPv2Provisioner(ctx, log, issuerNamespaceName, certificateRequest)
		return ctrl.Result{}, err
	}

	// 7. Get private key matching CertificateRequest
	privateKeySecretName := certificateRequest.ObjectMeta.Annotations[privateKeySecretNameAnnotation]
	privateKeySecretNamespaceName := types.NamespacedName{
		Namespace: k8sRequest.Namespace,
		Name:      privateKeySecretName,
	}
	var privateKeySecret core.Secret
	if err := controller.Client.Get(ctx, privateKeySecretNamespaceName, &privateKeySecret); err != nil {
		controller.handleErrorGettingPrivateKey(ctx, log, err, certificateRequest, privateKeySecretNamespaceName)
		return ctrl.Result{}, err
	}
	privateKeyBytes := privateKeySecret.Data[privateKeySecretKey]

	// 8. Decode CSR
	log.Info("Decoding CSR...")
	csr, err := x509utils.DecodeCSR(certificateRequest.Spec.Request)
	if err != nil {
		log.Error(err, "Cannot decode Certificate Signing Request")
		return ctrl.Result{}, err
	}

	// 9. Log Certificate Request properties not supported or overridden by CertService API
	logger.LogCertRequestProperties(ctrl.Log.WithName("CSR details"), certificateRequest, csr)

	// 10. Sign CertificateRequest
	signedPEM, trustedCAs, err := provisioner.Sign(ctx, certificateRequest, privateKeyBytes)
	if err != nil {
		controller.handleErrorFailedToSignCertificate(ctx, log, err, certificateRequest)
		return ctrl.Result{}, err
	}

	// 11. Store signed certificates in CertificateRequest
	certificateRequest.Status.Certificate = signedPEM
	certificateRequest.Status.CA = trustedCAs
	if err := controller.updateCertificateRequestWithSignedCerficates(ctx, certificateRequest); err != nil {
		return ctrl.Result{}, err
	}

	return ctrl.Result{}, nil
}

func (controller *CertificateRequestController) updateCertificateRequestWithSignedCerficates(ctx context.Context, certificateRequest *cmapi.CertificateRequest) error {
	return controller.setStatus(ctx, certificateRequest, cmmeta.ConditionTrue, cmapi.CertificateRequestReasonIssued, "Certificate issued")
}

func (controller *CertificateRequestController) SetupWithManager(manager ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(manager).
		For(&cmapi.CertificateRequest{}).
		Complete(controller)
}

func (controller *CertificateRequestController) setStatus(ctx context.Context, certificateRequest *cmapi.CertificateRequest, status cmmeta.ConditionStatus, reason, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	apiutil.SetCertificateRequestCondition(certificateRequest, cmapi.CertificateRequestConditionReady, status, reason, completeMessage)

	// Fire an Event to additionally inform users of the change
	eventType := core.EventTypeNormal
	if status == cmmeta.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	controller.Recorder.Event(certificateRequest, eventType, reason, completeMessage)

	return controller.Client.Status().Update(ctx, certificateRequest)
}

func isCMPv2IssuerReady(issuer cmpv2api.CMPv2Issuer) bool {
	condition := cmpv2api.CMPv2IssuerCondition{Type: cmpv2api.ConditionReady, Status: cmpv2api.ConditionTrue}
	return hasCondition(issuer, condition)
}

func hasCondition(issuer cmpv2api.CMPv2Issuer, condition cmpv2api.CMPv2IssuerCondition) bool {
	existingConditions := issuer.Status.Conditions
	for _, cond := range existingConditions {
		if condition.Type == cond.Type && condition.Status == cond.Status {
			return true
		}
	}
	return false
}

func isCMPv2CertificateRequest(certificateRequest *cmapi.CertificateRequest) bool {
	return certificateRequest.Spec.IssuerRef.Group != "" &&
		certificateRequest.Spec.IssuerRef.Group == cmpv2api.GroupVersion.Group &&
		certificateRequest.Spec.IssuerRef.Kind == cmpv2api.CMPv2IssuerKind

}

// Error handling

func (controller *CertificateRequestController) handleErrorCouldNotLoadCMPv2Provisioner(ctx context.Context, log logr.Logger, issuerNamespaceName types.NamespacedName, certificateRequest *cmapi.CertificateRequest) error {
	err := fmt.Errorf("provisioner %s not found", issuerNamespaceName)
	log.Error(err, "Failed to load CMPv2 Provisioner resource")
	_ = controller.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "Failed to load provisioner for CMPv2Issuer resource %s", issuerNamespaceName)
	return err
}

func (controller *CertificateRequestController) handleErrorCMPv2IssuerIsNotReady(ctx context.Context, log logr.Logger, issuerNamespaceName types.NamespacedName, certificateRequest *cmapi.CertificateRequest, req ctrl.Request) error {
	err := fmt.Errorf("resource %s is not ready", issuerNamespaceName)
	log.Error(err, "CMPv2Issuer not ready", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
	_ = controller.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "CMPv2Issuer resource %s is not Ready", issuerNamespaceName)
	return err
}

func (controller *CertificateRequestController) handleErrorGettingCMPv2Issuer(ctx context.Context, log logr.Logger, err error, certificateRequest *cmapi.CertificateRequest, issuerNamespaceName types.NamespacedName, req ctrl.Request) {
	log.Error(err, "Failed to retrieve CMPv2Issuer resource", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
	_ = controller.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "Failed to retrieve CMPv2Issuer resource %s: %v", issuerNamespaceName, err)
}

func (controller *CertificateRequestController) handleErrorGettingPrivateKey(ctx context.Context, log logr.Logger, err error, certificateRequest *cmapi.CertificateRequest, pkSecretNamespacedName types.NamespacedName) {
	log.Error(err, "Failed to retrieve private key secret for certificate request", "namespace", pkSecretNamespacedName.Namespace, "name", pkSecretNamespacedName.Name)
	_ = controller.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonPending, "Failed to retrieve private key secret: %v", err)
}

func (controller *CertificateRequestController) handleErrorFailedToSignCertificate(ctx context.Context, log logr.Logger, err error, certificateRequest *cmapi.CertificateRequest) {
	log.Error(err, "Failed to sign certificate request")
	_ = controller.setStatus(ctx, certificateRequest, cmmeta.ConditionFalse, cmapi.CertificateRequestReasonFailed, "Failed to sign certificate request: %v", err)
}

func handleErrorResourceNotFound(log logr.Logger, err error) error {
	if apierrors.IsNotFound(err) {
		log.Error(err, "CertificateRequest resource not found")
	} else {
		log.Error(err, "Failed to retrieve CertificateRequest resource")
	}
	return err
}
