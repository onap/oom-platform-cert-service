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
	"fmt"
	"k8s.io/api/core/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

func CreateProvisioner(issuer *cmpv2api.CMPv2Issuer, secret v1.Secret) (*CertServiceCA, error) {
	secretKeys := issuer.Spec.CertSecretRef
	key, err := readValueFromSecret(secret, secretKeys.KeyRef)
	if err != nil {
		return nil, err
	}
	cert, err := readValueFromSecret(secret, secretKeys.CertRef)
	if err != nil {
		return nil, err
	}
	cacert, err := readValueFromSecret(secret, secretKeys.CacertRef)
	if err != nil {
		return nil, err
	}
	return New(issuer, key, cert, cacert)
}

func readValueFromSecret(secret v1.Secret, secretKey string) ([]byte, error) {
	value, ok := secret.Data[secretKey]
	if !ok {
		err := fmt.Errorf("secret %s does not contain key %s", secret.Name, secretKey)
		return nil, err
	}
	return value, nil
}
