/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
 * ================================================================================
 * This source code was copied from the following git repository:
 * https://github.com/smallstep/step-issuer
 * The source code was modified for usage in the ONAP project.
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

package util

import (
	"context"
	"encoding/json"
	"strconv"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	core "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	revisionAnnotation                 = "cert-manager.io/certificate-revision"
	certificateConfigurationAnnotation = "kubectl.kubernetes.io/last-applied-configuration"
	oldCertificateSecretKey            = "tls.crt"
	oldPrivateKeySecretKey             = "tls.key"
)

func RetrieveOldCertificateAndPkForCertificateUpdate(
	k8sClient client.Client,
	certificateRequest *cmapi.CertificateRequest,
	ctx context.Context,
) ([]byte, []byte) {
	if !IsUpdateCertificateRevision(certificateRequest) {
		return []byte{}, []byte{}
	}
	return RetrieveOldCertificateAndPk(k8sClient, certificateRequest, ctx)
}

func IsUpdateCertificateRevision(certificateRequest *cmapi.CertificateRequest) bool {
	revision, err := strconv.Atoi(certificateRequest.ObjectMeta.Annotations[revisionAnnotation])
	if err != nil {
		return false
	}
	return revision > 1
}

func RetrieveOldCertificateAndPk(
	k8sClient client.Client,
	certificateRequest *cmapi.CertificateRequest,
	ctx context.Context,
) ([]byte, []byte) {
	certificateConfigString := certificateRequest.ObjectMeta.Annotations[certificateConfigurationAnnotation]
	var certificateConfig cmapi.Certificate
	if err := json.Unmarshal([]byte(certificateConfigString), &certificateConfig); err != nil {
		return []byte{}, []byte{}
	}
	oldCertificateSecretName := certificateConfig.Spec.SecretName
	oldCertificateSecretNamespacedName := types.NamespacedName{
		Namespace: certificateConfig.Namespace,
		Name:      oldCertificateSecretName,
	}
	var oldCertificateSecret core.Secret
	if err := k8sClient.Get(ctx, oldCertificateSecretNamespacedName, &oldCertificateSecret); err != nil {
		return []byte{}, []byte{}
	}
	return oldCertificateSecret.Data[oldCertificateSecretKey], oldCertificateSecret.Data[oldPrivateKeySecretKey]
}
