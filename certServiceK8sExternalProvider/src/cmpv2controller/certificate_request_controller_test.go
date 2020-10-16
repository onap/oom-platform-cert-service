package cmpv2controller

import (
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"testing"
	"github.com/stretchr/testify/assert"

)

func TestIsCMPv2CertificateRequest_notCMPv2Request(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	assert.False(t, isCMPv2CertificateRequest(request))

	request.Spec.IssuerRef.Group = "certmanager.onap.org"
	request.Spec.IssuerRef.Kind = "CertificateRequest"
	assert.False(t, isCMPv2CertificateRequest(request))
}

func TestIsCMPv2CertificateRequest_CMPvRequest(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.Spec.IssuerRef.Group = "certmanager.onap.org"
	request.Spec.IssuerRef.Kind = "CMPv2Issuer"

	assert.True(t, isCMPv2CertificateRequest(request))
}

