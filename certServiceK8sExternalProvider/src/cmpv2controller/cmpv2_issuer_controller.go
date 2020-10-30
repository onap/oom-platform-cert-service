/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (c) 2019 Smallstep Labs, Inc.
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

	core "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	"k8s.io/utils/clock"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
)

// CMPv2IssuerController reconciles a CMPv2Issuer object
type CMPv2IssuerController struct {
	client.Client
	Log      leveledlogger.LeveledLogger
	Clock    clock.Clock
	Recorder record.EventRecorder
	ProvisionerFactory provisioners.ProvisionerFactory
}

// Reconcile will read and validate the CMPv2Issuer resources, it will set the
// status condition ready to true if everything is right.
func (controller *CMPv2IssuerController) Reconcile(req ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := leveledlogger.GetLoggerWithValues("cmpv2-issuer-controller", req.NamespacedName)

	// 1. Load CMPv2Issuer
	issuer := new(cmpv2api.CMPv2Issuer)
	if err := controller.loadResource(ctx, req.NamespacedName, issuer); err != nil {
		handleErrorLoadingCMPv2Issuer(log, err)
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}
	log.Info("CMPv2Issuer loaded: ", "issuer", issuer)

	// 2. Validate CMPv2Issuer
	statusUpdater := newStatusUpdater(controller, issuer, log)
	if err := validateCMPv2IssuerSpec(issuer.Spec); err != nil {
		handleErrorCMPv2IssuerValidation(ctx, log, err, statusUpdater)
		return ctrl.Result{}, err
	}

	// 3. Load keystore and truststore information form k8s secret
	var secret core.Secret
	secretNamespaceName := types.NamespacedName{
		Namespace: req.Namespace,
		Name:      issuer.Spec.CertSecretRef.Name,
	}
	if err := controller.loadResource(ctx, secretNamespaceName, &secret); err != nil {
		handleErrorInvalidSecret(ctx, log, err, statusUpdater, secretNamespaceName)
		return ctrl.Result{}, err
	}

	// 4. Create CMPv2 provisioner
	provisioner, err := controller.ProvisionerFactory.CreateProvisioner(issuer, secret)
	if err != nil {
		log.Error(err, "failed to initialize provisioner")
		statusUpdater.UpdateNoError(ctx, cmpv2api.ConditionFalse, "Error", "Failed to initialize provisioner: %v", err)
		handleErrorProvisionerInitialization(ctx, log, err, statusUpdater)
		return ctrl.Result{}, err
	}

	// 5. Check health of the provisioner and store the instance for further use
	if err := provisioner.CheckHealth(); err != nil {
		return ctrl.Result{}, err
	}
	provisioners.Store(req.NamespacedName, provisioner)

	// 6. Update the status of CMPv2Issuer to 'Validated'
	if err := updateCMPv2IssuerStatusToVerified(statusUpdater, ctx, log); err != nil {
		handleErrorUpdatingCMPv2IssuerStatus(log, err)
		return ctrl.Result{}, err
	}

	return ctrl.Result{}, nil
}

func (controller *CMPv2IssuerController) SetupWithManager(manager ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(manager).
		For(&cmpv2api.CMPv2Issuer{}).
		Complete(controller)
}

func (controller *CMPv2IssuerController) loadResource(ctx context.Context, key client.ObjectKey, obj runtime.Object) error {
	return controller.Client.Get(ctx, key, obj)
}

func validateCMPv2IssuerSpec(issuerSpec cmpv2api.CMPv2IssuerSpec) error {
	switch {
	case issuerSpec.URL == "":
		return fmt.Errorf("spec.url cannot be empty")
	case issuerSpec.CaName == "":
		return fmt.Errorf("spec.caName cannot be empty")
	case issuerSpec.CertSecretRef.Name == "":
		return fmt.Errorf("spec.certSecretRef.name cannot be empty")
	case issuerSpec.CertSecretRef.KeyRef == "":
		return fmt.Errorf("spec.certSecretRef.keyRef cannot be empty")
	case issuerSpec.CertSecretRef.CertRef == "":
		return fmt.Errorf("spec.certSecretRef.certRef cannot be empty")
	case issuerSpec.CertSecretRef.CacertRef == "":
		return fmt.Errorf("spec.certSecretRef.cacertRef cannot be empty")
	default:
		return nil
	}
}

func updateCMPv2IssuerStatusToVerified(statusUpdater *CMPv2IssuerStatusUpdater, ctx context.Context, log leveledlogger.LeveledLogger) error {
	log.Info("CMPv2 provisioner created -> updating status to of CMPv2Issuer resource to: Verified")
	return statusUpdater.Update(ctx, cmpv2api.ConditionTrue, Verified, "CMPv2Issuer verified and ready to sign certificates")
}

// Error handling

func handleErrorUpdatingCMPv2IssuerStatus(log leveledlogger.LeveledLogger, err error) {
	log.Error(err, "Failed to update CMPv2Issuer status")
}

func handleErrorLoadingCMPv2Issuer(log leveledlogger.LeveledLogger, err error) {
	log.Error(err, "Failed to retrieve CMPv2Issuer resource")
}

func handleErrorProvisionerInitialization(ctx context.Context, log leveledlogger.LeveledLogger, err error, statusUpdater *CMPv2IssuerStatusUpdater) {
	log.Error(err, "Failed to initialize provisioner")
	statusUpdater.UpdateNoError(ctx, cmpv2api.ConditionFalse, Error, "Failed to initialize provisioner: %v", err)
}

func handleErrorCMPv2IssuerValidation(ctx context.Context, log leveledlogger.LeveledLogger, err error, statusUpdater *CMPv2IssuerStatusUpdater) {
	log.Error(err, "Failed to validate CMPv2Issuer resource")
	statusUpdater.UpdateNoError(ctx, cmpv2api.ConditionFalse, ValidationFailed, "Failed to validate resource: %v", err)
}

func handleErrorInvalidSecret(ctx context.Context, log leveledlogger.LeveledLogger, err error, statusUpdater *CMPv2IssuerStatusUpdater, secretNamespaceName types.NamespacedName) {
	log.Error(err, "Failed to retrieve CMPv2Issuer provisioner secret", "namespace", secretNamespaceName.Namespace, "name", secretNamespaceName.Name)
	if apierrors.IsNotFound(err) {
		statusUpdater.UpdateNoError(ctx, cmpv2api.ConditionFalse, NotFound, "Failed to retrieve provisioner secret: %v", err)
	} else {
		statusUpdater.UpdateNoError(ctx, cmpv2api.ConditionFalse, Error, "Failed to retrieve provisioner secret: %v", err)
	}
}
