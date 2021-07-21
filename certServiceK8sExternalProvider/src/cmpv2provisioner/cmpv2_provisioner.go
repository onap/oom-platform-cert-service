/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (c) 2019 Smallstep Labs, Inc.
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

package cmpv2provisioner

import (
	"sync"

	"k8s.io/apimachinery/pkg/types"

	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"onap.org/oom-certservice/k8s-external-provider/src/model"
)

var collection = new(sync.Map)

type CertServiceCA struct {
	name              string
	url               string
	healthEndpoint    string
	certEndpoint      string
	updateEndpoint    string
	caName            string
	certServiceClient certserviceclient.CertServiceClient
}

func New(cmpv2Issuer *cmpv2api.CMPv2Issuer, certServiceClient certserviceclient.CertServiceClient) (*CertServiceCA, error) {

	ca := CertServiceCA{}
	ca.name = cmpv2Issuer.Name
	ca.url = cmpv2Issuer.Spec.URL
	ca.caName = cmpv2Issuer.Spec.CaName
	ca.healthEndpoint = cmpv2Issuer.Spec.HealthEndpoint
	ca.certEndpoint = cmpv2Issuer.Spec.CertEndpoint
	ca.updateEndpoint = cmpv2Issuer.Spec.UpdateEndpoint
	ca.certServiceClient = certServiceClient

	log := leveledlogger.GetLoggerWithName("cmpv2-provisioner")
	log.Info("Configuring CA: ", "name", ca.name, "url", ca.url, "caName", ca.caName, "healthEndpoint", ca.healthEndpoint, "certEndpoint", ca.certEndpoint, "updateEndpoint", ca.updateEndpoint)

	return &ca, nil
}

func (ca *CertServiceCA) CheckHealth() error {
	log := leveledlogger.GetLoggerWithName("cmpv2-provisioner")
	log.Info("Checking health of CMPv2 issuer: ", "name", ca.name)
	return ca.certServiceClient.CheckHealth()
}

func Load(namespacedName types.NamespacedName) (*CertServiceCA, bool) {
	provisioner, ok := collection.Load(namespacedName)
	if !ok {
		return nil, ok
	}
	certServiceCAprovisioner, ok := provisioner.(*CertServiceCA)
	return certServiceCAprovisioner, ok
}

func Store(namespacedName types.NamespacedName, provisioner *CertServiceCA) {
	collection.Store(namespacedName, provisioner)
}

func (ca *CertServiceCA) Sign(
	signCertificateModel model.SignCertificateModel,
) (signedCertificateChain []byte, trustedCertificates []byte, err error) {
	log := leveledlogger.GetLoggerWithName("certservice-provisioner")

	certificateRequest := signCertificateModel.CertificateRequest
	log.Info("Signing certificate: ", "cert-name", certificateRequest.Name)
	log.Info("CA: ", "name", ca.name, "url", ca.url)

	var response *certserviceclient.CertificatesResponse
	var errAPI error
	if ca.isCertificateUpdate(signCertificateModel) {
		log.Debug("Certificate will be updated.", "old-certificate", signCertificateModel.OldCertificateBytes)
		log.Info("Attempt to send certificate update request")
		response, errAPI = ca.certServiceClient.UpdateCertificate(signCertificateModel)
	} else {
		log.Info("Attempt to send certificate request")
		response, errAPI = ca.certServiceClient.GetCertificates(signCertificateModel)
	}

	if errAPI != nil {
		return nil, nil, errAPI
	}
	log.Info("Successfully received response from CertService API")
	log.Debug("Certificate Chain", "cert-chain", response.CertificateChain)
	log.Debug("Trusted Certificates", "trust-certs", response.TrustedCertificates)

	log.Info("Start parsing response")
	signedCertificateChain, trustedCertificates, signErr := parseResponseToBytes(response)

	if signErr != nil {
		log.Error(signErr, "Cannot parse response from CertService API")
		return nil, nil, signErr
	}
	log.Info("Successfully signed: ", "cert-name", certificateRequest.Name)
	log.Debug("Signed cert PEM: ", "bytes", signedCertificateChain)
	log.Debug("Trusted CA  PEM: ", "bytes", trustedCertificates)

	return signedCertificateChain, trustedCertificates, nil
}

func (ca *CertServiceCA) updateEndpointIsConfigured() bool {
	log := leveledlogger.GetLoggerWithName("certservice-provisioner")
	isConfigured := ca.updateEndpoint != ""
	if !isConfigured {
		log.Info("Missing 'update endpoint' configuration. Certificates will received by certificate request instead of certificate update request")
	}
	return isConfigured
}

func (ca *CertServiceCA) isCertificateUpdate(signCertificateModel model.SignCertificateModel) bool {
	return len(signCertificateModel.OldCertificateBytes) > 0 &&
		len(signCertificateModel.OldPrivateKeyBytes) > 0 &&
		ca.updateEndpointIsConfigured()
}
