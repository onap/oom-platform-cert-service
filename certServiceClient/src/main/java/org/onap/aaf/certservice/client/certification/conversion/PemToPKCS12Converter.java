/*============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.certification.conversion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Optional;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.onap.aaf.certservice.client.certification.exception.PemToPKCS12ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PemToPKCS12Converter {

    private final static String PKCS12 = "PKCS12";
    private final static String PASSWORD_ERROR_MSG = "Password should be min. 16 chars long and should contain only alphanumeric characters and special characters like Underscore (_), Dollar ($) and Pound (#)";
    private final LoadStoreParameter EMPTY_KEYSTORE_CONFIGURATION = null;
    private final Logger LOGGER = LoggerFactory.getLogger(PemToPKCS12Converter.class);

    byte[] convertKeystore(List<String> certificateChain, Password password, String alias, PrivateKey privateKey)
        throws PemToPKCS12ConverterException {
        LOGGER.debug("Converting PEM certificates to PKCS12 keystore.");
        return convert(certificateChain, password, certs -> getKeyStore(alias, password, certs, privateKey));
    }

    byte[] convertTruststore(List<String> trustAnchors, Password password, String alias)
        throws PemToPKCS12ConverterException {
        LOGGER.debug("Converting PEM certificates to PKCS12 truststore.");
        return convert(trustAnchors, password, certs -> getTrustStore(alias, certs));
    }

    private byte[] convert(List<String> certificates, Password password, StoreEntryOperation operation)
        throws PemToPKCS12ConverterException {
        checkPassword(password);
        final Certificate[] X509Certificates = convertToCertificateArray(certificates);
        return getKeyStoreBytes(password, operation, X509Certificates);
    }

    private void checkPassword(Password password) throws PemToPKCS12ConverterException {
        if (!password.isCorrectPasswordPattern()) {
            LOGGER.error(PASSWORD_ERROR_MSG);
            throw new PemToPKCS12ConverterException(PASSWORD_ERROR_MSG);
        }
    }

    private byte[] getKeyStoreBytes(Password password, StoreEntryOperation op, Certificate[] x509Certificates)
        throws PemToPKCS12ConverterException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            KeyStore ks = op.getStore(x509Certificates);
            ks.store(bos, password.toCharArray());
            return bos.toByteArray();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.error("Pem to PKCS12 converter failed, exception message: {}", e.getMessage());
            throw new PemToPKCS12ConverterException(e);
        }
    }

    private KeyStore getKeyStore(String alias, Password password, Certificate[] certificates, PrivateKey privateKey)
        throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = getKeyStoreInstance();
        ks.setKeyEntry(alias, privateKey, password.toCharArray(), certificates);
        return ks;
    }

    private KeyStore getTrustStore(String alias, Certificate[] certificates)
        throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = getKeyStoreInstance();
        long i = 1L;
        for (Certificate c : certificates) {
            ks.setCertificateEntry(alias + i++, c);
        }
        return ks;
    }

    private KeyStore getKeyStoreInstance()
        throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(PKCS12);
        ks.load(EMPTY_KEYSTORE_CONFIGURATION);
        return ks;
    }

    private Certificate[] convertToCertificateArray(List<String> certificates)
        throws PemToPKCS12ConverterException {
        Certificate[] parsedCertificates = new Certificate[certificates.size()];
        for (String certificate : certificates) {
            parsedCertificates[certificates.indexOf(certificate)] = parseCertificate(certificate);
        }
        return parsedCertificates;
    }

    private Certificate parseCertificate(String certificate) throws PemToPKCS12ConverterException {
        try (PEMParser pem = new PEMParser(new StringReader(certificate))) {
            X509CertificateHolder certHolder = Optional.ofNullable((X509CertificateHolder) pem.readObject())
                .orElseThrow(
                    () -> new PemToPKCS12ConverterException("The certificate couldn't be parsed correctly. " + certificate));
            return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider())
                .getCertificate(certHolder);
        } catch (IOException | CertificateException e) {
            LOGGER.error("Certificates conversion failed, exception message: {}", e.getMessage());
            throw new PemToPKCS12ConverterException(e);
        }
    }
}
