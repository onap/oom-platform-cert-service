package cmpv2api

import (
	"testing"
	"github.com/stretchr/testify/assert"
)

func TestGroupVersion(t *testing.T) {

	assert.Equal(t, "certmanager.onap.org", GroupVersion.Group)
	assert.Equal(t, "v1", GroupVersion.Version)

	assert.Equal(t, "CMPv2Issuer", CMPv2IssuerKind)
}
