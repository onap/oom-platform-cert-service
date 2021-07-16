/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package util

import (
	"fmt"
	"testing"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

const (
	oldCertificateConfig = "{\"apiVersion\":\"cert-manager.io/v1\",\"kind\":\"Certificate\",\"metadata\":{\"annotations\":{},\"name\":\"cert-test\",\"namespace\":\"onap\"},\"spec\":{\"commonName\":\"certissuer.onap.org\",\"dnsNames\":[\"localhost\",\"certissuer.onap.org\"],\"emailAddresses\":[\"onap@onap.org\"],\"ipAddresses\":[\"127.0.0.1\"],\"issuerRef\":{\"group\":\"certmanager.onap.org\",\"kind\":\"CMPv2Issuer\",\"name\":\"cmpv2-issuer-onap\"},\"secretName\":\"cert-test-secret-name\",\"subject\":{\"countries\":[\"US\"],\"localities\":[\"San-Francisco\"],\"organizationalUnits\":[\"ONAP\"],\"organizations\":[\"Linux-Foundation\"],\"provinces\":[\"California\"]},\"uris\":[\"onap://cluster.local/\"]}}\n"
	testPrivateKeyData   = "test-private-key"
	testCertificateData  = "test-certificate"
)

func Test_CheckIfCertificateUpdateAndRetrieveOldCertificateAndPk_revisionOne(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation: "2",
	}
	certificate, privateKey := RetrieveOldCertificateAndPkForCertificateUpdate(nil, request, nil)
	//assert.False(t, isUpdate)
	assert.Equal(t, []byte{}, certificate)
	assert.Equal(t, []byte{}, privateKey)
}

func Test_CheckIfCertificateUpdateAndRetrieveOldCertificateAndPk_revisionTwoSecretPresent(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation:                 "2",
		certificateConfigurationAnnotation: oldCertificateConfig,
	}
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme(), getValidCertificateSecret())
	certificate, privateKey := RetrieveOldCertificateAndPkForCertificateUpdate(fakeClient, request, nil)
	//assert.True(t, isUpdate)
	assert.Equal(t, []byte(testCertificateData), certificate)
	assert.Equal(t, []byte(testPrivateKeyData), privateKey)
}

func Test_CheckIfCertificateUpdateAndRetrieveOldCertificateAndPk_revisionTwoSecretNotPresent(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation:                 "2",
		certificateConfigurationAnnotation: oldCertificateConfig,
	}
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme())
	certificate, privateKey := RetrieveOldCertificateAndPkForCertificateUpdate(fakeClient, request, nil)
	//assert.False(t, isUpdate)
	assert.Equal(t, []byte{}, certificate)
	assert.Equal(t, []byte{}, privateKey)
}

func Test_IsUpdateCertificateRevision(t *testing.T) {
	parameters := []struct {
		revision string
		expected bool
	}{
		{"1", false},
		{"2", true},
		{"invalid", false},
	}

	for _, parameter := range parameters {
		testName := fmt.Sprintf("Expected:%v for revision=%v", parameter.expected, parameter.revision)
		t.Run(testName, func(t *testing.T) {
			testIsUpdateCertificateRevision(t, parameter.revision, parameter.expected)
		})
	}
}

func testIsUpdateCertificateRevision(t *testing.T, revision string, expected bool) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation: revision,
	}
	assert.Equal(t, expected, IsUpdateCertificateRevision(request))
}

func Test_RetrieveOldCertificateAndPk_shouldSucceedWhenSecretPresent(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		certificateConfigurationAnnotation: oldCertificateConfig,
	}
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme(), getValidCertificateSecret())
	certificate, privateKey := RetrieveOldCertificateAndPk(fakeClient, request, nil)
	assert.Equal(t, []byte(testCertificateData), certificate)
	assert.Equal(t, []byte(testPrivateKeyData), privateKey)
}

func Test_RetrieveOldCertificateAndPk_shouldBeEmptyWhenSecretNotPresent(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		certificateConfigurationAnnotation: oldCertificateConfig,
	}
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme())
	certificate, privateKey := RetrieveOldCertificateAndPk(fakeClient, request, nil)
	assert.Equal(t, []byte{}, certificate)
	assert.Equal(t, []byte{}, privateKey)
}

func Test_RetrieveOldCertificateAndPk_shouldBeEmptyWhenOldCertificateCannotBeUnmarshalled(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme())
	certificate, privateKey := RetrieveOldCertificateAndPk(fakeClient, request, nil)
	assert.Equal(t, []byte{}, certificate)
	assert.Equal(t, []byte{}, privateKey)
}

func getValidCertificateSecret() *v1.Secret {
	const privateKeySecretKey = "tls.key"
	const certificateSecretKey = "tls.crt"

	return &v1.Secret{
		Data: map[string][]byte{
			privateKeySecretKey:  []byte("test-private-key"),
			certificateSecretKey: []byte("test-certificate"),
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      "cert-test-secret-name",
			Namespace: "onap",
		},
	}
}
