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
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"net/http"
	"net/url"
	"path"
)

func CreateCertServiceClient(baseUrl string, caName string, keyPemBase64 []byte, certPemBase64 []byte, cacertPemBase64 []byte) (*CertServiceClientImpl, error) {
	cert, err := tls.X509KeyPair(certPemBase64, keyPemBase64)
	if err != nil {
		return nil, err
	}
	x509.NewCertPool()
	caCertPool := x509.NewCertPool()
	ok := caCertPool.AppendCertsFromPEM(cacertPemBase64)
	if !ok {
		return nil, fmt.Errorf("couldn't certs from cacert")
	}
	httpClient := &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{
				RootCAs:      caCertPool,
				Certificates: []tls.Certificate{cert},
			},
		},
	}
	certificationUrl, err := parseUrl(baseUrl, caName)
	if err != nil {
		return nil, err
	}
	client := CertServiceClientImpl{
		certificationUrl: certificationUrl.String(),
		httpClient:       httpClient,
	}

	return &client, nil
}

func parseUrl(baseUrl string, caName string) (*url.URL, error) {
	parsedUrl, err := url.Parse(baseUrl)
	if err != nil {
		return nil, err
	}
	if caName == "" {
		return nil, fmt.Errorf("caName cannot be empty")
	}

	parsedUrl.Path = path.Join(parsedUrl.Path, caName)
	return parsedUrl, nil
}
