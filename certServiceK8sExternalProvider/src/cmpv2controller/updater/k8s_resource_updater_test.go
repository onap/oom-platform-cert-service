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


func Test_shouldFireWaningEvent_forCmpv2Issuer(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventIssuer(fakeRecorder, nil, cmpv2api.ConditionFalse, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Warning testReason testMessage")
}

func Test_shouldFireNormalEvent_forCmpv2Issuer(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventIssuer(fakeRecorder, nil, cmpv2api.ConditionTrue, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Normal testReason testMessage")
}

func Test_shouldFireWaningEvent_forCertRequest(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventCert(fakeRecorder, nil, cmmeta.ConditionFalse, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Warning testReason testMessage")
}

func Test_shouldFireNormalEvent_forCertRequest(t *testing.T) {
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)

	FireEventCert(fakeRecorder, nil, cmmeta.ConditionTrue, "testReason", "testMessage")

	assert.Equal(t, <-fakeRecorder.Events, "Normal testReason testMessage")
}
