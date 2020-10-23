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
	"flag"
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

var checkedLogMessages = [7]string{"Property 'duration'", "Property 'usages'", "Property 'ipAddresses'",
	"Property 'isCA'", "Property 'subject.streetAddress'", "Property 'subject.postalCodes'",
	"Property 'subject.serialNumber'"}

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
	logger := klogr.New()
	request := getCertificateRequestWithoutSkippedProperties()
	tmpWriteBuffer := getLogBuffer()

	//when
	LogCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)
	//then
	for _, logMsg := range checkedLogMessages {
		assert.False(t, logsContainExpectedMessage(logsArray, logMsg), "Logs contain: "+logMsg+", but should not")
	}
}

func TestLogShouldProvideInformationAboutSkippedPropertiesIfExistInCSR(t *testing.T) {
	//given
	logger := klogr.New()
	request := getCertificateRequestWithSkippedProperties()
	tmpWriteBuffer := getLogBuffer()

	//when
	LogCertRequestProperties(logger, request)
	closeLogBuffer()
	logsArray := convertBufferToStringArray(tmpWriteBuffer)

	//then
	for _, logMsg := range checkedLogMessages {
		assert.True(t, logsContainExpectedMessage(logsArray, logMsg), "Logs not contain: "+logMsg)
	}
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

func getLogBuffer() *bytes.Buffer {
	tmpWriteBuffer := bytes.NewBuffer(nil)
	klog.SetOutput(tmpWriteBuffer)
	return tmpWriteBuffer
}

func closeLogBuffer() {
	klog.Flush()
}

func convertBufferToStringArray(buffer *bytes.Buffer) []string {
	return strings.Split(buffer.String(), "\n")
}

func logsContainExpectedMessage(array []string, expectedMsg string) bool {
	for _, logMsg := range array {
		if strings.Contains(logMsg, expectedMsg) {
			return true
		}
	}
	return false
}
