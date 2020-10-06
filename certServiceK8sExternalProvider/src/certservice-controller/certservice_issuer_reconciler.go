/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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
	"github.com/go-logr/logr"
	core "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	"k8s.io/utils/clock"
	"onap.org/oom-certservice/k8s-external-provider/src/api"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/certservice-provisioner"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// CertServiceIssuerReconciler reconciles a CertServiceIssuer object
type CertServiceIssuerReconciler struct {
	client.Client
	Log      logr.Logger
	Clock    clock.Clock
	Recorder record.EventRecorder
}

// Reconcile will read and validate the CertServiceIssuer resources, it will set the
// status condition ready to true if everything is right.
func (r *CertServiceIssuerReconciler) Reconcile(req ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := r.Log.WithValues("certservice-issuer-controller", req.NamespacedName)

	iss := new(api.CertServiceIssuer)
	if err := r.Client.Get(ctx, req.NamespacedName, iss); err != nil {
		log.Error(err, "failed to retrieve CertServiceIssuer resource")
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	statusReconciler := newStatusReconciler(r, iss, log)
	if err := validateCertServiceIssuerSpec(iss.Spec); err != nil {
		log.Error(err, "failed to validate CertServiceIssuer resource")
		statusReconciler.UpdateNoError(ctx, api.ConditionFalse, "Validation", "Failed to validate resource: %v", err)
		return ctrl.Result{}, err
	}

	// Fetch the provisioner password
	var secret core.Secret
	secretNamespaceName := types.NamespacedName{
		Namespace: req.Namespace,
		Name:      iss.Spec.KeyRef.Name,
	}
	if err := r.Client.Get(ctx, secretNamespaceName, &secret); err != nil {
		log.Error(err, "failed to retrieve CertServiceIssuer provisioner secret", "namespace", secretNamespaceName.Namespace, "name", secretNamespaceName.Name)
		if apierrors.IsNotFound(err) {
			statusReconciler.UpdateNoError(ctx, api.ConditionFalse, "NotFound", "Failed to retrieve provisioner secret: %v", err)
		} else {
			statusReconciler.UpdateNoError(ctx, api.ConditionFalse, "Error", "Failed to retrieve provisioner secret: %v", err)
		}
		return ctrl.Result{}, err
	}
	password, ok := secret.Data[iss.Spec.KeyRef.Key]
	if !ok {
		err := fmt.Errorf("secret %s does not contain key %s", secret.Name, iss.Spec.KeyRef.Key)
		log.Error(err, "failed to retrieve CertServiceIssuer provisioner secret", "namespace", secretNamespaceName.Namespace, "name", secretNamespaceName.Name)
		statusReconciler.UpdateNoError(ctx, api.ConditionFalse, "NotFound", "Failed to retrieve provisioner secret: %v", err)
		return ctrl.Result{}, err
	}

	// Initialize and store the provisioner
	p, err := provisioners.New(iss, password)
	if err != nil {
		log.Error(err, "failed to initialize provisioner")
		statusReconciler.UpdateNoError(ctx, api.ConditionFalse, "Error", "failed initialize provisioner")
		return ctrl.Result{}, err
	}
	provisioners.Store(req.NamespacedName, p)

	return ctrl.Result{}, statusReconciler.Update(ctx, api.ConditionTrue, "Verified", "CertServiceIssuer verified and ready to sign certificates")
}

// SetupWithManager initializes the CertServiceIssuer controller into the controller
// runtime.
func (r *CertServiceIssuerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&api.CertServiceIssuer{}).
		Complete(r)
}

func validateCertServiceIssuerSpec(s api.CertServiceIssuerSpec) error {
	switch {
	case s.URL == "":
		return fmt.Errorf("spec.url cannot be empty")
	case s.KeyRef.Name == "":
		return fmt.Errorf("spec.keyRef.name cannot be empty")
	case s.KeyRef.Key == "":
		return fmt.Errorf("spec.keyRef.key cannot be empty")
	default:
		return nil
	}
}
