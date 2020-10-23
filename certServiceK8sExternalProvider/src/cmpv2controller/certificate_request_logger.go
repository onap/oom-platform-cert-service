package cmpv2controller

import (
	"crypto/x509"
	"encoding/pem"
	"strconv"

	"github.com/go-logr/logr"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
)

func logCertRequestProperties(log logr.Logger, request *cmapi.CertificateRequest) {
	logPropertiesOverridedByCertServiceAPI(log, request)
	logPropertiesNotSupportedByCertService(log, request)
}

func logPropertiesOverridedByCertServiceAPI(log logr.Logger, request *cmapi.CertificateRequest) {
	if request.Spec.Duration != nil && len(request.Spec.Duration.String()) > 0 {
		log.Info("Duration will be override " + request.Spec.Duration.String())
	}
	if request.Spec.Usages != nil && len(request.Spec.Usages) > 0 {
		log.Info("Key usages will be override " + string(request.Spec.Usages[0]))
	}
}

func logPropertiesNotSupportedByCertService(log logr.Logger, request *cmapi.CertificateRequest) {

	block, _ := pem.Decode(request.Spec.Request)
	cert, err := x509.ParseCertificateRequest(block.Bytes)
	if err != nil {
		log.Error(err, "Cannot parse Certificate Request")
	}
	//IP addresses in SANs
	log.Info("WARNING: IpAddress is not supported")
	for i := range cert.IPAddresses {
		p := cert.IPAddresses[i]
		log.Info(p.String())
	}
	//URIs in SANs
	log.Info("WARNING: URI will is not supported")
	for i := range cert.URIs {
		p := cert.URIs[i]
		log.Info(p.String())
	}
	//Email addresses in SANs

	log.Info("WARNING: Email Addres not supported")
	for i := range cert.EmailAddresses {
		p := cert.EmailAddresses[i]
		log.Info(p)
	}

	//isCA parameter
	if request.Spec.IsCA == true {
		log.Info("WARNING: IsCa property is not supported by Cert Service Api  " + strconv.FormatBool(request.Spec.
			IsCA))
	}

	// street address
	if len(cert.Subject.StreetAddress) > 0 {
		log.Info("WARNING: Street addres is not supported by Cert Service API")
	}

	//postal code
	if len(cert.Subject.StreetAddress) > 0 {
		log.Info("WARNING: Postal Code is not supported by Cert Service API")
	}

	// serial number
	if len(cert.Subject.StreetAddress) > 0 {
		log.Info("WARNING: Serial Number is not supported by Cert Service API")
	}

}
