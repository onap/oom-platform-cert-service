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
