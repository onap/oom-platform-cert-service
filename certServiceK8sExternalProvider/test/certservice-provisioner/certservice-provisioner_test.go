package certservice_provisioner

import (
	certservice_provisioner "onap.org/oom-certservice/k8s-external-provider/src/certservice-provisioner"
	"testing"
)

func TestSignCertificate(t *testing.T) {

	certservice_provisioner.SignCertificate()

	t.Logf("Everything is OK.")
}
