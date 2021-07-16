package model

import (
	"context"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"

	"github.com/stretchr/testify/assert"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
	"testing"
)

const (
	revisionAnnotation                 = "cert-manager.io/certificate-revision"
	validUrl = "https://oom-cert-service:8443/"
)

func Test_shouldReturnCertificateModel(t *testing.T) {
	certificateRequest := new(cmapi.CertificateRequest)
	certificateRequest.Spec.Request = testdata.CsrBytes
	request := new(cmapi.CertificateRequest)
	request.ObjectMeta.Annotations = map[string]string{
		revisionAnnotation: "2",
	}
	ctx := new(context.Context)
	privateKeyBytes := testdata.PkBytes
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme())


	signCertModel, err := CreateSignCertificateModel(fakeClient, certificateRequest, *ctx, privateKeyBytes)

	assert.NotNil(t, signCertModel)
	assert.Nil(t, err)
	assert.Equal(t, privateKeyBytes, signCertModel.PrivateKeyBytes)
	assert.True(t, len(signCertModel.OldCertificateBytes) <=0)
	assert.True(t, len(signCertModel.OldPrivateKeyBytes) <=0)
}

func Test_shouldReturnErrorWhenCSRIsEmpty(t *testing.T) {
	certificateRequest := new(cmapi.CertificateRequest)
	ctx := new(context.Context)
	privateKeyBytes := []byte{1, 2, 8, 43}
	fakeClient := fake.NewFakeClientWithScheme(testdata.GetScheme())


	_, err := CreateSignCertificateModel(fakeClient, certificateRequest, *ctx, privateKeyBytes)

	assert.NotNil(t, err)
}
