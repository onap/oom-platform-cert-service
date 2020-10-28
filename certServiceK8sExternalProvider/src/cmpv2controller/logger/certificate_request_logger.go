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
	x509 "crypto/x509"
	"encoding/pem"
	"net"
	"net/url"
	"strconv"

	"github.com/go-logr/logr"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
)

const (
	CertServiceName = "Cert Service API"
	CMPv2ServerName = "CMPv2 Server"
)

func LogCertRequestProperties(log logr.Logger, request *cmapi.CertificateRequest) {
	log.Info("Processing CSR...")
	block, _ := pem.Decode(request.Spec.Request)
	csr, err := x509.ParseCertificateRequest(block.Bytes)
	if err != nil {
		log.Error(err, "Cannot parse Certificate Signing Request")
	} else {
		logSupportedProperties(log, request, csr)
		logPropertiesNotSupportedByCertService(log, request, csr)
	}
	logPropertiesOverriddenByCMPv2Server(log, request)
}

func logSupportedProperties(log logr.Logger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {
	if len(csr.Subject.Organization) > 0 {
		log.Info(getSupportedMessage("organization", extractStringArray(csr.Subject.Organization)))
	}
	if len(csr.Subject.OrganizationalUnit) > 0 {
		log.Info(getSupportedMessage("organization unit", extractStringArray(csr.Subject.OrganizationalUnit)))
	}
	if len(csr.Subject.Country) > 0 {
		log.Info(getSupportedMessage("country", extractStringArray(csr.Subject.Country)))
	}
	if len(csr.Subject.Province) > 0 {
		log.Info(getSupportedMessage("state", extractStringArray(csr.Subject.Province)))
	}
	if len(csr.Subject.Locality) > 0 {
		log.Info(getSupportedMessage("location", extractStringArray(csr.Subject.Locality)))
	}
	if len(csr.DNSNames) > 0 {
		log.Info(getSupportedMessage("dns names", extractStringArray(csr.DNSNames)))
	}

}

func logPropertiesOverriddenByCMPv2Server(log logr.Logger, request *cmapi.CertificateRequest) {
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

func logPropertiesNotSupportedByCertService(log logr.Logger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {

	//IP addresses in SANs
	if len(csr.IPAddresses) > 0 {
		log.Info(getNotSupportedMessage("ipAddresses", extractIPAddresses(csr.IPAddresses)))
	}
	//URIs in SANs
	if len(csr.URIs) > 0 {
		log.Info(getNotSupportedMessage("uris", extractURIs(csr.URIs)))
	}

	//Email addresses in SANs
	if len(csr.EmailAddresses) > 0 {
		log.Info(getNotSupportedMessage("emailAddresses", extractStringArray(csr.EmailAddresses)))
	}

	if request.Spec.IsCA == true {
		log.Info(getNotSupportedMessage("isCA", strconv.FormatBool(request.Spec.IsCA)))
	}

	if len(csr.Subject.StreetAddress) > 0 {
		log.Info(getNotSupportedMessage("subject.streetAddress", extractStringArray(csr.Subject.StreetAddress)))
	}

	if len(csr.Subject.PostalCode) > 0 {
		log.Info(getNotSupportedMessage("subject.postalCodes", extractStringArray(csr.Subject.PostalCode)))
	}

	if len(csr.Subject.SerialNumber) > 0 {
		log.Info(getNotSupportedMessage("subject.serialNumber", csr.Subject.SerialNumber))
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

func getNotSupportedMessage(property string, value string) string {
	return "WARNING: Property '" + property + "' with value: " + value + " is not supported by " + CertServiceName
}

func getSupportedMessage(property string, value string) string {
	return "Property '" + property + "' with value: " + value + " [OK]"
}

func getOverriddenMessage(property string, values string) string {
	return "Property '" + property + "' with value: " + values + ", will be overridden by " + CMPv2ServerName
}
