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
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net/http"
)

const (
	CsrHeaderName = "CSR"
	PkHeaderName  = "PK"
)

type CertServiceClient interface {
	GetCertificates(csr []byte, key []byte) (*CertificatesResponse, error)
	CheckHealth() error
}

type CertServiceClientImpl struct {
	healthUrl        string
	certificationUrl string
	httpClient       HTTPClient
}

type HTTPClient interface {
	Do(req *http.Request) (*http.Response, error)
}

type CertificatesResponse struct {
	CertificateChain    []string `json:"certificateChain"`
	TrustedCertificates []string `json:"trustedCertificates"`
}

type ResponseException struct {
	ErrorMessage string `json:"errorMessage"`
}

func (client *CertServiceClientImpl) CheckHealth() error {
	request, err := http.NewRequest("GET", client.healthUrl, nil)
	if err != nil {
		return err
	}

	response, err := client.httpClient.Do(request)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		return fmt.Errorf("health check retured status code [%d]", response.StatusCode)
	}

	return nil
}

func (client *CertServiceClientImpl) GetCertificates(csr []byte, key []byte) (*CertificatesResponse, error) {

	request, err := http.NewRequest("GET", client.certificationUrl, nil)
	if err != nil {
		return nil, err
	}

	request.Header.Add(CsrHeaderName, base64.StdEncoding.EncodeToString(csr))
	request.Header.Add(PkHeaderName, base64.StdEncoding.EncodeToString(key))
	response, err := client.httpClient.Do(request)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		var responseException ResponseException
		err = json.NewDecoder(response.Body).Decode(&responseException)
		return nil, fmt.Errorf("CertService API returned status code [%d] and message [%s]",
			response.StatusCode, responseException.ErrorMessage)
	}

	var certificatesResponse CertificatesResponse
	err = json.NewDecoder(response.Body).Decode(&certificatesResponse)
	if err != nil {
		return nil, err
	}

	return &certificatesResponse, err
}
