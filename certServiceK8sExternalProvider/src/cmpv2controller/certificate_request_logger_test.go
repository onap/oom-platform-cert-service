package cmpv2controller

import (
	"bytes"
	"flag"
	"fmt"
	"strings"
	"testing"
	"time"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
	"k8s.io/klog/v2/klogr"
)

var csrPEM = (`-----BEGIN CERTIFICATE REQUEST-----
MIIC7zCCAdcCAQAwWzELMAkGA1UEBhMCUEwxEDAOBgNVBAcTB1dyb2NsYXcxDTAL
BgNVBAoTBE9OQVAxDTALBgNVBAsTBG9uYXAxHDAaBgNVBAMTE2NlcnRpc3N1ZXIu
b25hcC5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC7fBxglgfd
oEe7QTG7MWDBh/zIyCkfaNyqkf/WTVcFWH91qLgZwCdHXtKYROUS/uG4QCPioo/D
ZVigAtynMhIWWNe9MPTjTL/BVWXlZBnnt0ejBI1jJH+dROkVmxK06HHusTGcQ7IE
dwtkN47Ld1rLXqf+l6Yd2Z405/lEiJrKYmmIjx5+zQsSVjHMGLpwujsDxqNGut6h
lhonjYlcw0gQ9MtiDSMJEPsrrWDUz+ciC8RBySb4b0vlsS1AaMOrX9LwxyTfSu9O
FDnVun8fy4kjFyYDnqboU5td0+VC3BvFnXO/VsyPVO6POPtptMl0J8RNiio1n1EQ
JqT9nE3xliYbAgMBAAGgTzBNBgkqhkiG9w0BCQ4xQDA+MC8GA1UdEQQoMCaCCWxv
Y2FsaG9zdIITY2VydGlzc3Vlci5vbmFwLm9yZ4cEfwAAATALBgNVHQ8EBAMCBaAw
DQYJKoZIhvcNAQELBQADggEBAJTLI1pXJpRGFIeNHZHxnbpSFDE2dn0ZU2nI2x/y
n3MTgV4I6SjXQ0qY5KpNdDQ66XoLoZeyeiKxI8F4LTBjrKB25qCXq0D8K3gNaliG
ZkquDS5E/lsnpjY/nEi/6U+f94u0A9/wWeTMWxqaqlP1bt3MiJWCVwRuj4oxLJjB
W4a2H1bxshpzOWm4nKq7P0Z1COCyZNAIpmI/jbLOL4UMIs7JvgFRNDvEQLGvXZeb
nBs1/5RNqrUfYGu/OB5oux5t4eroM/K7HS+JQKIfPq29nK7WEe7gwdYwIImiyVYA
YyD4xOZ5bPddS779QwlZ259b53WLcwHebL9rGZo87L0/8A8=
-----END CERTIFICATE REQUEST-----`)

