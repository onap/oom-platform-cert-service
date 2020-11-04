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

package logger

import (
	"bytes"

	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"io/ioutil"
	"log"
	"os"
	"strings"
	"testing"
	"time"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	x509utils "onap.org/oom-certservice/k8s-external-provider/src/x509"
)

var checkedLogMessages = [7]string{"Property 'duration'", "Property 'usages'", "Property 'ipAddresses'",
	"Property 'isCA'", "Property 'subject.streetAddress'", "Property 'subject.postalCodes'",
	"Property 'subject.serialNumber'"}

var supportedProperties = [7]string{"Property 'organization'", "Property 'organization unit'", "Property 'country'",
	"Property 'state'", "Property 'location'", "Property 'dns names'"}

const RESULT_LOG = "testdata/test_result.log"

func TestMain(m *testing.M) {
	leveledlogger.SetConfigFileName("testdata/test_logger_config.json")
	os.Exit(m.Run())
}

func TestLogShouldNotProvideInformationAboutSkippedPropertiesIfNotExistInCSR(t *testing.T) {
	//given
	logger := leveledlogger.GetLoggerWithName("test")
	request := getCertificateRequestWithoutSkippedProperties()

	csr, err := x509utils.DecodeCSR(request.Spec.Request)
	if err != nil {
		assert.FailNow(t, "Could not parse Certificate Sign Request")
	}

	//when
	LogCertRequestProperties(logger, request, csr)
	logsArray := convertLogFileToStringArray(RESULT_LOG)

	//then
	for _, logMsg := range checkedLogMessages {
		assert.False(t, logsContainExpectedMessage(logsArray, logMsg), "Logs contain: "+logMsg+", but should not")
	}
	removeFile(RESULT_LOG)
}

func TestLogShouldProvideInformationAboutSkippedPropertiesIfExistInCSR(t *testing.T) {
	//given
	logger := leveledlogger.GetLoggerWithName("test")
	request := getCertificateRequestWithSkippedProperties()

	csr, err := x509utils.DecodeCSR(request.Spec.Request)
	if err != nil {
		assert.FailNow(t, "Could not parse Certificate Sign Request")
	}

	//when
	LogCertRequestProperties(logger, request, csr)
	logsArray := convertLogFileToStringArray(RESULT_LOG)

	//then
	for _, logMsg := range checkedLogMessages {
		assert.True(t, logsContainExpectedMessage(logsArray, logMsg), "Logs not contain: "+logMsg)
	}
	removeFile(RESULT_LOG)
}

func TestLogShouldListSupportedProperties(t *testing.T) {
	//given
	logger := leveledlogger.GetLoggerWithName("test")
	request := getCertificateRequestWithoutSkippedProperties()

	csr, err := x509utils.DecodeCSR(request.Spec.Request)
	if err != nil {
		assert.FailNow(t, "Could not parse Certificate Sign Request")
	}

	//when
	LogCertRequestProperties(logger, request, csr)
	logsArray := convertLogFileToStringArray(RESULT_LOG)

	//then
	for _, logMsg := range supportedProperties {
		assert.True(t, logsContainExpectedMessage(logsArray, logMsg), "Logs not contain: "+logMsg)
	}
	removeFile(RESULT_LOG)
}

func getCertificateRequestWithoutSkippedProperties() *cmapi.CertificateRequest {
	request := new(cmapi.CertificateRequest)
	request.Spec.Request = []byte(csrWithoutSkippedProperties)
	return request
}

func getCertificateRequestWithSkippedProperties() *cmapi.CertificateRequest {
	request := new(cmapi.CertificateRequest)
	request.Spec.Request = []byte(csrWithSkippedProperties)
	request.Spec.Duration = &metav1.Duration{Duration: time.Hour}
	request.Spec.IsCA = true
	request.Spec.Usages = cmapi.DefaultKeyUsages()
	return request
}

func convertBufferToStringArray(buffer *bytes.Buffer) []string {
	return strings.Split(buffer.String(), "\n")
}

func convertLogFileToStringArray(filename string) []string {
	buffer := bytes.NewBuffer(make([]byte, 0))
	buffer.Write(readFile(filename))
	return convertBufferToStringArray(buffer)
}

func readFile(filename string) []byte {
	certRequest, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}
	return certRequest
}

func removeFile(fileName string) {
	if _, err := os.Stat(fileName); err == nil {
		e := os.Remove(fileName)
		if e != nil {
			log.Fatal(e)
		}
	}
}

func logsContainExpectedMessage(array []string, expectedMsg string) bool {
	for _, logMsg := range array {
		if strings.Contains(logMsg, expectedMsg) {
			return true
		}
	}
	return false
}
