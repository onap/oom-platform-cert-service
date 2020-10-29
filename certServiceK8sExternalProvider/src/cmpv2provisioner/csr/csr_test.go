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
	"crypto/x509"
	"encoding/pem"
	"testing"

	"github.com/stretchr/testify/assert"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner/csr/testdata"
)

func Test_FilterFieldsFromCSR_shouldFilterUnsupportedFields(t *testing.T) {
	filteredCsrBytes, _ := FilterFieldsFromCSR(testdata.CsrBytesWithNotSupportedFields, testdata.PrivateKeyBytes)

	assertNotFilteredFieldsNotChanged(t, testdata.CsrBytesWithNotSupportedFields, filteredCsrBytes)
	assertFilteredFieldsEmpty(t, filteredCsrBytes)
}

func Test_FilterFieldsFromCSR_shouldNotChangeCsrWithoutNotSupportedFields(t *testing.T) {
	filteredCsrBytes, _ := FilterFieldsFromCSR(testdata.CsrBytesWithoutNotSupportedFields, testdata.PrivateKeyBytes)

	assertNotFilteredFieldsNotChanged(t, testdata.CsrBytesWithoutNotSupportedFields, filteredCsrBytes)
	assertFilteredFieldsEmpty(t, filteredCsrBytes)
}

func Test_FilterFieldsFromCSR_shouldErrorWhenCsrPemCannotBeDecoded(t *testing.T) {
	_, err := FilterFieldsFromCSR([]byte(""), testdata.PrivateKeyBytes)

	assert.Error(t, err)
}

func Test_FilterFieldsFromCSR_shouldErrorWhenCsrCannotBeParsed(t *testing.T) {
	//Private Key used as CSR
	_, err := FilterFieldsFromCSR(testdata.PrivateKeyBytes, testdata.PrivateKeyBytes)

	assert.Error(t, err)
}

func Test_FilterFieldsFromCSR_shouldErrorWhenPkPemCannotBeDecoded(t *testing.T) {
	_, err := FilterFieldsFromCSR(testdata.CsrBytesWithNotSupportedFields, []byte(""))

	assert.Error(t, err)
}

func Test_FilterFieldsFromCSR_shouldErrorWhenPkCannotBeParsed(t *testing.T) {
	//CSR used as Private Key
	_, err := FilterFieldsFromCSR(testdata.CsrBytesWithNotSupportedFields, testdata.CsrBytesWithNotSupportedFields)

	assert.Error(t, err)
}

func assertNotFilteredFieldsNotChanged(t *testing.T, originalCsrBytes []byte, filteredCsrBytes []byte) {
	originalCsr := parseCsrBytes(originalCsrBytes)
	filteredCsr := parseCsrBytes(filteredCsrBytes)

	assert.Equal(t, originalCsr.DNSNames, filteredCsr.DNSNames)
	assert.Equal(t, originalCsr.PublicKey, filteredCsr.PublicKey)
	assert.Equal(t, originalCsr.PublicKeyAlgorithm, filteredCsr.PublicKeyAlgorithm)
	assert.Equal(t, originalCsr.SignatureAlgorithm, filteredCsr.SignatureAlgorithm)
	assert.Equal(t, originalCsr.Subject.CommonName, filteredCsr.Subject.CommonName)
	assert.Equal(t, originalCsr.Subject.Country, filteredCsr.Subject.Country)
	assert.Equal(t, originalCsr.Subject.Locality, filteredCsr.Subject.Locality)
	assert.Equal(t, originalCsr.Subject.Organization, filteredCsr.Subject.Organization)
	assert.Equal(t, originalCsr.Subject.OrganizationalUnit, filteredCsr.Subject.OrganizationalUnit)
	assert.Equal(t, originalCsr.Subject.Province, filteredCsr.Subject.Province)
}

func assertFilteredFieldsEmpty(t *testing.T, csrBytes []byte) {
	csr := parseCsrBytes(csrBytes)
	assert.Nil(t, csr.URIs)
	assert.Nil(t, csr.EmailAddresses)
	assert.Nil(t, csr.IPAddresses)
	assert.Nil(t, csr.Subject.PostalCode)
	assert.Equal(t, "", csr.Subject.SerialNumber)
	assert.Nil(t, csr.Subject.StreetAddress)
}

func parseCsrBytes(csrBytes []byte) *x509.CertificateRequest {
	decodedCsr, _ := pem.Decode(csrBytes)
	csr, _ := x509.ParseCertificateRequest(decodedCsr.Bytes)
	return csr
}
