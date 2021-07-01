/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package org.onap.oom.certservice.certification;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.exception.StringToCertificateConversionException;
import org.onap.oom.certservice.certification.model.X509CertificateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class X509CertificateModelFactory {

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----\n";

    private final PemStringToCertificateConverter pemStringToCertificateConverter;
    private final X509CertificateParser x509CertificateParser;

    @Autowired
    public X509CertificateModelFactory(PemStringToCertificateConverter pemStringToCertificateConverter,
        X509CertificateParser x509CertificateParser) {
        this.pemStringToCertificateConverter = pemStringToCertificateConverter;
        this.x509CertificateParser = x509CertificateParser;
    }

    public X509CertificateModel createCertificateModel(StringBase64 base64EncodedCertificate)
        throws CertificateDecryptionException {
        final String certificateString = base64EncodedCertificate.asString()
            .map(this::getFirstCertificateFromCertificateChain)
            .orElseThrow(() -> new CertificateDecryptionException("Incorrect certificate, decryption failed"));
        try {
            final X509Certificate certificate = pemStringToCertificateConverter.convert(certificateString);
            final X500Name subjectData = x509CertificateParser.getSubject(certificate);
            final GeneralName[] sans = x509CertificateParser.getSans(certificate);
            return new X509CertificateModel(certificate, subjectData, sans);
        } catch (StringToCertificateConversionException e) {
            throw new CertificateDecryptionException("Cannot convert certificate", e);

        } catch (CertificateParsingException e) {
            throw new CertificateDecryptionException("Cannot read Subject Alternative Names from certificate");
        }
    }

    private String getFirstCertificateFromCertificateChain(String certificateChain) {
        if (doesNotContainCertificates(certificateChain)) {
            return null;
        }
        return certificateChain.split(END_CERTIFICATE)[0] + END_CERTIFICATE;
    }

    private boolean doesNotContainCertificates(String certificateChain) {
        return !(certificateChain.contains(BEGIN_CERTIFICATE) && certificateChain.contains(END_CERTIFICATE));
    }
}
