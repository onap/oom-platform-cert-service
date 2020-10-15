/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (c) 2019 Smallstep Labs, Inc.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
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

package cmpv2controller

import (
	"context"
	"fmt"
	"github.com/go-logr/logr"
	core "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/apis/meta/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

type CMPv2IssuerStatusUpdater struct {
	*CMPv2IssuerController
	issuer *cmpv2api.CMPv2Issuer
	logger logr.Logger
}

func newStatusUpdater(reconciler *CMPv2IssuerController, issuer *cmpv2api.CMPv2Issuer, log logr.Logger) *CMPv2IssuerStatusUpdater {
	return &CMPv2IssuerStatusUpdater{
		CMPv2IssuerController: reconciler,
		issuer:                issuer,
		logger:                log,
	}
}

func (reconciler *CMPv2IssuerStatusUpdater) Update(ctx context.Context, status cmpv2api.ConditionStatus, reason, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	reconciler.setCondition(status, reason, completeMessage)

	// Fire an Event to additionally inform users of the change
	eventType := core.EventTypeNormal
	if status == cmpv2api.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	reconciler.logger.Info("Firing event: ", "issuer", reconciler.issuer, "eventtype", eventType, "reason", reason, "message", completeMessage)
	reconciler.Recorder.Event(reconciler.issuer, eventType, reason, completeMessage)

	reconciler.logger.Info("Updating issuer... ")
	return reconciler.Client.Update(ctx, reconciler.issuer)
}

func (reconciler *CMPv2IssuerStatusUpdater) UpdateNoError(ctx context.Context, status cmpv2api.ConditionStatus, reason, message string, args ...interface{}) {
	if err := reconciler.Update(ctx, status, reason, message, args...); err != nil {
		reconciler.logger.Error(err, "failed to update", "status", status, "reason", reason)
	}
}

// setCondition will set a 'condition' on the given cmpv2api.CMPv2Issuer resource.
//
// - If no condition of the same type already exists, the condition will be
//   inserted with the LastTransitionTime set to the current time.
// - If a condition of the same type and state already exists, the condition
//   will be updated but the LastTransitionTime will not be modified.
// - If a condition of the same type and different state already exists, the
//   condition will be updated and the LastTransitionTime set to the current
//   time.
func (reconciler *CMPv2IssuerStatusUpdater) setCondition(status cmpv2api.ConditionStatus, reason, message string) {
	now := meta.NewTime(reconciler.Clock.Now())
	issuerCondition := cmpv2api.CMPv2IssuerCondition{
		Type:               cmpv2api.ConditionReady,
		Status:             status,
		Reason:             reason,
		Message:            message,
		LastTransitionTime: &now,
	}

	// Search through existing conditions
	for i, condition := range reconciler.issuer.Status.Conditions {
		// Skip unrelated conditions
		if condition.Type != cmpv2api.ConditionReady {
			continue
		}

		// If this update doesn't contain a state transition, we don't update
		// the conditions LastTransitionTime to Now()
		if condition.Status == status {
			issuerCondition.LastTransitionTime = condition.LastTransitionTime
		} else {
			reconciler.logger.Info("found status change for CMPv2Issuer condition; setting lastTransitionTime", "condition", condition.Type, "old_status", condition.Status, "new_status", status, "time", now.Time)
		}

		// Overwrite the existing condition
		reconciler.issuer.Status.Conditions[i] = issuerCondition
		return
	}

	// If we've not found an existing condition of this type, we simply insert
	// the new condition into the slice.
	reconciler.issuer.Status.Conditions = append(reconciler.issuer.Status.Conditions, issuerCondition)
	reconciler.logger.Info("setting lastTransitionTime for CMPv2Issuer condition", "condition", cmpv2api.ConditionReady, "time", now.Time)
}
