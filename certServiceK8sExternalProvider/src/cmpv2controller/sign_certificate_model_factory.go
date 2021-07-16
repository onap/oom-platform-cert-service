package cmpv2controller

import (
	"context"
	"github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller/util"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2provisioner/csr"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
	"onap.org/oom-certservice/k8s-external-provider/src/model"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

func CreateSignCertificateModel(client client.Client, certificateRequest *v1.CertificateRequest, ctx context.Context, privateKeyBytes []byte) (model.SignCertificateModel, error) {
	log := leveledlogger.GetLoggerWithName("certservice-model-factory")
	oldCertificateBytes, oldPrivateKeyBytes := util.RetrieveOldCertificateAndPkForCertificateUpdate(
		client, certificateRequest, ctx)

	csrBytes := certificateRequest.Spec.Request
	log.Debug("Original CSR PEM: ", "bytes", csrBytes)

	filteredCsrBytes, err := csr.FilterFieldsFromCSR(csrBytes, privateKeyBytes)
	if err != nil {
		return model.SignCertificateModel{}, err
	}

	log.Debug("Filtered out CSR PEM: ", "bytes", filteredCsrBytes)

	signCertificateModel := model.SignCertificateModel{
		CertificateRequest:  certificateRequest,
		FilteredCsr:         filteredCsrBytes,
		PrivateKeyBytes:     privateKeyBytes,
		OldCertificateBytes: oldCertificateBytes,
		OldPrivateKeyBytes:  oldPrivateKeyBytes,
	}
	return signCertificateModel, nil
}
