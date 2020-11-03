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
