/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.truststoremerger.merger.model;

import static org.onap.oom.truststoremerger.api.CertificateConstants.BOUNCY_CASTLE_PROVIDER;
import static org.onap.oom.truststoremerger.api.CertificateConstants.X_509_CERTIFICATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.onap.oom.truststoremerger.common.FileTools;
import org.onap.oom.truststoremerger.merger.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.merger.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.merger.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.merger.model.certificate.CertificateWithAlias;
import org.onap.oom.truststoremerger.merger.model.certificate.CertificateWithAliasFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemTruststore extends Truststore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PemTruststore.class);

    private static final boolean APPEND_TO_FILE = true;

    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();
    private final List<CertificateWithAlias> certificatesToBeSaved = new ArrayList<>();

    public PemTruststore(File storeFile) {
        super(storeFile, new FileTools());
    }

    public List<CertificateWithAlias> getCertificates()
        throws TruststoreDataOperationException, MissingTruststoreException {
        LOGGER.debug("Attempt to read certificates from file: {}", storeFile.getPath());
        if (isFileWithoutPemCertificate()) {
            throw new MissingTruststoreException("File does not contain any certificate");
        }
        List<Certificate> extractedCertificate = extractCertificatesFromFile();
        return wrapCertificates(extractedCertificate);
    }

    public void addCertificates(List<CertificateWithAlias> certificates)
        throws TruststoreDataOperationException, MissingTruststoreException {
        LOGGER.debug("Attempt to add certificates for saving to file");
        if (isFileWithoutPemCertificate()) {
            LOGGER.error("File does not contain any certificate. File path: {} ", storeFile.getPath());
            throw new MissingTruststoreException("File does not contain any certificate");
        }
        certificatesToBeSaved.addAll(certificates);
    }

    public void saveFile() throws WriteTruststoreFileException, TruststoreDataOperationException {
        LOGGER.debug("Attempt to save file: {}", storeFile.getPath());
        List<Certificate> certificates = certificatesToBeSaved.stream()
            .map(CertificateWithAlias::getCertificate)
            .collect(Collectors.toList());
        String certificatesAsString = transformToStringInPemFormat(certificates);
        appendToFile(certificatesAsString);
    }

    boolean isFileWithoutPemCertificate() throws TruststoreDataOperationException {
        List<Certificate> certificateList = extractCertificatesFromFile();
        return certificateList.isEmpty();
    }

    String transformToStringInPemFormat(List<Certificate> certificates) throws TruststoreDataOperationException {
        StringWriter sw = new StringWriter();
        List<PemObjectGenerator> generators = transformToPemGenerators(certificates);
        try (PemWriter pemWriter = new PemWriter(sw)) {
            for (PemObjectGenerator generator : generators) {
                pemWriter.writeObject(generator);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot convert certificates to PEM format");
            throw new TruststoreDataOperationException(e);
        }
        return sw.toString();
    }

    private List<Certificate> extractCertificatesFromFile() throws TruststoreDataOperationException {
        try (FileInputStream inputStream = new FileInputStream(storeFile)) {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory factory = CertificateFactory.getInstance(X_509_CERTIFICATE, BOUNCY_CASTLE_PROVIDER);
            return new ArrayList<>(factory.generateCertificates(inputStream));
        } catch (Exception e) {
            LOGGER.error("Cannot read certificates from file: {}", storeFile.getPath());
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<PemObjectGenerator> transformToPemGenerators(List<Certificate> certificates)
        throws TruststoreDataOperationException {
        List<PemObjectGenerator> generators = new ArrayList<>();
        for (Certificate certificate : certificates) {
            PemObjectGenerator generator = createPemGenerator(certificate);
            generators.add(generator);
        }
        return generators;
    }

    private JcaMiscPEMGenerator createPemGenerator(Certificate certificate)
        throws TruststoreDataOperationException {
        try {
            return new JcaMiscPEMGenerator(certificate);
        } catch (IOException e) {
            LOGGER.error("Cannot convert Certificate Object to PemGenerator Object");
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<CertificateWithAlias> wrapCertificates(List<Certificate> rawCertificates) {
        return rawCertificates.stream()
            .map(factory::createPemCertificate)
            .collect(Collectors.toList());
    }

    private void appendToFile(String certificatesAsString) throws WriteTruststoreFileException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(storeFile, APPEND_TO_FILE);
            fileOutputStream.write(certificatesAsString.getBytes());
        } catch (Exception e) {
            LOGGER.error("Cannot write certificates to file");
            throw new WriteTruststoreFileException(e);
        }
    }
}
