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

package org.onap.oom.certservice.certification.conversion;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.oom.certservice.certification.X509CertificateParser;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;
import org.onap.oom.certservice.certification.exception.StringToCertificateConversionException;
import org.onap.oom.certservice.certification.model.OldCertificateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldCertificateModelFactory {

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----\n";
    private static final PemObjectFactory PEM_OBJECT_FACTORY = new PemObjectFactory();

    private final PemStringToCertificateConverter pemStringToCertificateConverter;
    private final X509CertificateParser x509CertificateParser;

    @Autowired
    public OldCertificateModelFactory(PemStringToCertificateConverter pemStringToCertificateConverter,
        X509CertificateParser x509CertificateParser) {
        this.pemStringToCertificateConverter = pemStringToCertificateConverter;
        this.x509CertificateParser = x509CertificateParser;
    }

    public OldCertificateModel createCertificateModel(StringBase64 base64EncodedCertificate, String encodedOldPrivateKey)
        throws CertificateDecryptionException {
        final String certificateString = base64EncodedCertificate.asString()
            .map(this::getFirstCertificateFromCertificateChain)
            .orElseThrow(() -> new CertificateDecryptionException("Incorrect certificate, decryption failed"));
        try {
            final X509Certificate x509Certificate = pemStringToCertificateConverter.convert(certificateString);
            final X500Name subjectData = x509CertificateParser.getSubject(x509Certificate);
            final GeneralName[] sans = x509CertificateParser.getSans(x509Certificate);
            final Certificate certificate = new JcaX509CertificateHolder(x509Certificate).toASN1Structure();
            final PrivateKey oldPrivateKey = getOldPrivateKeyObject(encodedOldPrivateKey);
            return new OldCertificateModel(certificate, subjectData, sans, oldPrivateKey);
        } catch (StringToCertificateConversionException e) {
            throw new CertificateDecryptionException("Cannot convert certificate", e);
        } catch (CertificateParsingException e) {
            throw new CertificateDecryptionException("Cannot read Subject Alternative Names from certificate");
        } catch (NoSuchAlgorithmException | KeyDecryptionException | CertificateEncodingException | InvalidKeySpecException e) {
            throw new CertificateDecryptionException("Cannot convert certificate or key", e);
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

    private PrivateKey getOldPrivateKeyObject(String encodedOldPrivateKey)
        throws KeyDecryptionException, InvalidKeySpecException, NoSuchAlgorithmException {

        StringBase64 stringBase64 = new StringBase64(encodedOldPrivateKey);
        PemObject pemObject = stringBase64.asString()
            .flatMap(PEM_OBJECT_FACTORY::createPemObject)
            .orElseThrow(
                () -> new KeyDecryptionException("Incorrect Key, decryption failed")
            );
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
