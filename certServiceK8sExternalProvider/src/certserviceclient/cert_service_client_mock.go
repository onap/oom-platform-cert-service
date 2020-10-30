package certserviceclient

type CertServiceClientMock struct {
	GetCertificatesFunc func(csr []byte, key []byte) (*CertificatesResponse, error)
}

func (client *CertServiceClientMock) GetCertificates(csr []byte, key []byte) (*CertificatesResponse, error) {
	return client.GetCertificatesFunc(csr, key)
}

func (client *CertServiceClientMock) CheckHealth() error {
	return nil
}
