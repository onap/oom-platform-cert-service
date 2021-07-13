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

package certserviceclient

import (
	"testing"

	"github.com/stretchr/testify/assert"

	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

const (
	validUrl                 = "https://oom-cert-service:8443/"
	validUrl2                = "https://oom-cert-service:8443"
	invalidUrl               = "https://oom-cert  service:8443/"
	healthEndpoint           = "actuator/health"
	healthEndpointInvalid    = ":/actuator/health"
	certEndpoint             = "v1/certificate"
	updateEndpoint           = "v1/certificate-update"
	certEndpointInvalid      = ":/v1/certificate"
	caName                   = "RA"
	caNameInvalid            = ":/RA"
	expectedCertificationUrl = "https://oom-cert-service:8443/v1/certificate/RA"
	expectedHealthCheckUrl   = "https://oom-cert-service:8443/actuator/health"
)

func Test_shouldCreateCertServiceClient(t *testing.T) {
	shouldCreateCertServiceClientWithExpectedUrl(t, validUrl)
	shouldCreateCertServiceClientWithExpectedUrl(t, validUrl2)
}

func shouldCreateCertServiceClientWithExpectedUrl(t *testing.T, baseUrl string) {
	client, err := CreateCertServiceClient(baseUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.NotNil(t, client)
	assert.Nil(t, err)
	assert.Equal(t, expectedCertificationUrl, client.certificationUrl)
	assert.Equal(t, expectedHealthCheckUrl, client.healthUrl)
}

func Test_shouldReturnError_whenCaNameInvalid(t *testing.T) {
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, caNameInvalid, testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenHealthEndpointInvalid(t *testing.T) {
	client, err := CreateCertServiceClient(validUrl, healthEndpointInvalid, certEndpoint, updateEndpoint, caName, testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenCertEndpointInvalid(t *testing.T) {
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpointInvalid, updateEndpoint, caName, testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenUrlInvalid(t *testing.T) {
	client, err := CreateCertServiceClient(invalidUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenCanameEmpty(t *testing.T) {
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, "", testdata.KeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenKeyNotMatchingCert(t *testing.T) {
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.NotMatchingKeyBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenKeyInvalid(t *testing.T) {
	//Cert used as key
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.CertBytes, testdata.CertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenCertInvalid(t *testing.T) {
	//Cacert used as cert
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.KeyBytes, testdata.CacertBytes, testdata.CacertBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenCacertInvalid(t *testing.T) {
	//Key used as cacert
	client, err := CreateCertServiceClient(validUrl, healthEndpoint, certEndpoint, updateEndpoint, caName, testdata.KeyBytes, testdata.CertBytes, testdata.KeyBytes)

	assert.Nil(t, client)
	assert.Error(t, err)
}