var csrWithSkippedInformation = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDOTCCAiECAQAwgaQxCzAJBgNVBAYTAlBMMRMwEQYDVQQIEwpEb2xueVNsYXNr
MRAwDgYDVQQHEwdXcm9jbGF3MREwDwYDVQQJEwhMb3RuaWN6YTEPMA0GA1UEERMG
MTItMzQ1MQ0wCwYDVQQKEwRPTkFQMQ0wCwYDVQQLEwRvbmFwMRwwGgYDVQQDExNj
ZXJ0aXNzdWVyLm9uYXAub3JnMQ4wDAYDVQQFEwUxMjM0NTCCASIwDQYJKoZIhvcN
AQEBBQADggEPADCCAQoCggEBAN1boMRC+3xovNxdPOW35CDZ0ojs4ggfaqNsR0WT
YPKjQALzvAhzLiaGk0Zi/S3NxRdabIxJBqQ3pbQjOg+LBZa5zwOaWH7jJ7mac5ut
/g7b3iTSRlT1UmTFqXf092ORpXwpuWBEs+/pJ4LU0L6cl/XFVuQfb6DRICdlYYIC
AXmcXHtx+kJM94Ma1nPcl3oqOf66/KCyreSfrfkXtheB1Skqczh3ImjLh0G37Tnz
VV1gmfpx+elPiiFqs/xOLPcybWqyqAgnQZemBRciQaRwqeMNWvCmjib92A50H51I
KOWJzx7SnO2E12ix6wsUafCuqldOQ7kFA2sv8QTU64lh3x8CAwEAAaBPME0GCSqG
SIb3DQEJDjFAMD4wLwYDVR0RBCgwJoIJbG9jYWxob3N0ghNjZXJ0aXNzdWVyLm9u
YXAub3JnhwR/AAABMAsGA1UdDwQEAwIFoDANBgkqhkiG9w0BAQsFAAOCAQEAuQ9u
aJnqkRF4LeRSWyP5zn4Y/a5UKeA2aMBshB5mX9eS8C71/Nt5MUq8FTlqFcWAAw8L
xoMcZXg9hnqxSOWtHyVas4W5bFZokuJtUY91AXn8uvbu3u5xQirYQWL5GTTA5SLr
47der024C/Fj3bhkwxSXfjH52YgYq2lLeVlywzva/TUrWaF4RyaiI3OH0lLlT8Qx
T2+TK6xRIdpK99a2ZiXh1m1L7GmS4TISeEBc8ljyL/FZvXMwakgvkY1BIBVUZjKe
CUEjbvOiOgSOi/YOqEbrsdz94Abu1VrIhYXXoK5BpR2IOQGbLQYHQYARfiahPU3X
FaQCJjnIIyH7JICDBw==
-----END CERTIFICATE REQUEST-----`)

func Test_shouldBeValidCMPv2CertificateRequest_whenKindIsCMPvIssuer1(t *testing.T) {
	request := new(cmapi.CertificateRequest)
	request.Spec.IssuerRef.Group = group
	request.Spec.IssuerRef.Kind = "CMPv2Issuer"

	assert.True(t, isCMPv2CertificateRequest(request))
}

//func TestInfo123(t *testing.T) {
//	//given
//	prepareCommandLine()
//	logger := klogr.New()
//	request := prepareCertificateRequest()
//	tmpWriteBuffer := getLogBuffer()
//
//	//when
//	logCertRequestProperties(logger, request)
//	closeLogBuffer()
//	logsArray := convertBufferToStringArray(tmpWriteBuffer)
//	//then
//	//expectedMessage := "warning"
//	expectedMessage := "Duration"
//	//expectedMessage := "Encoded"
//	result := containsExpectedMessage(logsArray, expectedMessage)
//	assert.False(t, result)
//}

func TestLogShouldProvideInformationAboutSkippedProperties(t *testing.T) {
	//given
	prepareCommandLine()
	logger := klogr.New()
	request := prepareCertificateRequestWithSkipped()
	tmpWriteBuffer := getLogBuffer()

	//when
	logCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)
	//then
	//expectedMessage := "warning"
	expectedMessage := "Postal"
	//expectedMessage := "Encoded"
	result := containsExpectedMessage(logsArray, expectedMessage)
	assert.True(t, result)
}

func closeLogBuffer() {
	klog.Flush()
}

func getLogBuffer() *bytes.Buffer {
	tmpWriteBuffer := bytes.NewBuffer(nil)
	klog.SetOutput(tmpWriteBuffer)
	return tmpWriteBuffer
}

func prepareCertificateRequest() *cmapi.CertificateRequest {
	request := new(cmapi.CertificateRequest)
	request.Spec.Request = []byte(csrPEM)
	return request
}

func prepareCertificateRequestWithSkipped() *cmapi.CertificateRequest {
	request := new(cmapi.CertificateRequest)
	request.Spec.Request = []byte(csrWithSkippedInformation)
	//request.Spec.Duration = &metav1.Duration{Duration: time.Hour}
	request.Spec.Duration = &metav1.Duration{Duration: time.Hour}
	return request
}

func prepareCommandLine() {
	klog.InitFlags(nil)
	flag.CommandLine.Set("v", "10")
	flag.CommandLine.Set("skip_headers", "true")
	flag.CommandLine.Set("logtostderr", "false")
	flag.CommandLine.Set("alsologtostderr", "false")
	flag.Parse()
}

func containsExpectedMessage(array []string, expectedMsg string) bool {
	for _, logMsg := range array {
		fmt.Println(logMsg)
		if strings.Contains(logMsg, expectedMsg) {
			return true
		}
	}
	return false
}

func convertBufferToStringArray(buffer *bytes.Buffer) []string {
	return strings.Split(buffer.String(), "\n")
}
