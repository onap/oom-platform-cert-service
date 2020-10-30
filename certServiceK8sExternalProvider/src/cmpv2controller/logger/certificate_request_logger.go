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

	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
)

const (
	CertServiceName = "Cert Service API"
	CMPv2ServerName = "CMPv2 Server"
)

func LogCertRequestProperties(log leveledlogger.LeveledLogger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {
	logSupportedProperties(log, request, csr)
	logPropertiesNotSupportedByCertService(log, request, csr)
	logPropertiesOverriddenByCMPv2Server(log, request)
}

func logSupportedProperties(log leveledlogger.LeveledLogger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {
	logSupportedProperty(log, csr.Subject.Organization, "organization")
	logSupportedProperty(log, csr.Subject.OrganizationalUnit, "organization unit")
	logSupportedProperty(log, csr.Subject.Country, "country")
	logSupportedProperty(log, csr.Subject.Province, "state")
	logSupportedProperty(log, csr.Subject.Locality, "location")
	logSupportedProperty(log, csr.DNSNames, "dns names")
}

func logSupportedProperty(log leveledlogger.LeveledLogger, values []string, propertyName string) {
	if len(values) > 0 {
		log.Info(getSupportedMessage(propertyName, extractStringArray(values)))
	}
}

func logPropertiesOverriddenByCMPv2Server(log leveledlogger.LeveledLogger, request *cmapi.CertificateRequest) {
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

func logPropertiesNotSupportedByCertService(log leveledlogger.LeveledLogger, request *cmapi.CertificateRequest, csr *x509.CertificateRequest) {

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
	for _, val := range strArray {
		values = values + val + ", "
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
	return "Property '" + property + "' with value: " + value + " will be sent in certificate signing request to " + CMPv2ServerName
}

func getOverriddenMessage(property string, values string) string {
	return "Property '" + property + "' with value: " + values + " will be overridden by " + CMPv2ServerName
}
