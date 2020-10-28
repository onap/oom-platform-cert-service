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
	"testing"

	"github.com/stretchr/testify/assert"

	"onap.org/oom-certservice/k8s-external-provider/src/x509/testdata"
)

func TestShouldDecodeCSR(t *testing.T) {
	csr, err := DecodeCSR([]byte(testdata.ValidCertificateSignRequest))

	assert.Nil(t, err)
	assert.Equal(t, "ONAP", csr.Subject.Organization[0])
}

func TestShouldReturnError(t *testing.T) {
	_, err := DecodeCSR([]byte(testdata.InvalidCertificateSignRequest))

	assert.NotNil(t, err)
}
