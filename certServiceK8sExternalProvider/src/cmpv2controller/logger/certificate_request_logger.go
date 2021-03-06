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
	"net"
	"net/url"
	"strconv"
	"strings"

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"

	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
)

const (
	CertServiceName = "Cert Service API"
	CMPv2ServerName = "CMPv2 Server"
)

func LogCertRequestProperties(log leveledlogger.Logger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {
	logSupportedProperties(log, csr)
	logPropertiesNotSupportedByCertService(log, request, csr)
	logPropertiesOverriddenByCMPv2Server(log, request)
}

func logSupportedProperties(log leveledlogger.Logger, csr *x509.CertificateRequest) {
	logSupportedSingleValueProperty(log, csr.Subject.CommonName, "common name")
	logSupportedMultiValueProperty(log, csr.Subject.Organization, "organization")
	logSupportedMultiValueProperty(log, csr.Subject.OrganizationalUnit, "organization unit")
	logSupportedMultiValueProperty(log, csr.Subject.Country, "country")
	logSupportedMultiValueProperty(log, csr.Subject.Province, "state")
	logSupportedMultiValueProperty(log, csr.Subject.Locality, "location")
	logSupportedMultiValueProperty(log, csr.DNSNames, "dns names")
	logSupportedMultiValueProperty(log, csr.EmailAddresses, "email addresses")
	logSupportedMultiValueProperty(log, mapIpAddressesToText(csr.IPAddresses), "ipAddresses")
	logSupportedMultiValueProperty(log, mapUrisToText(csr.URIs), "uris")
}

func logSupportedMultiValueProperty(log leveledlogger.Logger, values []string, propertyName string) {
	if len(values) > 0 {
		log.Info(getSupportedMessage(propertyName, extractStringArray(values)))
	}
}

func logSupportedSingleValueProperty(log leveledlogger.Logger, value string, propertyName string) {
	log.Info(getSupportedMessage(propertyName, value))
}

func logPropertiesOverriddenByCMPv2Server(log leveledlogger.Logger, request *cmapi.CertificateRequest) {
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

func logPropertiesNotSupportedByCertService(log leveledlogger.Logger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {
	if request.Spec.IsCA == true {
		log.Warning(getNotSupportedMessage("isCA", strconv.FormatBool(request.Spec.IsCA)))
	}

	if len(csr.Subject.StreetAddress) > 0 {
		log.Warning(getNotSupportedMessage("subject.streetAddress", extractStringArray(csr.Subject.StreetAddress)))
	}

	if len(csr.Subject.PostalCode) > 0 {
		log.Warning(getNotSupportedMessage("subject.postalCodes", extractStringArray(csr.Subject.PostalCode)))
	}

	if len(csr.Subject.SerialNumber) > 0 {
		log.Warning(getNotSupportedMessage("subject.serialNumber", csr.Subject.SerialNumber))
	}

}

func extractStringArray(strArray []string) string {
	separator := ","
	return strings.Join(strArray, separator)
}

func mapUrisToText(uris []*url.URL) []string {
	urisAsText := make([]string, len(uris))
	for i, ipAddress := range uris {
		urisAsText[i] = ipAddress.String()
	}
	return urisAsText
}

func mapIpAddressesToText(addresses []net.IP) []string {
	ipsAsText := make([]string, len(addresses))
	for i, ipAddress := range addresses {
		ipsAsText[i] = ipAddress.String()
	}
	return ipsAsText
}

func getSupportedMessage(property string, value string) string {
	return "+ property '" + property + "' with value '" + value + "' will be sent in certificate signing request to " + CMPv2ServerName
}

func getNotSupportedMessage(property string, value string) string {
	return "- property '" + property + "' with value '" + value + "' is not supported by " + CertServiceName
}

func getOverriddenMessage(property string, values string) string {
	return "* property '" + property + "' with value '" + values + "' will be overridden by " + CMPv2ServerName
}
