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

package testdata

import (
	"bytes"
	"io/ioutil"
	"log"
	"testing"
)

func ReadFile(filename string) []byte {
	certRequest, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}
	return certRequest
}

func VerifyCertsAreEqualToExpected(t *testing.T, signedPEM []byte, trustedCAs []byte) {
	expectedSignedFilename := "../cmpv2provisioner/testdata/expected_signed.pem"
	expectedTrustedFilename := "../cmpv2provisioner/testdata/expected_trusted.pem"

	VerifyThatConditionIsTrue(AreSlicesEqual(signedPEM,
		ReadFile(expectedSignedFilename)), "Signed pem is different than expected.", t)
	VerifyThatConditionIsTrue(AreSlicesEqual(trustedCAs,
		ReadFile(expectedTrustedFilename)), "Trusted CAs pem is different than expected.", t)
}

func AreSlicesEqual(slice1 []byte, slice2 []byte) bool {
	return bytes.Compare(slice1, slice2) == 0
}

func VerifyThatConditionIsTrue(cond bool, message string, t *testing.T) {
	if !cond {
		t.Fatal(message)
	}
}
