package x509

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestShouldDecodeCSR(t *testing.T) {
	csr, err := DecodeCSR([]byte(certificateSignRequest))

	assert.Nil(t, err)
	assert.Equal(t, "ONAP", csr.Subject.Organization[0])
}
