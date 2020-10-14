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

package provisioners

import (
	"bytes"
	"context"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	certmanager "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"k8s.io/apimachinery/pkg/types"
	"onap.org/oom-certservice/k8s-external-provider/src/api"
	ctrl "sigs.k8s.io/controller-runtime"
	"sync"
)

var collection = new(sync.Map)

type CertServiceCA struct {
	name string
	url  string
	key  []byte
}

func New(certServiceIssuer *api.CertServiceIssuer, key []byte) (*CertServiceCA, error) {

	ca := CertServiceCA{}
	ca.name = certServiceIssuer.Name
	ca.url = certServiceIssuer.Spec.URL
	ca.key = key

	log := ctrl.Log.WithName("certservice-provisioner")
	log.Info("Configuring CA: ", "name", ca.name, "url", ca.url, "key", ca.key)

	return &ca, nil
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

func (ca *CertServiceCA) Sign(ctx context.Context, certificateRequest *certmanager.CertificateRequest) ([]byte, []byte, error) {
	log := ctrl.Log.WithName("certservice-provisioner")
	log.Info("Signing certificate: ", "cert-name", certificateRequest.Name)

	key, _ := base64.RawStdEncoding.DecodeString(string(ca.key))
	log.Info("CA: ", "name", ca.name, "url", ca.url, "key", key)

	crPEM := certificateRequest.Spec.Request
	csrBase64 := crPEM
	log.Info("Csr PEM: ", "bytes", csrBase64)

	csr, err := decodeCSR(crPEM)
	if err != nil {
		return nil, nil, err
	}

	cert := x509.Certificate{}
	cert.Raw = csr.Raw

	// TODO
	// write here code which will call CertServiceCA and sign CSR
	// END

	encodedPEM, err := encodeX509(&cert)
	if err != nil {
		return nil, nil, err
	}

	signedPEM := encodedPEM
	trustedCA := encodedPEM

	log.Info("Successfully signed: ", "cert-name", certificateRequest.Name)
	log.Info("Signed cert PEM: ", "bytes", signedPEM)
	log.Info("Trusted CA  PEM: ", "bytes", trustedCA)

	return signedPEM, trustedCA, nil
}

// TODO JM utility methods - will be used in "real" implementation

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

// generateSubject returns the first SAN that is not 127.0.0.1 or localhost. The
// CSRs generated by the Certificate resource have always those SANs. If no SANs
// are available `certservice-issuer-certificate` will be used as a subject is always
// required.
func generateSubject(sans []string) string {
	if len(sans) == 0 {
		return "certservice-issuer-certificate"
	}
	for _, s := range sans {
		if s != "127.0.0.1" && s != "localhost" {
			return s
		}
	}
	return sans[0]
}

func decode(cert string) []byte {
	bytes, _ := base64.RawStdEncoding.DecodeString(cert)
	return bytes
}
