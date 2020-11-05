/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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

package updater

import (
	"context"
	"fmt"

	apiutil "github.com/jetstack/cert-manager/pkg/api/util"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	cmmeta "github.com/jetstack/cert-manager/pkg/apis/meta/v1"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
)

type CertificateRequestStatusUpdater struct {
	client             client.Client
	recorder           record.EventRecorder
	logger             leveledlogger.Logger
	ctx                context.Context
	certificateRequest *cmapi.CertificateRequest
}

func NewCertificateRequestUpdater(client client.Client,
	recorder record.EventRecorder, certificateRequest *cmapi.CertificateRequest, ctx context.Context, log leveledlogger.Logger) *CertificateRequestStatusUpdater {
	return &CertificateRequestStatusUpdater{
		client:             client,
		recorder:           recorder,
		logger:             log,
		ctx:                ctx,
		certificateRequest: certificateRequest,
	}
}

func (updater *CertificateRequestStatusUpdater) UpdateStatusWithEventTypeWarning(reason string, message string, args ...interface{}) error {
	return updater.updateStatus(cmmeta.ConditionFalse, reason, message, args...)
}

func (updater *CertificateRequestStatusUpdater) UpdateCertificateRequestWithSignedCertificates() error {
	return updater.updateStatus(cmmeta.ConditionTrue, cmapi.CertificateRequestReasonIssued, "Certificate issued")
}

func (updater *CertificateRequestStatusUpdater) updateStatus(status cmmeta.ConditionStatus, reason string, message string, args ...interface{}) error {
	completeMessage := fmt.Sprintf(message, args...)
	apiutil.SetCertificateRequestCondition(updater.certificateRequest, cmapi.CertificateRequestConditionReady, status, reason, completeMessage)

	FireEventCert(updater.recorder, updater.certificateRequest, status, reason, completeMessage)

	return updater.client.Status().Update(updater.ctx, updater.certificateRequest)
}
