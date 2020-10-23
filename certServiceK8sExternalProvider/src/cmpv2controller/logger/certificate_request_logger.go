package cmpv2controller

import (
	"crypto/x509"
	"encoding/pem"
	"net"
	"net/url"
	"strconv"

	"github.com/go-logr/logr"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
)

const (
	CertServiceName = "Cert Service API"
)

func logCertRequestProperties(log logr.Logger, request *cmapi.CertificateRequest) {
	logPropertiesOverriddenByCertServiceAPI(log, request)
	logPropertiesNotSupportedByCertService(log, request)
}

func logPropertiesOverriddenByCertServiceAPI(log logr.Logger, request *cmapi.CertificateRequest) {
	if request.Spec.Duration != nil && len(request.Spec.Duration.String()) > 0 {
		log.Info(getOverriddenMessage("duration", request.Spec.Duration.Duration.String()))
	}
	if request.Spec.Usages != nil && len(request.Spec.Usages) > 0 {
		log.Info(getOverriddenMessage("usages", extractUsages(request.Spec.Usages)))
	}
}

func extractUsages(usages []cmapi.KeyUsage) string {
	values := ""
	for _, usage := range usages {
		values = values + string(usage) + ", "
	}
	return values
}

func getOverriddenMessage(property string, values string) string {
	return "Property '" + property + "' with value: " + values + ", will be overridden by " + CertServiceName
}

func logPropertiesNotSupportedByCertService(log logr.Logger, request *cmapi.CertificateRequest) {

	block, _ := pem.Decode(request.Spec.Request)
	cert, err := x509.ParseCertificateRequest(block.Bytes)
	if err != nil {
		log.Error(err, "Cannot parse Certificate Request")
	}
	//IP addresses in SANs
	if len(cert.IPAddresses) > 0 {
		log.Info(getNotSupportedMessage("ipAddresses", extractIPAddresses(cert.IPAddresses)))
	}

	//TODO in test
	//URIs in SANs
	if len(cert.URIs) > 0 {
		log.Info(getNotSupportedMessage("uris", extractURIs(cert.URIs)))
	}

	//Email addresses in SANs
	if len(cert.EmailAddresses) > 0 {
		log.Info(getNotSupportedMessage("emailAddresses", extractStringArray(cert.EmailAddresses)))
	}

	if request.Spec.IsCA == true {
		log.Info(getNotSupportedMessage("isCA", strconv.FormatBool(request.Spec.IsCA)))
	}

	if len(cert.Subject.StreetAddress) > 0 {
		log.Info(getNotSupportedMessage("subject.streetAddress", extractStringArray(cert.Subject.StreetAddress)))
	}

	if len(cert.Subject.PostalCode) > 0 {
		log.Info(getNotSupportedMessage("subject.postalCodes", extractStringArray(cert.Subject.PostalCode)))
	}

	if len(cert.Subject.SerialNumber) > 0 {
		log.Info(getNotSupportedMessage("subject.serialNumber", cert.Subject.SerialNumber))
	}

}

func extractStringArray(strArray []string) string {
	values := ""
	for _, emailSANs := range strArray {
		values = values + emailSANs + ", "
	}
	return values
}

func extractURIs(URIs []*url.URL) string {
	values := ""
	for _, uri := range URIs {
		values = values + uri.String() + ", "
	}
	return values
}

func extractIPAddresses(addresses []net.IP) string {
	values := ""
	for _, ipAddress := range addresses {
		values = values + ipAddress.String() + ", "
	}
	return values
}

func getNotSupportedMessage(property string, values string) string {
	return "WARNING: Property '" + property + "' with value: " + values + " is not supported by " + CertServiceName
}
