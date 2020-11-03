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
	"testing"

	"github.com/go-logr/logr"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"k8s.io/client-go/tools/record"
	"k8s.io/utils/clock"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	provisioners "onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner"
	"onap.org/oom-certservice/k8s-external-provider/src/testdata"
)

func Test_shouldPrepareAndVerifyCMPv2Issuer_whenRequestReceived(t *testing.T) {
	scheme := testdata.GetScheme()
	issuer, secret := testdata.GetValidIssuerWithSecret()
	fakeClient := fake.NewFakeClientWithScheme(scheme, &issuer, &secret)
	fakeRequest := testdata.GetFakeRequest(testdata.IssuerObjectName)
	fakeRecorder := record.NewFakeRecorder(recorderBufferSize)
	controller := getCMPv2IssuerController(fakeRecorder, fakeClient)

	res, err := controller.Reconcile(fakeRequest)

	expectedProvisioner, _ := controller.ProvisionerFactory.CreateProvisioner(&issuer, secret)
	actualProvisioner, _ := provisioners.Load(testdata.GetIssuerStoreKey())
	assert.Nil(t, err)
	assert.NotNil(t, res)
	assert.Equal(t, <-fakeRecorder.Events, "Normal Verified CMPv2Issuer verified and ready to sign certificates")
	assert.NotNil(t, actualProvisioner)
	assert.ObjectsAreEqual(expectedProvisioner, actualProvisioner)
	clearProvisioner()
}

func Test_shouldBeValidCMPv2IssuerSpec_whenAllFieldsAreSet(t *testing.T) {
	spec := testdata.GetValidCMPv2IssuerSpec()

	err := validateCMPv2IssuerSpec(spec, &MockLogger{})
	assert.Nil(t, err)
}

func Test_shouldBeInvalidCMPv2IssuerSpec_whenSpecIsEmpty(t *testing.T) {
	spec := cmpv2api.CMPv2IssuerSpec{}
	err := validateCMPv2IssuerSpec(spec, nil)
	assert.NotNil(t, err)
}

func Test_shouldBeInvalidCMPv2IssuerSpec_whenNotAllFieldsAreSet(t *testing.T) {
	setEmptyFieldFunctions := map[string]func(spec *cmpv2api.CMPv2IssuerSpec){
		"emptyUrl":            func(spec *cmpv2api.CMPv2IssuerSpec) { spec.URL = "" },
		"empryCaName":         func(spec *cmpv2api.CMPv2IssuerSpec) { spec.CaName = "" },
		"emptySecretName":     func(spec *cmpv2api.CMPv2IssuerSpec) { spec.CertSecretRef.Name = "" },
		"emptySecretKeyRef":   func(spec *cmpv2api.CMPv2IssuerSpec) { spec.CertSecretRef.KeyRef = "" },
		"emptySecretCertRef":  func(spec *cmpv2api.CMPv2IssuerSpec) { spec.CertSecretRef.CertRef = "" },
		"emptySecretCaertRef": func(spec *cmpv2api.CMPv2IssuerSpec) { spec.CertSecretRef.CacertRef = "" },
	}

	for caseName, setEmptyFieldFunction := range setEmptyFieldFunctions {
		t.Run(caseName, func(t *testing.T) {
			test_shouldBeInvalidCMPv2IssuerSpec_whenFunctionApplied(t, setEmptyFieldFunction)
		})
	}
}

func test_shouldBeInvalidCMPv2IssuerSpec_whenFunctionApplied(t *testing.T, transformSpec func(spec *cmpv2api.CMPv2IssuerSpec)) {
	spec := testdata.GetValidCMPv2IssuerSpec()
	transformSpec(&spec)
	err := validateCMPv2IssuerSpec(spec, nil)
	assert.NotNil(t, err)
}

func getCMPv2IssuerController(fakeRecorder *record.FakeRecorder, mockClient client.Client) CMPv2IssuerController {
	controller := CMPv2IssuerController{
		Log:                ctrl.Log.WithName("controllers").WithName("CertificateRequest"),
		Clock:              clock.RealClock{},
		Recorder:           fakeRecorder,
		Client:             mockClient,
		ProvisionerFactory: &provisioners.ProvisionerFactoryMock{},
	}
	return controller
}

type MockLogger struct {
	mock.Mock
}

func (m *MockLogger) Info(msg string, keysAndValues ...interface{})             {}
func (m *MockLogger) Error(err error, msg string, keysAndValues ...interface{}) {}
func (m *MockLogger) Enabled() bool                                             { return false }
func (m *MockLogger) V(level int) logr.Logger                                   { return m }
func (m *MockLogger) WithValues(keysAndValues ...interface{}) logr.Logger       { return m }
func (m *MockLogger) WithName(name string) logr.Logger                          { return m }
