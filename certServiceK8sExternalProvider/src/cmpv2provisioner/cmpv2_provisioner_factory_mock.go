package cmpv2provisioner

import (
	v1 "k8s.io/api/core/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

type ProvisionerFactoryMock struct {
	CreateProvisionerFunc func(issuer *cmpv2api.CMPv2Issuer, secret v1.Secret) (*CertServiceCA, error)
}

func (f *ProvisionerFactoryMock) CreateProvisioner(issuer *cmpv2api.CMPv2Issuer, secret v1.Secret) (*CertServiceCA, error) {

	provisioner, err := New(issuer, &certserviceclient.CertServiceClientMock{
		GetCertificatesFunc: func(csr []byte, pk []byte) (response *certserviceclient.CertificatesResponse, e error) {
			mockResponse := &certserviceclient.CertificatesResponse{
				CertificateChain:    []string{"cert-0", "cert-1"},
				TrustedCertificates: []string{"trusted-cert-0", "trusted-cert-1"},
			} //TODO: mock real certServiceClient response
			return mockResponse, nil
		},
	})

	return provisioner, err
}
