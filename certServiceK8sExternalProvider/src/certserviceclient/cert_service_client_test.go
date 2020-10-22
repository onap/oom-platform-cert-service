package certserviceclient

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"

	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

const (
	certificationUrl = "https://oom-cert-service:8443/v1/certificate/RA"
)


func Test_shouldParseCertificateResponseCorrectly(t *testing.T) {
	responseJson := `{"certificateChain": ["cert-0", "cert-1"], "trustedCertificates": ["trusted-cert-0", "trusted-cert-1"]}`
	responseJsonReader := ioutil.NopCloser(bytes.NewReader([]byte(responseJson)))
	client := CertServiceClient{
		certificationUrl: certificationUrl,
		httpClient:       &httpClientMock{
			DoFunc: func(req *http.Request) (response *http.Response, e error) {
				mockedResponse := &http.Response{
					Body: responseJsonReader,
				}
				return mockedResponse, nil
			},
		},
	}
	response, _ := client.GetCertificates(testdata.CsrBytes, testdata.PkBytes)
	assert.ElementsMatch(t, []string{"cert-0", "cert-1"}, response.CertificateChain)
	assert.ElementsMatch(t, []string{"trusted-cert-0", "trusted-cert-1"}, response.TrustedCertificates)
}

func Test_shouldReturnError_whenResponseIsNotJson(t *testing.T) {
	responseJson := `not a json`
	responseJsonReader := ioutil.NopCloser(bytes.NewReader([]byte(responseJson)))
	client := CertServiceClient{
		certificationUrl: certificationUrl,
		httpClient:       &httpClientMock{
			DoFunc: func(req *http.Request) (response *http.Response, e error) {
				mockedResponse := &http.Response{
					Body: responseJsonReader,
				}
				return mockedResponse, nil
			},
		},
	}
	response, err := client.GetCertificates(testdata.CsrBytes, testdata.PkBytes)

	assert.Nil(t, response)
	assert.Error(t, err)
}

func Test_shouldReturnError_whenHttpClientReturnsError(t *testing.T) {
	client := CertServiceClient{
		certificationUrl: certificationUrl,
		httpClient:       &httpClientMock{
			DoFunc: func(req *http.Request) (response *http.Response, err error) {
				return nil, fmt.Errorf("mock error")
			},
		},
	}
	response, err := client.GetCertificates(testdata.CsrBytes, testdata.PkBytes)

	assert.Nil(t, response)
	assert.Error(t, err)
}


type httpClientMock struct {
	DoFunc func(req *http.Request) (*http.Response, error)
}

func (client httpClientMock) Do(req *http.Request) (*http.Response, error) {
	return client.DoFunc(req)
}
