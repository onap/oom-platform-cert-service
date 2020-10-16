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

package app

import (
	"testing"
	"github.com/stretchr/testify/assert"
)

func TestExitCodes(t *testing.T) {
	assert.Equal(t, FAILED_TO_CREATE_CONTROLLER_MANAGER.Code, 1)
	assert.Equal(t, FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER.Code, 2)
	assert.Equal(t, FAILED_TO_REGISTER_CERT_REQUEST_CONTROLLER.Code, 3)
	assert.Equal(t, EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER.Code, 4)
}
