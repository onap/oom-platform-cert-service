package certserviceclient

import (
	"encoding/base64"
	"encoding/json"
	"net/http"
)

const (
	CsrHeaderName = "CSR"
	PkHeaderName = "PK"
)

type CertServiceClient struct {
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

func (client *CertServiceClient) GetCertificates(csr []byte, key []byte) (*CertificatesResponse, error) {

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

	var certificatesResponse CertificatesResponse
	err = json.NewDecoder(response.Body).Decode(&certificatesResponse)
	if err != nil {
		return nil, err
	}

	return &certificatesResponse, err
}
