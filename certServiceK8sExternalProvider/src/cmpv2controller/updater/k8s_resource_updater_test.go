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

package updater

import (
	"testing"

	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"
	"github.com/stretchr/testify/assert"
	"k8s.io/client-go/tools/record"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

const (
	recorderBufferSize     = 3
)


func Test_shouldFireWarningEvent_forCmpv2Issuer(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventIssuer(fakeRecorder, nil, cmpv2api.ConditionFalse, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Warning testReason testMessage")
}

func Test_shouldFireNormalEvent_forCmpv2Issuer(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventIssuer(fakeRecorder, nil, cmpv2api.ConditionTrue, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Normal testReason testMessage")
}

func Test_shouldFireWarningEvent_forCertRequest(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventCert(fakeRecorder, nil, cmmeta.ConditionFalse, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Warning testReason testMessage")
}

func Test_shouldFireNormalEvent_forCertRequest(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventCert(fakeRecorder, nil, cmmeta.ConditionTrue, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Normal testReason testMessage")
}
