package certserviceclient

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"net/http"
	"net/url"
	"path"
)

func CreateCertServiceClient(baseUrl string, caName string, keyPemBase64 []byte, certPemBase64 []byte, cacertPemBase64 []byte) (*CertServiceClient, error) {
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
	client := CertServiceClient{
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
