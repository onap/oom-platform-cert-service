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

package x509

import (
	"bytes"
	"crypto/x509"
	"encoding/pem"
	"fmt"
)

const (
	PemCsrType        = "CERTIFICATE REQUEST"
	pemPrivateKeyType = "PRIVATE KEY"
)

// decodeCSR decodes a certificate request in PEM format
func DecodeCSR(data []byte) (*x509.CertificateRequest, error) {
	block, err := decodePemBlock(data, PemCsrType)
	if err != nil {
		return nil,  fmt.Errorf("error decoding CSR PEM: %v", err)
	}
	csr, err := x509.ParseCertificateRequest(block.Bytes)
	if err != nil {
		return nil, fmt.Errorf("error parsing certificate request: %v", err)
	}
	if err := csr.CheckSignature(); err != nil {
		return nil, fmt.Errorf("error checking certificate request signature: %v", err)
	}
	return csr, nil
}

func DecodePrivateKey(data []byte) (interface{}, error) {
	block, err := decodePemBlock(data, pemPrivateKeyType)
	if err != nil {
		return nil,  fmt.Errorf("error decoding Private Key PEM: %v", err)
	}
	key, err := x509.ParsePKCS8PrivateKey(block.Bytes)
	if err != nil {
		return nil,  fmt.Errorf("error parsing Private Key: %v", err)
	}
	return key, nil
}

func decodePemBlock(data []byte, pemType string) (*pem.Block, error) {
	block, rest := pem.Decode(data)
	if block == nil || len(rest) > 0 {
		return nil, fmt.Errorf("unexpected PEM")
	}
	if block.Type != pemType {
		return nil, fmt.Errorf("PEM is not: %s", pemType)
	}
	return block, nil
}


func ParseCertificateArrayToBytes(certificateArray []string) ([]byte, error) {
	buffer := bytes.NewBuffer([]byte{})
	for _, cert := range certificateArray {
		block, _ := pem.Decode([]byte(cert))
		err := pem.Encode(buffer, &pem.Block{Type: "CERTIFICATE", Bytes: block.Bytes})
		if err != nil {
			return nil, err
		}
	}
	return buffer.Bytes(), nil
}
