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

package cmpv2controller

import (
	"testing"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
)

const group = "certmanager.onap.org"

func Test_shouldBeInvalidCMPv2CertificateRequest_whenEmpty(t *testing.T) {
	request := new(cmapi.CertificateRequest)

	assert.False(t, isCMPv2CertificateRequest(request))
}

func Test_shouldBeInvalidCMPv2CertificateRequest_whenKindIsCertificateRequest(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.Spec.IssuerRef.Group = group
	request.Spec.IssuerRef.Kind = "CertificateRequest"

	assert.False(t, isCMPv2CertificateRequest(request))
}

func Test_shouldBeValidCMPv2CertificateRequest_whenKindIsCMPvIssuer(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.Spec.IssuerRef.Group = group
	request.Spec.IssuerRef.Kind = "CMPv2Issuer"

	assert.True(t, isCMPv2CertificateRequest(request))
}
