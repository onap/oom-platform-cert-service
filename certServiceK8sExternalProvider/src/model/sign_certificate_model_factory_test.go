/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package model

import (
	"context"
	"testing"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

const (
	revisionAnnotation                 = "cert-manager.io/certificate-revision"
	certificateConfigurationAnnotation = "kubectl.kubernetes.io/last-applied-configuration"
	testPrivateKeyData                 = "test-private-key"
	testCertificateData                = "test-certificate"
)

func Test_shouldCreateCertificateModelWithCorrectParameters(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation:                 "2",
		certificateConfigurationAnnotation: testdata.OldCertificateConfig,
	}
	request.Spec.Request = testdata.CsrBytes
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme(), testdata.GetValidCertificateSecret())

	signCertModel, err := CreateSignCertificateModel(fakeClient, request, *new(context.Context), testdata.PkBytes)

	assert.Nil(t, err)
	assert.NotNil(t, signCertModel)
	assert.NotNil(t, signCertModel.FilteredCsr)
	assert.Equal(t, testdata.PkBytes, signCertModel.PrivateKeyBytes)
	assert.Equal(t, request, signCertModel.CertificateRequest)
	assert.Equal(t, []byte(testCertificateData), signCertModel.OldCertificateBytes)
	assert.Equal(t, []byte(testPrivateKeyData), signCertModel.OldPrivateKeyBytes)
}
