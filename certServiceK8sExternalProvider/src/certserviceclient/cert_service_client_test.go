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
	client := CertServiceClientImpl{
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
	client := CertServiceClientImpl{
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
	client := CertServiceClientImpl{
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
	DoFunc func(*http.Request) (*http.Response, error)
}

func (client httpClientMock) Do(req *http.Request) (*http.Response, error) {
	return client.DoFunc(req)
}
