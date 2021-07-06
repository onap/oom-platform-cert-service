package util

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"strconv"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	core "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	revisionAnnotation                 = "cert-manager.io/certificate-revision"
	certificateConfigurationAnnotation = "kubectl.kubernetes.io/last-applied-configuration"
	oldCertificateSecretKey            = "tls.crt"
	oldPrivateKeySecretKey             = "tls.key"
)

func CheckIfCertificateUpdateAndRetrieveOldCertificateAndPk(
	k8sClient client.Client,
	certificateRequest *cmapi.CertificateRequest,
	ctx context.Context,
) (bool, string, string) {
	if !IsUpdateCertificateRevision(certificateRequest) {
		return false, "", ""
	}
	certificate, privateKey := RetrieveOldCertificateAndPk(k8sClient, certificateRequest, ctx)
	areCertAndPkPresent := certificate != "" && privateKey != ""
	return areCertAndPkPresent, certificate, privateKey
}

func IsUpdateCertificateRevision(certificateRequest *cmapi.CertificateRequest) bool {
	revision, err := strconv.Atoi(certificateRequest.ObjectMeta.Annotations[revisionAnnotation])
	if err != nil {
		return false
	}
	return revision > 1
}

func RetrieveOldCertificateAndPk(
	k8sClient client.Client,
	certificateRequest *cmapi.CertificateRequest,
	ctx context.Context,
) (string, string) {
	certificateConfigString := certificateRequest.ObjectMeta.Annotations[certificateConfigurationAnnotation]
	var certificateConfig cmapi.Certificate
	if err := json.Unmarshal([]byte(certificateConfigString), &certificateConfig); err != nil {
		return "", ""
	}
	oldCertificateSecretName := certificateConfig.Spec.SecretName
	oldCertificateSecretNamespacedName := types.NamespacedName{
		Namespace: certificateConfig.Namespace,
		Name:      oldCertificateSecretName,
	}
	var oldCertificateSecret core.Secret
	if err := k8sClient.Get(ctx, oldCertificateSecretNamespacedName, &oldCertificateSecret); err != nil {
		return "", ""
	}
	oldCertificateString := base64.StdEncoding.EncodeToString(oldCertificateSecret.Data[oldCertificateSecretKey])
	oldPrivateKeyString := base64.StdEncoding.EncodeToString(oldCertificateSecret.Data[oldPrivateKeySecretKey])
	return oldCertificateString, oldPrivateKeyString
}
