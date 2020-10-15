package cmpv2controller

import (
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"testing"
)

func TestIsCMPv2CertificateRequest_notCMPv2Request(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	if isCMPv2CertificateRequest(request) {
		t.Logf("CPMv2 request [NOK]")
		t.FailNow()
	}

	request.Spec.IssuerRef.Group = "certmanager.onap.org"
	request.Spec.IssuerRef.Kind = "CertificateRequest"
	if isCMPv2CertificateRequest(request) {
		t.Logf("CPMv2 request [NOK]")
		t.FailNow()
	}
}

func TestIsCMPv2CertificateRequest_CMPvRequest(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.Spec.IssuerRef.Group = "certmanager.onap.org"
	request.Spec.IssuerRef.Kind = "CMPv2Issuer"

	if isCMPv2CertificateRequest(request) {
		t.Logf("CPMv2 request [OK]")
	} else {
		t.Logf("Not a CPMv2 request [NOK]")
		t.FailNow()
	}
}

