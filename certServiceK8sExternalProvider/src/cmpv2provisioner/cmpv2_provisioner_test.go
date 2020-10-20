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

package cmpv2provisioner

import (
	"bytes"
	"context"
	"io/ioutil"
	"log"
	"testing"
	"time"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	apimach "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

const ISSUER_NAME = "cmpv2-issuer"
const ISSUER_URL = "issuer/url"
const KEY = "onapwro-key"
const CERT = "onapwro-cert"
const CACERT = "onapwro-cacert"
const ISSUER_NAMESPACE = "onap"

func Test_shouldCreateCorrectCertServiceCA(t *testing.T) {
	issuer, key, cert, cacert := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL, KEY, CERT, CACERT)
	provisioner, err := New(&issuer, key, cert, cacert)

	assert.Nil(t, err)
	assert.Equal(t, string(provisioner.key), string(key), "Unexpected provisioner key.")
	assert.Equal(t, string(provisioner.cert), string(cert), "Unexpected provisioner cert.")
	assert.Equal(t, string(provisioner.cacert), string(cacert), "Unexpected provisioner cacert.")
	assert.Equal(t, provisioner.name, issuer.Name, "Unexpected provisioner name.")
	assert.Equal(t, provisioner.url, issuer.Spec.URL, "Unexpected provisioner url.")
}

func Test_shouldSuccessfullyLoadPreviouslyStoredProvisioner(t *testing.T) {
	issuer, key, cert, cacert := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL, KEY, CERT, CACERT)
	provisioner, err := New(&issuer, key, cert, cacert)

	assert.Nil(t, err)

	issuerNamespaceName := createIssuerNamespaceName(ISSUER_NAMESPACE, ISSUER_NAME)

	Store(issuerNamespaceName, provisioner)
	provisioner, ok := Load(issuerNamespaceName)

	verifyThatConditionIsTrue(ok, "Provisioner could not be loaded.", t)
	assert.Equal(t, string(provisioner.key), string(key), "Unexpected provisioner key.")
	assert.Equal(t, string(provisioner.cert), string(cert), "Unexpected provisioner cert.")
	assert.Equal(t, string(provisioner.cacert), string(cacert), "Unexpected provisioner cacert.")
	assert.Equal(t, provisioner.name, issuer.Name, "Unexpected provisioner name.")
	assert.Equal(t, provisioner.url, issuer.Spec.URL, "Unexpected provisioner url.")
}

func Test_shouldReturnCorrectSignedPemsWhenParametersAreCorrect(t *testing.T) {
	const EXPECTED_SIGNED_FILENAME = "test_resources/expected_signed.pem"
	const EXPECTED_TRUSTED_FILENAME = "test_resources/expected_trusted.pem"

	issuer, key, cert, cacert := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL, KEY, CERT, CACERT)
	provisioner, err := New(&issuer, key, cert, cacert)

	issuerNamespaceName := createIssuerNamespaceName(ISSUER_NAMESPACE, ISSUER_NAME)
	Store(issuerNamespaceName, provisioner)

	provisioner, ok := Load(issuerNamespaceName)

	verifyThatConditionIsTrue(ok, "Provisioner could not be loaded", t)

	ctx := context.Background()
	request := createCertificateRequest()

	signedPEM, trustedCAs, err := provisioner.Sign(ctx, request)

	assert.Nil(t, err)

	verifyThatConditionIsTrue(areSlicesEqual(signedPEM, readFile(EXPECTED_SIGNED_FILENAME)), "Signed pem is different than expected.", t)
	verifyThatConditionIsTrue(areSlicesEqual(trustedCAs, readFile(EXPECTED_TRUSTED_FILENAME)), "Trusted CAs pem is different than expected.", t)
}

func verifyThatConditionIsTrue(cond bool, message string, t *testing.T) {
	if !cond {
		t.Fatal(message)
	}
}

func createIssuerNamespaceName(namespace string, name string) types.NamespacedName {
	return types.NamespacedName{
		Namespace: namespace,
		Name:      name,
	}
}

func createIssuerAndCerts(name string, url string, key string, cert string, cacert string) (cmpv2api.CMPv2Issuer, []byte, []byte, []byte) {
	issuer := cmpv2api.CMPv2Issuer{}
	issuer.Name = name
	issuer.Spec.URL = url
	return issuer, []byte(key), []byte(cert), []byte(cacert)
}

func readFile(filename string) []byte {
	certRequest, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}
	return certRequest
}

func createCertificateRequest() *cmapi.CertificateRequest {
	const CERTIFICATE_DURATION = "1h"
	const ISSUER_KIND = "CMPv2Issuer"
	const ISSUER_GROUP = "certmanager.onap.org"
	const CONDITION_TYPE = "Ready"

	const SPEC_REQUEST_FILENAME = "test_resources/test_certificate_request.pem"
	const STATUS_CERTIFICATE_FILENAME = "test_resources/test_certificate.pem"

	duration := new(apimach.Duration)
	d, _ := time.ParseDuration(CERTIFICATE_DURATION)
	duration.Duration = d

	request := new(cmapi.CertificateRequest)
	request.Spec.Duration = duration
	request.Spec.IssuerRef.Name = ISSUER_NAME
	request.Spec.IssuerRef.Kind = ISSUER_KIND
	request.Spec.IssuerRef.Group = ISSUER_GROUP
	request.Spec.Request = readFile(SPEC_REQUEST_FILENAME)
	request.Spec.IsCA = true

	cond := new(cmapi.CertificateRequestCondition)
	cond.Type = CONDITION_TYPE
	request.Status.Conditions = []cmapi.CertificateRequestCondition{*cond}
	request.Status.Certificate = readFile(STATUS_CERTIFICATE_FILENAME)

	return request
}

func areSlicesEqual(slice1 []byte, slice2 []byte) bool {
	return bytes.Compare(slice1, slice2) == 0
}
