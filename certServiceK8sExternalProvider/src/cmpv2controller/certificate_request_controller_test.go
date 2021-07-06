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
	"context"
	"testing"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner"
	provisionersdata "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner/csr/testdata"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
	x509 "onap.org/oom-certservice/k8s-external-provider/src/x509/testdata"
)

const (
	group                  = "certmanager.onap.org"
	certificateRequestName = "testRequest"
	recorderBufferSize     = 3
)

func Test_shouldSaveCorrectSignedPems_whenRequestReceived(t *testing.T) {
	verifiedIssuer := getVerifiedIssuer()
	createProvisioner(verifiedIssuer)
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme(), &verifiedIssuer,
		getValidCertificateRequest(), getValidPrivateKeySecret())

	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)
	controller := getCertRequestController(fakeRecorder, fakeClient)
	fakeRequest := testdata.GetFakeRequest(certificateRequestName)

	res, err := controller.Reconcile(fakeRequest)

	signedPEM, trustedCAs := getCertificates(controller, fakeRequest.NamespacedName)
	assert.Nil(t, err)
	assert.NotNil(t, res)
	assert.Equal(t, <-fakeRecorder.Events, "Normal Issued Certificate issued")
	testdata.VerifyCertsAreEqualToExpected(t, signedPEM, trustedCAs)
	clearProvisioner()
}

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

func getCertificates(controller CertificateRequestController, namespacedName types.NamespacedName) ([]byte, []byte) {
	certificateRequest := new(cmapi.CertificateRequest)
	_ = controller.Client.Get(context.Background(), namespacedName, certificateRequest)

	signedPEM := certificateRequest.Status.Certificate
	trustedCAs := certificateRequest.Status.CA

	return signedPEM, trustedCAs
}

func getValidPrivateKeySecret() *v1.Secret {
	const privateKeySecretKey = "tls.key"

	return &v1.Secret{
		Data: map[string][]byte{
			privateKeySecretKey: provisionersdata.PrivateKeyBytes,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      testdata.PrivateKeySecret,
			Namespace: testdata.Namespace,
		},
	}
}

func getValidCertificateRequest() *cmapi.CertificateRequest {
	return &cmapi.CertificateRequest{
		TypeMeta: metav1.TypeMeta{
			Kind:       "",
			APIVersion: testdata.APIVersion,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      certificateRequestName,
			Namespace: testdata.Namespace,
			Annotations: map[string]string{
				privateKeySecretNameAnnotation: testdata.PrivateKeySecret,
			},
		},

		Spec: cmapi.CertificateRequestSpec{
			IssuerRef: cmmeta.ObjectReference{
				Group: cmpv2api.GroupVersion.Group,
				Kind:  cmpv2api.CMPv2IssuerKind,
				Name:  testdata.IssuerObjectName,
			},
			Request: []byte(x509.ValidCertificateSignRequest),
		},
	}
}

func getCertRequestController(fakeRecorder *record.FakeRecorder, fakeClient client.Client) CertificateRequestController {
	controller := CertificateRequestController{
		Client:   fakeClient,
		Log:      leveledlogger.GetLoggerWithValues("controllers", "CertificateRequest"),
		Recorder: fakeRecorder,
	}
	return controller
}

func getVerifiedIssuer() cmpv2api.CMPv2Issuer {
	issuer, _ := testdata.GetValidIssuerWithSecret()
	issuer.Status = cmpv2api.CMPv2IssuerStatus{
		Conditions: []cmpv2api.CMPv2IssuerCondition{{
			Type:   cmpv2api.ConditionReady,
			Status: cmpv2api.ConditionTrue}},
	}
	return issuer
}

func createProvisioner(verifiedIssuer cmpv2api.CMPv2Issuer) {
	provisionerFactory := provisioners.ProvisionerFactoryMock{}
	fakeProvisioner, _ := provisionerFactory.CreateProvisioner(&verifiedIssuer, v1.Secret{})

	provisioners.Store(testdata.GetIssuerStoreKey(), fakeProvisioner)
}

func clearProvisioner() {
	provisioners.Store(testdata.GetIssuerStoreKey(), nil)
}
