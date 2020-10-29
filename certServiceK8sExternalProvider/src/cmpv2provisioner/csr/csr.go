package csr

import (
	"crypto/rand"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"
	"fmt"
)

func FilterFieldsFromCSR(csrBytes []byte, privateKeyBytes []byte) ([]byte, error) {
	decodedCsr, _ := pem.Decode(csrBytes)
	if decodedCsr == nil {
		return nil, fmt.Errorf("can't decode CSR PEM")
	}
	csr, err := x509.ParseCertificateRequest(decodedCsr.Bytes)
	if err != nil {
		return nil, err
	}

	decodedPrivateKey, _ := pem.Decode(privateKeyBytes)
	if decodedPrivateKey == nil {
		return nil, fmt.Errorf("can't decode Private Key PEM")
	}
	key, err := x509.ParsePKCS8PrivateKey(decodedPrivateKey.Bytes)
	if err != nil {
		return nil, err
	}

	filteredSubject := filterFieldsFromSubject(csr.Subject)

	filteredCsr, err := x509.CreateCertificateRequest(rand.Reader, &x509.CertificateRequest{
		Subject:            filteredSubject,
		DNSNames:           csr.DNSNames,
	}, key)
	if err != nil {
		return nil, err
	}

	csrBytes = pem.EncodeToMemory(&pem.Block{Type: decodedCsr.Type, Bytes: filteredCsr})
	return csrBytes, nil
}

func filterFieldsFromSubject(subject pkix.Name) pkix.Name {
	subject.StreetAddress = []string{}
	subject.SerialNumber = ""
	subject.PostalCode = []string{}
	return subject
}
