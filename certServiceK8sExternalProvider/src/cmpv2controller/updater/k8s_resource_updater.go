package updater

import (
	core "k8s.io/api/core/v1"

	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/tools/record"

	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

// Fire an Event to additionally inform users of the change
func FireEventCert(recorder record.EventRecorder, resource runtime.Object, status cmmeta.ConditionStatus, reason string, message string) {
	eventType := core.EventTypeNormal
	if status == cmmeta.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	recorder.Event(resource, eventType, reason, message)
}

func FireEventIssuer(recorder record.EventRecorder, resource runtime.Object, status cmpv2api.ConditionStatus, reason string, message string) {
	eventType := core.EventTypeNormal
	if status == cmpv2api.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	recorder.Event(resource, eventType, reason, message)
}
