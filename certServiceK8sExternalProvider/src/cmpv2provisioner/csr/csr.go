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

package csr

import (
	"crypto/rand"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"

	x509utils "onap.org/oom-certservice/k8s-external-provider/src/x509"
)

func FilterFieldsFromCSR(csrBytes []byte, privateKeyBytes []byte) ([]byte, error) {
	csr, err := x509utils.DecodeCSR(csrBytes)
	if err != nil {
		return nil, err
	}

	key, err := x509utils.DecodePrivateKey(privateKeyBytes)
	if err != nil {
		return nil, err
	}

	filteredSubject := filterFieldsFromSubject(csr.Subject)

	filteredCsr, err := x509.CreateCertificateRequest(rand.Reader, &x509.CertificateRequest{
		Subject:  filteredSubject,
		DNSNames: csr.DNSNames,
		IPAddresses: csr.IPAddresses,
		URIs: csr.URIs,
		EmailAddresses: csr.EmailAddresses,
	}, key)
	if err != nil {
		return nil, err
	}

	csrBytes = pem.EncodeToMemory(&pem.Block{Type: x509utils.PemCsrType, Bytes: filteredCsr})
	return csrBytes, nil
}

func filterFieldsFromSubject(subject pkix.Name) pkix.Name {
	subject.StreetAddress = []string{}
	subject.SerialNumber = ""
	subject.PostalCode = []string{}
	return subject
}
