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

package cmpv2controller

import (
	"github.com/go-logr/logr"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"testing"
)

func Test_shouldBeInvalidCMPv2IssuerSpec_whenSpecIsEmpty(t *testing.T) {
	spec := cmpv2api.CMPv2IssuerSpec{}
	err := validateCMPv2IssuerSpec(spec, nil)
	assert.NotNil(t, err)
}

func Test_shouldBeInvalidCMPv2IssuerSpec_whenNotAllFieldsAreSet(t *testing.T) {
	spec := cmpv2api.CMPv2IssuerSpec{}
	spec.URL = "https://localhost"
	spec.KeyRef = cmpv2api.SecretKeySelector{}
	spec.KeyRef.Name = "secret-key"

	err := validateCMPv2IssuerSpec(spec, &MockLogger{})
	assert.NotNil(t, err)
}

func Test_shouldBeValidCMPv2IssuerSpec_whenAllFieldsAreSet(t *testing.T) {
	spec := cmpv2api.CMPv2IssuerSpec{}
	spec.URL = "https://localhost"
	spec.KeyRef = cmpv2api.SecretKeySelector{}
	spec.KeyRef.Name = "secret-key"
	spec.KeyRef.Key = "the-key"

	err := validateCMPv2IssuerSpec(spec, &MockLogger{})
	assert.Nil(t, err)
}

type MockLogger struct {
	mock.Mock
}
func (m *MockLogger) Info(msg string, keysAndValues ...interface{}) {}
func (m *MockLogger) Error(err error, msg string, keysAndValues ...interface{}) {}
func (m *MockLogger) Enabled() bool { return false }
func (m *MockLogger) V(level int) logr.Logger { return m }
func (m *MockLogger) WithValues(keysAndValues ...interface{}) logr.Logger { return m }
func (m *MockLogger) WithName(name string) logr.Logger { return m }
