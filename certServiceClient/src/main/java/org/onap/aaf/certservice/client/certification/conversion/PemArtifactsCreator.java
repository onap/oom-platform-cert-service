/*============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.certification.conversion;

import org.onap.oom.certservice.client.certification.PrivateKeyToPemEncoder;
import org.onap.oom.certservice.client.certification.exception.CertFileWriterException;
import org.onap.oom.certservice.client.certification.exception.PkEncodingException;
import org.onap.oom.certservice.client.certification.writer.CertFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.List;

public class PemArtifactsCreator implements ArtifactsCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PemArtifactsCreator.class);

    private static final String KEY_PEM = "key.pem";
    private static final String KEYSTORE_PEM = "keystore.pem";
    private static final String TRUSTSTORE_PEM = "truststore.pem";

    private final CertFileWriter writer;
    private final PrivateKeyToPemEncoder pkEncoder;

    public PemArtifactsCreator(CertFileWriter writer, PrivateKeyToPemEncoder pkEncoder) {
        this.writer = writer;
        this.pkEncoder = pkEncoder;
    }

    @Override
    public void create(List<String> keystoreData, List<String> truststoreData, PrivateKey privateKey)
            throws PkEncodingException, CertFileWriterException {
        LOGGER.debug("Attempt to create PEM private key file and saving data. File name: {}", KEY_PEM);
        writer.saveData(pkEncoder.encodePrivateKeyToPem(privateKey).getBytes(), KEY_PEM);

        LOGGER.debug("Attempt to create PEM keystore file and saving data. File name: {}", KEYSTORE_PEM);
        writer.saveData(getDataAsBytes(keystoreData), KEYSTORE_PEM);

        LOGGER.debug("Attempt to create PEM truststore file and saving data. File name: {}", TRUSTSTORE_PEM);
        writer.saveData(getDataAsBytes(truststoreData), TRUSTSTORE_PEM);
    }

    private byte[] getDataAsBytes(List<String> data) {
        return String.join("\n", data).getBytes();
    }
}
