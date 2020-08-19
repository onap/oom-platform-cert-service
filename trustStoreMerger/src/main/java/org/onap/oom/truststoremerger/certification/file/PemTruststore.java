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
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAliasFactory;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.certification.file.provider.PemManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemTruststore extends TruststoreFile {

    private static final boolean APPEND_TO_FILE = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(PemTruststore.class);
    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();
    private final PemManipulator pemManipulator;
    private List<CertificateWithAlias> certificatesToBeSaved = new ArrayList<>();

    public PemTruststore(File truststoreFile, PemManipulator pemManipulator) {
        super(truststoreFile);
        this.pemManipulator = pemManipulator;
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {}", this.getTruststoreFile().getPath());
        List<Certificate> rawCertificates = pemManipulator.getNotEmptyCertificateList(this.getTruststoreFile());
        return wrapCertificates(rawCertificates);
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        LOGGER.debug("Adding certificates to file: {}", this.getTruststoreFile().getPath());
        if (pemManipulator.fileNotContainsPemCertificate(this.getTruststoreFile())) {
            LOGGER.error("File not contains any certificate. File path: {} ", this.getTruststoreFile().getPath());
            throw new MissingTruststoreException("PEM file not contains any certificate");
        }
        certificatesToBeSaved.addAll(certificates);
    }

    @Override
    public void saveFile() throws ExitableException {
        LOGGER.debug("Saving file: {} ", this.getTruststoreFile().getPath());
        List<Certificate> certificates = certificatesToBeSaved.stream()
            .map(CertificateWithAlias::getCertificate)
            .collect(Collectors.toList());
        String certificatesAsString = pemManipulator.transformToStringInPemFormat(certificates);
        appendToFile(certificatesAsString);
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


}
