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

import java.io.IOException;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.onap.oom.certservice.certification.exception.StringToCertificateConversionException;
import org.springframework.stereotype.Service;

@Service
public class PemStringToCertificateConverter {

    public X509Certificate convert(String certificatePemString) throws StringToCertificateConversionException {
        try (PEMParser pemParser = new PEMParser(new StringReader(certificatePemString))) {
            X509CertificateHolder certHolder = Optional.ofNullable((X509CertificateHolder) pemParser.readObject())
                .orElseThrow(
                    () -> new StringToCertificateConversionException("The certificate could not be converted correctly."));
            return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider())
                .getCertificate(certHolder);
        } catch (IOException | CertificateException e) {
            throw new StringToCertificateConversionException("Exception occurred during certificate conversion.", e);
        }
    }
}
