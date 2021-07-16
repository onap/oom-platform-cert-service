/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package model

import (
	"context"
	"github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/util"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner/csr"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

func CreateSignCertificateModel(client client.Client, certificateRequest *v1.CertificateRequest, ctx context.Context, privateKeyBytes []byte) (SignCertificateModel, error) {
	log := leveledlogger.GetLoggerWithName("certservice-certificate-model")
	oldCertificateBytes, oldPrivateKeyBytes := util.RetrieveOldCertificateAndPkForCertificateUpdate(
		client, certificateRequest, ctx)

	csrBytes := certificateRequest.Spec.Request
	log.Debug("Original CSR PEM: ", "bytes", csrBytes)

	filteredCsrBytes, err := csr.FilterFieldsFromCSR(csrBytes, privateKeyBytes)
	if err != nil {
		return SignCertificateModel{}, err
	}
	log.Debug("Filtered out CSR PEM: ", "bytes", filteredCsrBytes)

	signCertificateModel := SignCertificateModel{
		CertificateRequest:  certificateRequest,
		FilteredCsr:         filteredCsrBytes,
		PrivateKeyBytes:     privateKeyBytes,
		OldCertificateBytes: oldCertificateBytes,
		OldPrivateKeyBytes:  oldPrivateKeyBytes,
	}
	return signCertificateModel, nil
}
