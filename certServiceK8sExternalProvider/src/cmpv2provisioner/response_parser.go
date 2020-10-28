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

package cmpv2provisioner

import (
	"bytes"
	"encoding/pem"

	"onap.org/oom-certservice/k8s-external-provider/src/certserviceclient"
)

func parseResponseToBytes(response *certserviceclient.CertificatesResponse) ([]byte, []byte, error) {

	certificateChainBytes, err := ParseCertificateArrayToBytes(response.CertificateChain)

	if err != nil {
		return nil, nil, err
	}
	trustedCertificatesBytes, err := ParseCertificateArrayToBytes(response.CertificateChain)

	if err != nil {
		return nil, nil, err
	}

	return certificateChainBytes, trustedCertificatesBytes, nil
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
