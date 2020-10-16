package cmpv2controller

import (
	"github.com/go-logr/logr"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"testing"
)

func TestValidateCMPv2IssuerSpec_invalid(t *testing.T) {
	spec := cmpv2api.CMPv2IssuerSpec{}
	err := validateCMPv2IssuerSpec(spec, nil)
	assert.NotNil(t, err)
}

func TestValidateCMPv2IssuerSpec_valid(t *testing.T) {
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
