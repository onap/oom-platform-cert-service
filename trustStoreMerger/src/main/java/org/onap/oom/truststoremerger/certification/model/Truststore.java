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

package org.onap.oom.truststoremerger.certification.model;

import java.io.File;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.exception.CreateBackupException;
import org.onap.oom.truststoremerger.certification.model.processor.TruststoreProcessor;
import org.onap.oom.truststoremerger.certification.model.entry.CertificateWithAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Truststore {

    private static final Logger LOGGER = LoggerFactory.getLogger(Truststore.class);

    private final TruststoreProcessor certProcessor;

    public Truststore(TruststoreProcessor certProcessor) {
        this.certProcessor = certProcessor;
    }

    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Attempt to read certificates from file: {}", certProcessor.getStoreFile().getPath());
        return certProcessor.getNotEmptyCertificateList();
    }

    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        LOGGER.debug("Attempt to add certificates for saving to file");
        certProcessor.addCertificates(certificates);
    }

    public void saveFile() throws ExitableException {
        LOGGER.debug("Attempt to save file: {}", certProcessor.getStoreFile().getPath());
        certProcessor.saveFile();
    }

    public void createBackup() throws CreateBackupException {
        certProcessor.createBackup();
    }

    File getFile() {
        return certProcessor.getStoreFile();
    }
}
