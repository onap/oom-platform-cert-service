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
	"encoding/base64"
	"fmt"
	"testing"

	"github.com/stretchr/testify/assert"

	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

func Test_shouldCreateProvisioner(t *testing.T) {
	issuer, secret := testdata.GetValidIssuerWithSecret()
	provisionerFactory := ProvisionerFactoryImpl{}

	provisioner, _ := provisionerFactory.CreateProvisioner(&issuer, secret)

	assert.NotNil(t, provisioner)
	assert.Equal(t, testdata.Url, provisioner.url)
	assert.Equal(t, testdata.CaName, provisioner.caName)
	assert.Equal(t, testdata.HealthEndpoint, provisioner.healthEndpoint)
	assert.Equal(t, testdata.CertEndpoint, provisioner.certEndpoint)
}

func Test_shouldReturnError_whenSecretMissingKeyRef(t *testing.T) {
	issuer, secret := testdata.GetValidIssuerWithSecret()
	delete(secret.Data, testdata.KeySecretKey)
	provisionerFactory := ProvisionerFactoryImpl{}

	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", testdata.SecretName, testdata.KeySecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCertRef(t *testing.T) {
	issuer, secret := testdata.GetValidIssuerWithSecret()
	delete(secret.Data, testdata.CertSecretKey)
	provisionerFactory := ProvisionerFactoryImpl{}

	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", testdata.SecretName, testdata.CertSecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCacertRef(t *testing.T) {
	issuer, secret := testdata.GetValidIssuerWithSecret()
	delete(secret.Data, testdata.CacertSecretKey)
	provisionerFactory := ProvisionerFactoryImpl{}

	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", testdata.SecretName, testdata.CacertSecretKey), err)
	}
}

func Test_shouldReturnError_whenCreationOfCertServiceClientReturnsError(t *testing.T) {
	issuer, secret := testdata.GetValidIssuerWithSecret()
	invalidKeySecretValue, _ := base64.StdEncoding.DecodeString("")
	secret.Data[testdata.KeySecretKey] = invalidKeySecretValue
	provisionerFactory := ProvisionerFactoryImpl{}

	provisioner, err := provisionerFactory.CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	assert.Error(t, err)
}
