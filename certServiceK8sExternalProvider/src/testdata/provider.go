package testdata

import (
	"k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
)

const (
	SecretName       = "issuer-cert-secret"
	Url              = "https://oom-cert-service:8443/v1/certificate/"
	HealthEndpoint   = "actuator/health"
	CertEndpoint     = "v1/certificate"
	CaName           = "RA"
	KeySecretKey     = "cmpv2Issuer-key.pem"
	CertSecretKey    = "cmpv2Issuer-cert.pem"
	CacertSecretKey  = "cacert.pem"
	Namespace        = "default"
	IssuerObjectName = "fakeIssuer"
	Kind             = "CMPv2Issuer"
	APIVersion       = "v1"
)

func GetValidIssuerAndSecret() (cmpv2api.CMPv2Issuer, v1.Secret) {
	issuer := cmpv2api.CMPv2Issuer{

		TypeMeta: metav1.TypeMeta{
			APIVersion: APIVersion,
			Kind:       Kind,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      IssuerObjectName,
			Namespace: Namespace,
		},
		Spec: GetValidCMPv2IssuerSpec(),
	}
	secret := v1.Secret{

		Data: map[string][]byte{
			KeySecretKey:    KeyBytes,
			CertSecretKey:   CertBytes,
			CacertSecretKey: CacertBytes,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      SecretName,
			Namespace: Namespace,
		},
	}
	secret.Name = SecretName
	return issuer, secret
}

func GetValidCMPv2IssuerSpec() cmpv2api.CMPv2IssuerSpec {
	issuerSpec := cmpv2api.CMPv2IssuerSpec{
		URL:            Url,
		HealthEndpoint: HealthEndpoint,
		CertEndpoint:   CertEndpoint,
		CaName:         CaName,
		CertSecretRef: cmpv2api.SecretKeySelector{
			Name:      SecretName,
			KeyRef:    KeySecretKey,
			CertRef:   CertSecretKey,
			CacertRef: CacertSecretKey,
		},
	}
	return issuerSpec
}

