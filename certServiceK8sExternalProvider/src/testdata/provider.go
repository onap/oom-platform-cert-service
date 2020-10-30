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

package testdata

import (
	"k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

const (
	SecretName       = "issuer-cert-secret"
	Url              = "https://oom-cert-service:8443/v1/certificate/"
	HealthEndpoint   = "actuator/health"
	CertEndpoint     = "v1/certificate"
	CaName           = "RA"
	KeySecretKey     = "cmpv2Issuer-key.pem"
	CertSecretKey    = "cmpv2Issuer-cert.pem"
	CacertSecretKey  = "cacert.pem"
	Namespace        = "default"
	IssuerObjectName = "fakeIssuer"
	Kind             = "CMPv2Issuer"
	APIVersion       = "v1"
)

func GetValidIssuerWithSecret() (cmpv2api.CMPv2Issuer, v1.Secret) {
	issuer := cmpv2api.CMPv2Issuer{

		TypeMeta: metav1.TypeMeta{
			APIVersion: APIVersion,
			Kind:       Kind,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      IssuerObjectName,
			Namespace: Namespace,
		},
		Spec: GetValidCMPv2IssuerSpec(),
	}
	secret := v1.Secret{

		Data: map[string][]byte{
			KeySecretKey:    KeyBytes,
			CertSecretKey:   CertBytes,
			CacertSecretKey: CacertBytes,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      SecretName,
			Namespace: Namespace,
		},
	}
	secret.Name = SecretName
	return issuer, secret
}

func GetValidCMPv2IssuerSpec() cmpv2api.CMPv2IssuerSpec {
	issuerSpec := cmpv2api.CMPv2IssuerSpec{
		URL:            Url,
		HealthEndpoint: HealthEndpoint,
		CertEndpoint:   CertEndpoint,
		CaName:         CaName,
		CertSecretRef: cmpv2api.SecretKeySelector{
			Name:      SecretName,
			KeyRef:    KeySecretKey,
			CertRef:   CertSecretKey,
			CacertRef: CacertSecretKey,
		},
	}
	return issuerSpec
}

