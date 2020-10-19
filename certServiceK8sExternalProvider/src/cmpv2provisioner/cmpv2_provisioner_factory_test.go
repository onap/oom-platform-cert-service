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
	"fmt"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	"log"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"testing"
)

const (
	secretName      = "issuer-cert-secret"
	url             = "https://oom-cert-service:8443/v1/certificate/"
	caName          = "RA"
	keySecretKey    = "cmpv2Issuer-key.pem"
	certSecretKey   = "cmpv2Issuer-cert.pem"
	cacertSecretKey = "cacert.pem"
)

var (
	keySecretValue    = []byte("keyData")
	certSecretValue   = []byte("certData")
	cacertSecretValue = []byte("cacertData")
)

func Test_shouldCreateProvisioner(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()

	provisioner, _ := CreateProvisioner(&issuer, secret)

	log.Println(provisioner)
	assert.NotNil(t, provisioner)
	assert.Equal(t, url, provisioner.url)
	assert.Equal(t, caName, provisioner.caName)
	assert.Equal(t, keySecretValue, provisioner.key)
	assert.Equal(t, certSecretValue, provisioner.cert)
	assert.Equal(t, cacertSecretValue, provisioner.cacert)
}

func Test_shouldReturnError_whenSecretMissingKeyRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, keySecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	log.Println(provisioner)
	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, keySecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCertRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, certSecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	log.Println(provisioner)
	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, certSecretKey), err)
	}
}

func Test_shouldReturnError_whenSecretMissingCacertRef(t *testing.T) {
	issuer, secret := getValidIssuerAndSecret()
	delete(secret.Data, cacertSecretKey)

	provisioner, err := CreateProvisioner(&issuer, secret)

	log.Println(provisioner)
	assert.Nil(t, provisioner)
	if assert.Error(t, err) {
		assert.Equal(t, fmt.Errorf("secret %s does not contain key %s", secretName, cacertSecretKey), err)
	}
}

func getValidIssuerAndSecret() (cmpv2api.CMPv2Issuer, v1.Secret) {
	issuer := cmpv2api.CMPv2Issuer{
		Spec: cmpv2api.CMPv2IssuerSpec{
			URL:    url,
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
			keySecretKey:    keySecretValue,
			certSecretKey:   certSecretValue,
			cacertSecretKey: cacertSecretValue,
		},
	}
	secret.Name = secretName
	return issuer, secret
}
