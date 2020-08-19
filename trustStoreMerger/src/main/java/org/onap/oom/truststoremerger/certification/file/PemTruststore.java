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

package org.onap.oom.truststoremerger.certification.file;

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
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAliasFactory;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemTruststore extends TruststoreFile {

    private static final String X_509_INSTANCE = "X.509";
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final boolean APPEND_TO_FILE = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(P12Truststore.class);
    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();
    private List<CertificateWithAlias> certificatesToBeSaved = new ArrayList<>();

    public PemTruststore(File truststoreFile) {
        super(truststoreFile);
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {}", this.getTruststoreFile().getPath());
        List<Certificate> rawCertificates = getRawCertificates(this.getTruststoreFile());
        return wrapCertificates(rawCertificates);
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) {
        LOGGER.debug("Adding certificates to file: {}", this.getTruststoreFile().getPath());
        certificatesToBeSaved.addAll(certificates);
    }

    @Override
    public void saveFile() throws ExitableException {
        LOGGER.debug("Saving file: {} ", this.getTruststoreFile().getPath());
        String certificatesAsString = getCertificatesAsString(certificatesToBeSaved);
        appendToFile(certificatesAsString);
    }

    private List<Certificate> getRawCertificates(File file)
        throws MissingTruststoreException, TruststoreDataOperationException {
        List<Certificate> extractedCertificate = extractCertificatesFromFile(file);
        if (extractedCertificate.isEmpty()) {
            throw new MissingTruststoreException("PEM file not contains any certificate");
        }
        return extractedCertificate;
    }

    private List<Certificate> extractCertificatesFromFile(File file) throws TruststoreDataOperationException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory factory = CertificateFactory.getInstance(X_509_INSTANCE, BOUNCY_CASTLE_PROVIDER);
            return new ArrayList<>(factory.generateCertificates(inputStream));
        } catch (Exception e) {
            LOGGER.error("Cannot read certificates from file: {}", file.getPath());
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
            FileOutputStream fileOutputStream = new FileOutputStream(this.getTruststoreFile(), APPEND_TO_FILE);
            fileOutputStream.write(certificatesAsString.getBytes());
        } catch (Exception e) {
            LOGGER.error("Cannot write certificates to file");
            throw new WriteTruststoreFileException(e);
        }
    }

    private String getCertificatesAsString(List<CertificateWithAlias> certificates) throws ExitableException {
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

    private List<PemObjectGenerator> transformToPemGenerators(List<CertificateWithAlias> certificates)
        throws ExitableException {
        List<PemObjectGenerator> generators = new ArrayList<>();
        for (CertificateWithAlias certificatesWithAlias : certificates) {
            PemObjectGenerator generator = createPemGenerator(certificatesWithAlias.getCertificate());
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


}
