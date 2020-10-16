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
