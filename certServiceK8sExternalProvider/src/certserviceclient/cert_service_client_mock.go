/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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

import "onap.org/oom-certservice/k8s-external-provider/src/model"

type CertServiceClientMock struct {
	GetCertificatesFunc   func(signCertificateModel model.SignCertificateModel) (*CertificatesResponse, error)
	UpdateCertificateFunc func(signCertificateModel model.SignCertificateModel) (*CertificatesResponse, error)
}

func (client *CertServiceClientMock) UpdateCertificate(signCertificateModel model.SignCertificateModel) (*CertificatesResponse, error) {
	return client.UpdateCertificateFunc(signCertificateModel)
}

func (client *CertServiceClientMock) GetCertificates(signCertificateModel model.SignCertificateModel) (*CertificatesResponse, error) {
	return client.GetCertificatesFunc(signCertificateModel)
}

func (client *CertServiceClientMock) CheckHealth() error {
	return nil
}
