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
	v1 "k8s.io/api/core/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

const (
	secretName      = "issuer-cert-secret"
	url             = "https://oom-cert-service:8443/v1/certificate/"
	healthEndpoint  = "actuator/health"
	certEndpoint    = "v1/certificate"
	caName          = "RA"
	keySecretKey    = "cmpv2Issuer-key.pem"
	certSecretKey   = "cmpv2Issuer-cert.pem"
	cacertSecretKey = "cacert.pem"
)

func Test_shouldCreateProvisioner(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()

	provisioner, _ := CreateProvisioner(&issuer, secret)

	assert.NotNil(t, provisioner)
	assert.Equal(t, url, provisioner.url)
	assert.Equal(t, caName, provisioner.caName)
	assert.Equal(t, healthEndpoint, provisioner.healthEndpoint)
	assert.Equal(t, certEndpoint, provisioner.certEndpoint)
}

func Test_shouldReturnError_whenSecretMissingKeyRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, keySecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, keySecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCertRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, certSecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, certSecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCacertRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, cacertSecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, cacertSecretKey), err)
	}
}

func Test_shouldReturnError_whenCreationOfCertServiceClientReturnsError(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	invalidKeySecretValue, _    := base64.StdEncoding.DecodeString("")
	secret.Data[keySecretKey] = invalidKeySecretValue

	provisioner, err := CreateProvisioner(&issuer, secret)

	assert.Nil(t, provisioner)
	assert.Error(t, err)
}

func getValidIssuerAndSecret() (cmpv2api.CMPv2Issuer, v1.Secret) {
	issuer := cmpv2api.CMPv2Issuer{
		Spec: cmpv2api.CMPv2IssuerSpec{
			URL:    url,
			HealthEndpoint: healthEndpoint,
			CertEndpoint: certEndpoint,
			CaName: caName,
			CertSecretRef: cmpv2api.SecretKeySelector{
				Name:      secretName,
				KeyRef:    keySecretKey,
				CertRef:   certSecretKey,
				CacertRef: cacertSecretKey,
			},
		},
	}
	secret := v1.Secret{

		Data: map[string][]byte{
			keySecretKey:    testdata.KeyBytes,
			certSecretKey:   testdata.CertBytes,
			cacertSecretKey: testdata.CacertBytes,
		},
	}
	secret.Name = secretName
	return issuer, secret
}
