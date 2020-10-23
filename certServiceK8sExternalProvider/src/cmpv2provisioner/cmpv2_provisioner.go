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

package cmpv2provisioner

import (
	"bytes"
	"context"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"sync"

	certmanager "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"k8s.io/apimachinery/pkg/types"
	ctrl "sigs.k8s.io/controller-runtime"

	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

var collection = new(sync.Map)

type CertServiceCA struct {
	name              string
	url               string
	healthEndpoint    string
	certEndpoint      string
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
	ca.certServiceClient = certServiceClient

	log := ctrl.Log.WithName("cmpv2-provisioner")
	log.Info("Configuring CA: ", "name", ca.name, "url", ca.url, "caName", ca.caName, "healthEndpoint", ca.healthEndpoint, "certEndpoint", ca.certEndpoint)

	return &ca, nil
}

func (ca *CertServiceCA) CheckHealth() error {
	log := ctrl.Log.WithName("cmpv2-provisioner")
	log.Info("Checking health of CA: ", "name", ca.name)
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

func (ca *CertServiceCA) Sign(ctx context.Context, certificateRequest *certmanager.CertificateRequest, privateKeyBytes []byte) ([]byte, []byte, error) {
	log := ctrl.Log.WithName("certservice-provisioner")
	log.Info("Signing certificate: ", "cert-name", certificateRequest.Name)

	log.Info("CA: ", "name", ca.name, "url", ca.url)

	csrBytes := certificateRequest.Spec.Request
	log.Info("Csr PEM: ", "bytes", csrBytes)

	csr, err := decodeCSR(csrBytes)
	if err != nil {
		return nil, nil, err
	}

	response, err := ca.certServiceClient.GetCertificates(csrBytes, privateKeyBytes)
	if err != nil {
		return nil, nil, err
	}
	log.Info("Certificate Chain", "cert-chain", response.CertificateChain)
	log.Info("Trusted Certificates", "trust-certs", response.TrustedCertificates)


	// TODO
	// stored response as PEM
	cert := x509.Certificate{}
	cert.Raw = csr.Raw
	encodedPEM, err := encodeX509(&cert)
	if err != nil {
		return nil, nil, err
	}
	// END

	signedPEM := encodedPEM
	trustedCA := encodedPEM

	log.Info("Signed cert PEM: ", "bytes", signedPEM)
	log.Info("Trusted CA  PEM: ", "bytes", trustedCA)
	log.Info("Successfully signed: ", "cert-name", certificateRequest.Name)

	return signedPEM, trustedCA, nil
}

// decodeCSR decodes a certificate request in PEM format and returns the
func decodeCSR(data []byte) (*x509.CertificateRequest, error) {
	block, rest := pem.Decode(data)
	if block == nil || len(rest) > 0 {
		return nil, fmt.Errorf("unexpected CSR PEM on sign request")
	}
	if block.Type != "CERTIFICATE REQUEST" {
		return nil, fmt.Errorf("PEM is not a certificate request")
	}
	csr, err := x509.ParseCertificateRequest(block.Bytes)
	if err != nil {
		return nil, fmt.Errorf("error parsing certificate request: %v", err)
	}
	if err := csr.CheckSignature(); err != nil {
		return nil, fmt.Errorf("error checking certificate request signature: %v", err)
	}
	return csr, nil
}

// encodeX509 will encode a *x509.Certificate into PEM format.
func encodeX509(cert *x509.Certificate) ([]byte, error) {
	caPem := bytes.NewBuffer([]byte{})
	err := pem.Encode(caPem, &pem.Block{Type: "CERTIFICATE", Bytes: cert.Raw})
	if err != nil {
		return nil, err
	}
	return caPem.Bytes(), nil
}
