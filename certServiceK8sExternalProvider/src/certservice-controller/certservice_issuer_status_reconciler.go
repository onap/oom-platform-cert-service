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

package certservice_controller

import (
	"context"
	"fmt"
	"github.com/go-logr/logr"
	core "k8s.io/api/core/v1"
	meta "k8s.io/apimachinery/pkg/apis/meta/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/api"
)

type certServiceIssuerStatusReconciler struct {
	*CertServiceIssuerReconciler
	issuer *api.CertServiceIssuer
	logger logr.Logger
}

func newStatusReconciler(reconciler *CertServiceIssuerReconciler, issuer *api.CertServiceIssuer, log logr.Logger) *certServiceIssuerStatusReconciler {
	return &certServiceIssuerStatusReconciler{
		CertServiceIssuerReconciler: reconciler,
		issuer:                      issuer,
		logger:                      log,
	}
}

func (reconciler *certServiceIssuerStatusReconciler) Update(ctx context.Context, status api.ConditionStatus, reason, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	reconciler.setCondition(status, reason, completeMessage)

	// Fire an Event to additionally inform users of the change
	eventType := core.EventTypeNormal
	if status == api.ConditionFalse {
		eventType = core.EventTypeWarning
	}
	reconciler.Recorder.Event(reconciler.issuer, eventType, reason, completeMessage)

	return reconciler.Client.Status().Update(ctx, reconciler.issuer)
}

func (reconciler *certServiceIssuerStatusReconciler) UpdateNoError(ctx context.Context, status api.ConditionStatus, reason, message string, args ...interface{}) {
	if err := reconciler.Update(ctx, status, reason, message, args...); err != nil {
		reconciler.logger.Error(err, "failed to update", "status", status, "reason", reason)
	}
}

// setCondition will set a 'condition' on the given api.CertServiceIssuer resource.
//
// - If no condition of the same type already exists, the condition will be
//   inserted with the LastTransitionTime set to the current time.
// - If a condition of the same type and state already exists, the condition
//   will be updated but the LastTransitionTime will not be modified.
// - If a condition of the same type and different state already exists, the
//   condition will be updated and the LastTransitionTime set to the current
//   time.
func (reconciler *certServiceIssuerStatusReconciler) setCondition(status api.ConditionStatus, reason, message string) {
	now := meta.NewTime(reconciler.Clock.Now())
	issuerCondition := api.CertServiceIssuerCondition{
		Type:               api.ConditionReady,
		Status:             status,
		Reason:             reason,
		Message:            message,
		LastTransitionTime: &now,
	}

	// Search through existing conditions
	for i, condition := range reconciler.issuer.Status.Conditions {
		// Skip unrelated conditions
		if condition.Type != api.ConditionReady {
			continue
		}

		// If this update doesn't contain a state transition, we don't update
		// the conditions LastTransitionTime to Now()
		if condition.Status == status {
			issuerCondition.LastTransitionTime = condition.LastTransitionTime
		} else {
			reconciler.logger.Info("found status change for CertServiceIssuer condition; setting lastTransitionTime", "condition", condition.Type, "old_status", condition.Status, "new_status", status, "time", now.Time)
		}

		// Overwrite the existing condition
		reconciler.issuer.Status.Conditions[i] = issuerCondition
		return
	}

	// If we've not found an existing condition of this type, we simply insert
	// the new condition into the slice.
	reconciler.issuer.Status.Conditions = append(reconciler.issuer.Status.Conditions, issuerCondition)
	reconciler.logger.Info("setting lastTransitionTime for CertServiceIssuer condition", "condition", api.ConditionReady, "time", now.Time)
}
