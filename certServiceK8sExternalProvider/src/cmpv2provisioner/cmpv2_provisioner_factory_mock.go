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

package cmpv2provisioner

import (
	v1 "k8s.io/api/core/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner/testdata"
	"onap.org/oom-certservice/k8s-external-provider/src/model"
)

type ProvisionerFactoryMock struct {
	CreateProvisionerFunc func(issuer *cmpv2api.CMPv2Issuer, secret v1.Secret) (*CertServiceCA, error)
}

func (f *ProvisionerFactoryMock) CreateProvisioner(issuer *cmpv2api.CMPv2Issuer, secret v1.Secret) (*CertServiceCA, error) {
	provisioner, err := New(issuer, &certserviceclient.CertServiceClientMock{
		GetCertificatesFunc: func(signCertificateModel model.SignCertificateModel) (response *certserviceclient.CertificatesResponse, e error) {
			return &testdata.SampleCertServiceResponse, nil
		},
		UpdateCertificateFunc: func(signCertificateModel model.SignCertificateModel) (*certserviceclient.CertificatesResponse, error) {
			return &testdata.SampleCertServiceResponse, nil
		},
	})

	return provisioner, err
}
