package cmpv2controller

import (
	"bytes"
	"flag"
	"fmt"
	"os"
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

var csrPEM2 = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDETCCAfkCAQAwgYIxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh
MRYwFAYDVQQHEw1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKExBMaW51eC1Gb3VuZGF0
aW9uMQ0wCwYDVQQLEwRPTkFQMRwwGgYDVQQDExNjZXJ0aXNzdWVyLm9uYXAub3Jn
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxhQiSgyYGpEfX/HuCFwT
GHkLe1CheKz2CQzSP9an5BSdET1OgABmuJjtnXZzKpPAZCGJX2QTyDE9zvdTN0Ci
/8WRL/m2tWUPbt8qRVW36PSKazpB+ELZjQi3rmYtmWUlRuJNfLcksK59pcD5W46t
d9eettkex0FAcxpQE/ukhpW9r6QrmlQAQHuF1rBw6uJMGzFSPWh9XFLFbxZJyJCu
AIycvT95bgtot3EMPwGkxAYzxtAu6D5/n65nIZ0f9BuuNFtmnoHmn/9fPUnZHA0h
qP9kXAAU10S3gig+Na6DeZFBE1y9jCt4vmSq2ssBO24kOAHrg5GrqEsnfoSnu8Nb
sQIDAQABoEkwRwYJKoZIhvcNAQkOMTowODApBgNVHREEIjAggglsb2NhbGhvc3SC
E2NlcnRpc3N1ZXIub25hcC5vcmcwCwYDVR0PBAQDAgWgMA0GCSqGSIb3DQEBCwUA
A4IBAQAWkOeJHnmtlSvlb7HbBeSGY4E9M338sKtwV4ZSvH+n5rgwamkvjhUwhycs
UR0XgeAyD86kK6kkvVewdIanHYp1k7CuDZkU6piy8t4RhosyqUWQNWtemGYdNZCL
cgZ1Jbj4NdIZo2EKBIEbTrm9VFt1zidYRFNGNJp8RQQds6r4qATq1NKr6ptrLuIc
dzfOm1ZPtSn8u4H4+z1re6q18JeM0VPXBiXBtEXwQRXIEnsjCzYxdjy+QwbEmlpB
o2hMIamWNIbskYnNkaky8eQzjJ8uIesESeanWJlrMUbzicOwQeYMPmj+Mkn1nqlK
YFwml5XnVXXpGLHGWCswpN3CDyXi
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

var csrWithSkippedInformation2 = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDWDCCAkACAQAwgaQxCzAJBgNVBAYTAlBMMRMwEQYDVQQIEwpEb2xueVNsYXNr
MRAwDgYDVQQHEwdXcm9jbGF3MREwDwYDVQQJEwhMb3RuaWN6YTEPMA0GA1UEERMG
MTItMzQ1MQ0wCwYDVQQKEwRPTkFQMQ0wCwYDVQQLEwRvbmFwMRwwGgYDVQQDExNj
ZXJ0aXNzdWVyLm9uYXAub3JnMQ4wDAYDVQQFEwUxMjM0NTCCASIwDQYJKoZIhvcN
AQEBBQADggEPADCCAQoCggEBALtCvAqSPaRkM5MhNqueIpC/5lRkqfPVY+ll7aBQ
XVNG9s6QhFIf71qcokiT7PHd3dLybdPoz4Yt8/eX03L/Ppu7i5azaoXeABpT3dzg
9dXAMrcNeJpAclMxz8eg4+rXaiO7Pd3I0RBaUFzJXFoiE5NEGfdMozFvtT3sJ5uc
X9FSlqOrKeJZKQRL40FFmgFbTbS6uuxpCccdwZplvqH9hJd4gXNwBY21Nv2ViBxe
pKntb0FylssHtbBH2w1iB0HwAFNJyx/E97X1bSWnBL8UkB+Vn/8znyxO8S/c+rPW
kwMixjclfQsmywkdF949SnePLhzDxUyybFPe4hPnegfTCikCAwEAAaBuMGwGCSqG
SIb3DQEJDjFfMF0wLwYDVR0RBCgwJoIJbG9jYWxob3N0ghNjZXJ0aXNzdWVyLm9u
YXAub3JnhwR/AAABMAsGA1UdDwQEAwICBDAdBgNVHSUEFjAUBggrBgEFBQcDAQYI
KwYBBQUHAwIwDQYJKoZIhvcNAQELBQADggEBAIRAiMx1aOr1p3Aq/ky6ZYWSH327
94H3Kr1+CW7DkR1D0OBFi2YtiWKmf/xjXOOuoi46SSn5lQOI1GxZVrlbqdl/dlik
/C/7EK9XW98/KembLs7XjlsU6kX7Q73C1ZB5j1I8u+ZY+GQOUiCSqZG1FKfKXPe8
Rlwav+OaTJhTpKAekdohqRHg5r+bbM8muVCVb1eNmOXaMgAGN+7rUPej77s9T5TN
+jwoqRFJVaKzzPsPe5ts6pa4mVkz0Ds1D2/u3xFwSTabV9xwka/pLHnYfM712VWg
IE07jjZap9tzp045wDamGmM24GTLoFJdrKNo8HOy0+BF4SN25cGw5HAdLpI=
-----END CERTIFICATE REQUEST-----`)

var checkedLogMessages = [7]string{"Property 'duration'", "Property 'usages'", "Property 'ipAddresses'",
"Property 'isCA'", "Property 'subject.streetAddress'", "Property 'subject.postalCodes'", "Property 'subject.serialNumber'"}

func TestMain(m *testing.M) {
	klog.InitFlags(nil)
	flag.CommandLine.Set("v", "10")
	flag.CommandLine.Set("skip_headers", "true")
	flag.CommandLine.Set("logtostderr", "false")
	flag.CommandLine.Set("alsologtostderr", "false")
	flag.Parse()
	os.Exit(m.Run())
}

func TestLogShouldNotProvideInformationAboutSkippedPropertiesIfNotExistInCSR(t *testing.T) {
	//given
	//prepareCommandLine()
	logger := klogr.New()
	request := prepareCertificateRequest()
	tmpWriteBuffer := getLogBuffer()

	//when
	logCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)
	//then
	//expectedMessage := "warning"
	expectedMessage := "postalCodes"
	//expectedMessage := "Encoded"
	result := logsContainExpectedMessage(logsArray, expectedMessage)
	assert.False(t, result)

	for _, logMsg := range checkedLogMessages {
		assert.False(t, logsContainExpectedMessage(logsArray, logMsg), "Logs not contain: "+logMsg)
	}
}

func TestLogShouldProvideInformationAboutSkippedPropertiesIfExistInCSR(t *testing.T) {
	//given
	//prepareCommandLine()
	logger := klogr.New()
	request := prepareCertificateRequestWithSkipped()
	tmpWriteBuffer := getLogBuffer()

	checkedLogMessages := [7]string{"Property 'duration'", "Property 'usages'", "Property 'ipAddresses'",
		"Property 'isCA'", "Property 'subject.streetAddress'", "Property 'subject.postalCodes'", "Property 'subject.serialNumber'"}

	//when
	logCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)

	//then
	for _, logMsg := range checkedLogMessages {
		assert.True(t, logsContainExpectedMessage(logsArray, logMsg), "Logs not contain: "+logMsg)
	}

}

func TestInfo1234(t *testing.T) {
	//given
	logger := klogr.New()
	request := prepareCertificateRequest()
	tmpWriteBuffer := getLogBuffer()

	//when
	logCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)
	//then
	//expectedMessage := "warning"
	expectedMessage := "postalCodes"
	//expectedMessage := "Encoded"
	result := logsContainExpectedMessage(logsArray, expectedMessage)
	assert.False(t, result)
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
	request.Spec.Request = []byte(csrPEM2)
	return request
}

func prepareCertificateRequestWithSkipped() *cmapi.CertificateRequest {
	request := new(cmapi.CertificateRequest)
	request.Spec.Request = []byte(csrWithSkippedInformation2)
	request.Spec.Duration = &metav1.Duration{Duration: time.Hour}
	request.Spec.IsCA = true
	request.Spec.Usages = cmapi.DefaultKeyUsages()
	return request
}

func logsContainExpectedMessage(array []string, expectedMsg string) bool {
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
