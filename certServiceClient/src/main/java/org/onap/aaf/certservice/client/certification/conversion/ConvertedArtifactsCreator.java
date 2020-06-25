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

import org.onap.aaf.certservice.client.certification.exception.CertFileWriterException;
import org.onap.aaf.certservice.client.certification.exception.PemConversionException;
import org.onap.aaf.certservice.client.certification.writer.CertFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.List;

public class ConvertedArtifactsCreator implements ArtifactsCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertedArtifactsCreator.class);
    private static final String CERTIFICATE_ALIAS = "certificate";
    private static final String TRUSTED_CERTIFICATE_ALIAS = "trusted-certificate-";
    private static final int PASSWORD_LENGTH = 24;
    private static final String PASS_EXT = "pass";
    private static final String KEYSTORE = "keystore";
    private static final String TRUSTSTORE = "truststore";

    private final String fileExtension;
    private final RandomPasswordGenerator passwordGenerator;
    private final PemConverter converter;
    private final CertFileWriter fileWriter;

    ConvertedArtifactsCreator(CertFileWriter fileWriter, RandomPasswordGenerator passwordGenerator,
                              PemConverter converter, String fileExtension) {
        this.passwordGenerator = passwordGenerator;
        this.converter = converter;
        this.fileWriter = fileWriter;
        this.fileExtension = fileExtension;
    }

    @Override
    public void create(List<String> keystoreData, List<String> truststoreData, PrivateKey privateKey)
            throws PemConversionException, CertFileWriterException {
        createKeystore(keystoreData, privateKey);
        createTruststore(truststoreData);
    }

    private void createKeystore(List<String> data, PrivateKey privateKey)
            throws PemConversionException, CertFileWriterException {
        Password password = passwordGenerator.generate(PASSWORD_LENGTH);
        String keystoreArtifactName = getFilenameWithExtension(KEYSTORE, fileExtension);
        String keystorePass = getFilenameWithExtension(KEYSTORE, PASS_EXT);

        LOGGER.debug("Attempt to create keystore files and saving data. File names: {}, {}", keystoreArtifactName, keystorePass);

        fileWriter.saveData(converter.convertKeystore(data, password, CERTIFICATE_ALIAS, privateKey), keystoreArtifactName);
        fileWriter.saveData(getPasswordAsBytes(password), keystorePass);
    }

    private void createTruststore(List<String> data)
            throws PemConversionException, CertFileWriterException {
        Password password = passwordGenerator.generate(PASSWORD_LENGTH);
        String truststoreArtifactName = getFilenameWithExtension(TRUSTSTORE, fileExtension);
        String truststorePass = getFilenameWithExtension(TRUSTSTORE, PASS_EXT);

        LOGGER.debug("Attempt to create truststore files and saving data. File names: {}, {}", truststoreArtifactName, truststorePass);

        fileWriter.saveData(converter.convertTruststore(data, password, TRUSTED_CERTIFICATE_ALIAS), truststoreArtifactName);
        fileWriter.saveData(getPasswordAsBytes(password), truststorePass);
    }

    private byte[] getPasswordAsBytes(Password password) {
        return password.getCurrentPassword().getBytes();
    }

    private String getFilenameWithExtension(String filename, String extension) {
        return String.format("%s.%s", filename, extension);
    }
}
