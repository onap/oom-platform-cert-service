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
	"onap.org/oom-certservice/k8s-external-provider/src/klogger"

	core "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/apis/meta/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

type CMPv2IssuerStatusUpdater struct {
	*CMPv2IssuerController
	issuer *cmpv2api.CMPv2Issuer
	logger klogger.LeveledLogger
}

func newStatusUpdater(controller *CMPv2IssuerController, issuer *cmpv2api.CMPv2Issuer, log klogger.LeveledLogger) *CMPv2IssuerStatusUpdater {
	return &CMPv2IssuerStatusUpdater{
		CMPv2IssuerController: controller,
		issuer:                issuer,
		logger:                log,
	}
}

func (updater *CMPv2IssuerStatusUpdater) Update(ctx context.Context, status cmpv2api.ConditionStatus, reason, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	updater.setCondition(status, reason, completeMessage)

	// Fire an Event to additionally inform users of the change
	eventType := core.EventTypeNormal
	if status == cmpv2api.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	updater.Recorder.Event(updater.issuer, eventType, reason, completeMessage)

	return updater.Client.Update(ctx, updater.issuer)
}

func (updater *CMPv2IssuerStatusUpdater) UpdateNoError(ctx context.Context, status cmpv2api.ConditionStatus, reason, message string, args ...interface{}) {
	if err := updater.Update(ctx, status, reason, message, args...); err != nil {
		updater.logger.Error(err, "failed to update", "status", status, "reason", reason)
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
func (updater *CMPv2IssuerStatusUpdater) setCondition(status cmpv2api.ConditionStatus, reason, message string) {
	now := meta.NewTime(updater.Clock.Now())
	issuerCondition := cmpv2api.CMPv2IssuerCondition{
		Type:               cmpv2api.ConditionReady,
		Status:             status,
		Reason:             reason,
		Message:            message,
		LastTransitionTime: &now,
	}

	// Search through existing conditions
	for i, condition := range updater.issuer.Status.Conditions {
		// Skip unrelated conditions
		if condition.Type != cmpv2api.ConditionReady {
			continue
		}

		// If this update doesn't contain a state transition, we don't update
		// the conditions LastTransitionTime to Now()
		if condition.Status == status {
			issuerCondition.LastTransitionTime = condition.LastTransitionTime
		} else {
			updater.logger.Info("found status change for CMPv2Issuer condition; setting lastTransitionTime", "condition", condition.Type, "old_status", condition.Status, "new_status", status, "time", now.Time)
		}

		// Overwrite the existing condition
		updater.issuer.Status.Conditions[i] = issuerCondition
		return
	}

	// If we've not found an existing condition of this type, we simply insert
	// the new condition into the slice.
	updater.issuer.Status.Conditions = append(updater.issuer.Status.Conditions, issuerCondition)
	updater.logger.Info("setting lastTransitionTime for CMPv2Issuer condition", "condition", cmpv2api.ConditionReady, "time", now.Time)
}
