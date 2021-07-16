/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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
	"testing"
	"time"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	apiv1 "k8s.io/api/core/v1"
	apimach "k8s.io/apimachinery/pkg/apis/meta/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/model"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

const ISSUER_NAME = "cmpv2-issuer"
const ISSUER_URL = "issuer/url"
const ISSUER_NAMESPACE = "onap"

func Test_shouldCreateCorrectCertServiceCA(t *testing.T) {
	issuer := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL)
	provisioner, err := New(&issuer, &certserviceclient.CertServiceClientMock{})

	assert.Nil(t, err)
	assert.Equal(t, provisioner.name, issuer.Name, "Unexpected provisioner name.")
	assert.Equal(t, provisioner.url, issuer.Spec.URL, "Unexpected provisioner url.")
}

func Test_shouldSuccessfullyLoadPreviouslyStoredProvisioner(t *testing.T) {
	issuer := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL)
	provisioner, err := New(&issuer, &certserviceclient.CertServiceClientMock{})

	assert.Nil(t, err)

	issuerNamespaceName := testdata.CreateIssuerNamespaceName(ISSUER_NAMESPACE, ISSUER_NAME)

	Store(issuerNamespaceName, provisioner)
	provisioner, ok := Load(issuerNamespaceName)

	testdata.VerifyThatConditionIsTrue(ok, "Provisioner could not be loaded.", t)
	assert.Equal(t, provisioner.name, issuer.Name, "Unexpected provisioner name.")
	assert.Equal(t, provisioner.url, issuer.Spec.URL, "Unexpected provisioner url.")
}

func Test_shouldReturnCorrectSignedPemsWhenParametersAreCorrectForCertificateRequest(t *testing.T) {
	issuer := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL)
	provisionerFactory := ProvisionerFactoryMock{}
	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, apiv1.Secret{})

	issuerNamespaceName := testdata.CreateIssuerNamespaceName(ISSUER_NAMESPACE, ISSUER_NAME)
	Store(issuerNamespaceName, provisioner)

	provisioner, ok := Load(issuerNamespaceName)

	testdata.VerifyThatConditionIsTrue(ok, "Provisioner could not be loaded", t)

	request := createCertificateRequest()
	privateKeyBytes := getPrivateKeyBytes()

	signCertificateModel := model.SignCertificateModel{
		CertificateRequest:  request,
		PrivateKeyBytes:     privateKeyBytes,
		OldCertificateBytes: []byte{},
		OldPrivateKeyBytes:  []byte{},
	}

	signedPEM, trustedCAs, err := provisioner.Sign(signCertificateModel)

	assert.Nil(t, err)

	testdata.VerifyCertsAreEqualToExpected(t, signedPEM, trustedCAs)
}

func Test_shouldReturnCorrectSignedPemsWhenParametersAreCorrectForUpdateCertificateRequest(t *testing.T) {
	issuer := createIssuerAndCerts(ISSUER_NAME, ISSUER_URL)
	provisionerFactory := ProvisionerFactoryMock{}
	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, apiv1.Secret{})

	issuerNamespaceName := testdata.CreateIssuerNamespaceName(ISSUER_NAMESPACE, ISSUER_NAME)
	Store(issuerNamespaceName, provisioner)

	provisioner, ok := Load(issuerNamespaceName)

	testdata.VerifyThatConditionIsTrue(ok, "Provisioner could not be loaded", t)

	request := createCertificateRequest()
	privateKeyBytes := getPrivateKeyBytes()

	signCertificateModel := model.SignCertificateModel{
		CertificateRequest:  request,
		PrivateKeyBytes:     privateKeyBytes,
		OldCertificateBytes: testdata.OldCertificateBytes,
		OldPrivateKeyBytes:  testdata.OldPrivateKeyBytes,
	}

	signedPEM, trustedCAs, err := provisioner.Sign(signCertificateModel)

	assert.Nil(t, err)

	testdata.VerifyCertsAreEqualToExpected(t, signedPEM, trustedCAs)
}

func createIssuerAndCerts(name string, url string) cmpv2api.CMPv2Issuer {
	issuer := cmpv2api.CMPv2Issuer{}
	issuer.Name = name
	issuer.Spec.URL = url
	return issuer
}

func createCertificateRequest() *cmapi.CertificateRequest {
	const CERTIFICATE_DURATION = "1h"
	const ISSUER_KIND = "CMPv2Issuer"
	const ISSUER_GROUP = "certmanager.onap.org"
	const CONDITION_TYPE = "Ready"

	const SPEC_REQUEST_FILENAME = "testdata/test_certificate_request.pem"
	const STATUS_CERTIFICATE_FILENAME = "testdata/test_certificate.pem"

	duration := new(apimach.Duration)
	d, _ := time.ParseDuration(CERTIFICATE_DURATION)
	duration.Duration = d

	request := new(cmapi.CertificateRequest)
	request.Spec.Duration = duration
	request.Spec.IssuerRef.Name = ISSUER_NAME
	request.Spec.IssuerRef.Kind = ISSUER_KIND
	request.Spec.IssuerRef.Group = ISSUER_GROUP
	request.Spec.Request = testdata.ReadFile(SPEC_REQUEST_FILENAME)
	request.Spec.IsCA = true

	cond := new(cmapi.CertificateRequestCondition)
	cond.Type = CONDITION_TYPE
	request.Status.Conditions = []cmapi.CertificateRequestCondition{*cond}
	request.Status.Certificate = testdata.ReadFile(STATUS_CERTIFICATE_FILENAME)

	return request
}

func getPrivateKeyBytes() []byte {
	return testdata.ReadFile("testdata/test_private_key.pem")
}
