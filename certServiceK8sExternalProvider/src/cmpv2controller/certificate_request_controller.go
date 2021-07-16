/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright 2019 The cert-manager authors.
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	core "k8s.io/api/core/v1"
	apierrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/logger"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/updater"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/util"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"onap.org/oom-certservice/k8s-external-provider/src/model"
	x509utils "onap.org/oom-certservice/k8s-external-provider/src/x509"
)

const (
	privateKeySecretNameAnnotation = "cert-manager.io/private-key-secret-name"
	privateKeySecretKey            = "tls.key"
)

// CertificateRequestController reconciles a CMPv2Issuer object.
type CertificateRequestController struct {
	Client   client.Client
	Recorder record.EventRecorder
	Log      leveledlogger.Logger
}

// Reconcile will read and validate a CMPv2Issuer resource associated to the
// CertificateRequest resource, and it will sign the CertificateRequest with the
// provisioner in the CMPv2Issuer.
func (controller *CertificateRequestController) Reconcile(k8sRequest ctrl.Request) (ctrl.Result, error) {
	ctx := context.Background()
	log := leveledlogger.GetLoggerWithValues("certificate-request-controller", k8sRequest.NamespacedName)

	// 1. Fetch the CertificateRequest resource being reconciled.
	certificateRequest := new(cmapi.CertificateRequest)
	certUpdater := updater.NewCertificateRequestUpdater(controller.Client, controller.Recorder, certificateRequest, ctx, log)

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
		controller.handleErrorGettingCMPv2Issuer(certUpdater, log, err, certificateRequest, issuerNamespaceName, k8sRequest)
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	// 5. Check if CMPv2Issuer is ready to sing certificates
	if !isCMPv2IssuerReady(issuer) {
		err := controller.handleErrorCMPv2IssuerIsNotReady(certUpdater, log, issuerNamespaceName, certificateRequest, k8sRequest)
		return ctrl.Result{}, err
	}

	// 6. Load the provisioner that will sign the CertificateRequest
	provisioner, ok := provisioners.Load(issuerNamespaceName)
	if !ok {
		err := controller.handleErrorCouldNotLoadCMPv2Provisioner(certUpdater, log, issuerNamespaceName)
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	// 7. Get private key matching CertificateRequest
	privateKeySecretName := certificateRequest.ObjectMeta.Annotations[privateKeySecretNameAnnotation]
	privateKeySecretNamespaceName := types.NamespacedName{
		Namespace: k8sRequest.Namespace,
		Name:      privateKeySecretName,
	}
	var privateKeySecret core.Secret
	if err := controller.Client.Get(ctx, privateKeySecretNamespaceName, &privateKeySecret); err != nil {
		controller.handleErrorGettingPrivateKey(certUpdater, log, err, privateKeySecretNamespaceName)
		return ctrl.Result{}, err
	}
	privateKeyBytes := privateKeySecret.Data[privateKeySecretKey]

	// 8. Decode CSR
	log.Info("Decoding CSR...")
	csr, err := x509utils.DecodeCSR(certificateRequest.Spec.Request)
	if err != nil {
		controller.handleErrorFailedToDecodeCSR(certUpdater, log, err)
		return ctrl.Result{}, err
	}

	// 9. Log Certificate Request properties not supported or overridden by CertService API
	logger.LogCertRequestProperties(leveledlogger.GetLoggerWithName("CSR details:"), certificateRequest, csr)

	// 10. Check if CertificateRequest is an update request
	isUpdateRevision, oldCertificate, oldPrivateKey := util.CheckIfCertificateUpdateAndRetrieveOldCertificateAndPk(
		controller.Client, certificateRequest, ctx)
	if isUpdateRevision {
		log.Info("Update revision detected")
	}
	signCertificateModel := model.SignCertificateModel{
		CertificateRequest: certificateRequest,
		PrivateKeyBytes:    privateKeyBytes,
		IsUpdateRevision:   isUpdateRevision,
		OldCertificate:     oldCertificate,
		OldPrivateKey:      oldPrivateKey,
	}

	// 11. Sign CertificateRequest
	signedPEM, trustedCAs, err := provisioner.Sign(ctx, signCertificateModel)
	if err != nil {
		controller.handleErrorFailedToSignCertificate(certUpdater, log, err)
		return ctrl.Result{}, nil
	}

	// 12. Store signed certificates in CertificateRequest
	certificateRequest.Status.Certificate = signedPEM
	certificateRequest.Status.CA = trustedCAs
	if err := certUpdater.UpdateCertificateRequestWithSignedCertificates(); err != nil {
		return ctrl.Result{}, err
	}

	return ctrl.Result{}, nil
}

func (controller *CertificateRequestController) SetupWithManager(manager ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(manager).
		For(&cmapi.CertificateRequest{}).
		Complete(controller)
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

func (controller *CertificateRequestController) handleErrorCouldNotLoadCMPv2Provisioner(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, issuerNamespaceName types.NamespacedName) error {
	err := fmt.Errorf("provisioner %s not found", issuerNamespaceName)
	log.Error(err, "Failed to load CMPv2 Provisioner resource")
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonPending, "Failed to load provisioner for CMPv2Issuer resource %s", issuerNamespaceName)
	return err
}

func (controller *CertificateRequestController) handleErrorCMPv2IssuerIsNotReady(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, issuerNamespaceName types.NamespacedName, certificateRequest *cmapi.CertificateRequest, req ctrl.Request) error {
	err := fmt.Errorf("resource %s is not ready", issuerNamespaceName)
	log.Error(err, "CMPv2Issuer not ready", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonPending, "CMPv2Issuer resource %s is not Ready", issuerNamespaceName)
	return err
}

func (controller *CertificateRequestController) handleErrorGettingCMPv2Issuer(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, err error, certificateRequest *cmapi.CertificateRequest, issuerNamespaceName types.NamespacedName, req ctrl.Request) {
	log.Error(err, "Failed to retrieve CMPv2Issuer resource", "namespace", req.Namespace, "name", certificateRequest.Spec.IssuerRef.Name)
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonPending, "Failed to retrieve CMPv2Issuer resource %s: %v", issuerNamespaceName, err)
}

func (controller *CertificateRequestController) handleErrorGettingPrivateKey(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, err error, pkSecretNamespacedName types.NamespacedName) {
	log.Error(err, "Failed to retrieve private key secret for certificate request", "namespace", pkSecretNamespacedName.Namespace, "name", pkSecretNamespacedName.Name)
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonPending, "Failed to retrieve private key secret: %v", err)
}

func (controller *CertificateRequestController) handleErrorFailedToSignCertificate(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, err error) {
	log.Error(err, "Failed to sign certificate request")
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonFailed, "Failed to sign certificate request: %v", err)
}

func (controller *CertificateRequestController) handleErrorFailedToDecodeCSR(updater *updater.CertificateRequestStatusUpdater, log leveledlogger.Logger, err error) {
	log.Error(err, "Failed to decode certificate sign request")
	_ = updater.UpdateStatusWithEventTypeWarning(cmapi.CertificateRequestReasonFailed, "Failed to decode CSR: %v", err)
}

func handleErrorResourceNotFound(log leveledlogger.Logger, err error) error {
	if apierrors.IsNotFound(err) {
		log.Error(err, "CertificateRequest resource not found")
		return nil
	} else {
		log.Error(err, "Failed to retrieve CertificateRequest resource")
		return err
	}
}
