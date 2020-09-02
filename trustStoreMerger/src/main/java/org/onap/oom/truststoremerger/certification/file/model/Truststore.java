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

package org.onap.oom.truststoremerger.certification.file.model;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.exception.CreateBackupException;
import org.onap.oom.truststoremerger.certification.file.provider.CertificateController;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Truststore {

    private static final Logger LOGGER = LoggerFactory.getLogger(Truststore.class);
    private static final String BACKUP_EXTENSION = ".bak";

    private final File file;
    private final CertificateController certController;

    public Truststore(File file, CertificateController certController) {
        this.file = file;
        this.certController = certController;
    }

    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Attempt ro read certificates from file: {}", this.getFile().getPath());
        return certController.getNotEmptyCertificateList();
    }

    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        LOGGER.debug("Attempt to add certificates for saving to file");
        certController.addCertificates(certificates);
    }

    public void saveFile() throws ExitableException {
        LOGGER.debug("Attempt to save file: {}", this.getFile().getPath());
        certController.saveFile();
    }

    public void createBackup() throws CreateBackupException {
        LOGGER.debug("Create backup of file: {}", file.getPath());
        String backupFilePath = file.getAbsolutePath() + BACKUP_EXTENSION;
        try (FileOutputStream fileOutputStream = new FileOutputStream(backupFilePath)) {
            Files.copy(file.toPath(), fileOutputStream);
        } catch (Exception e) {
            LOGGER.error("Cannot create backup of file: {} ", getFile().getPath());
            throw new CreateBackupException(e);
        }
    }

    public File getFile() {
        return file;
    }
}
