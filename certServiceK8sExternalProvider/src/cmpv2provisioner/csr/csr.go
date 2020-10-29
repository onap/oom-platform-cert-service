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
	"fmt"
)

func FilterFieldsFromCSR(csrBytes []byte, privateKeyBytes []byte) ([]byte, error) {
	decodedCsr, _ := pem.Decode(csrBytes)
	if decodedCsr == nil {
		return nil, fmt.Errorf("can't decode CSR PEM")
	}
	csr, err := x509.ParseCertificateRequest(decodedCsr.Bytes)
	if err != nil {
		return nil, err
	}

	decodedPrivateKey, _ := pem.Decode(privateKeyBytes)
	if decodedPrivateKey == nil {
		return nil, fmt.Errorf("can't decode Private Key PEM")
	}
	key, err := x509.ParsePKCS8PrivateKey(decodedPrivateKey.Bytes)
	if err != nil {
		return nil, err
	}

	filteredSubject := filterFieldsFromSubject(csr.Subject)

	filteredCsr, err := x509.CreateCertificateRequest(rand.Reader, &x509.CertificateRequest{
		Subject:  filteredSubject,
		DNSNames: csr.DNSNames,
	}, key)
	if err != nil {
		return nil, err
	}

	csrBytes = pem.EncodeToMemory(&pem.Block{Type: decodedCsr.Type, Bytes: filteredCsr})
	return csrBytes, nil
}

func filterFieldsFromSubject(subject pkix.Name) pkix.Name {
	subject.StreetAddress = []string{}
	subject.SerialNumber = ""
	subject.PostalCode = []string{}
	return subject
}
