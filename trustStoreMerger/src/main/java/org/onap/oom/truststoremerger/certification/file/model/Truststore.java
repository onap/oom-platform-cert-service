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
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.exception.CreateBackupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Truststore {

    private static final String BACKUP_EXTENSION = ".bak";
    private static final Logger LOGGER = LoggerFactory.getLogger(Truststore.class);
    private File truststoreFile;

    Truststore(File truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    public abstract List<CertificateWithAlias> getCertificates() throws ExitableException;

    public abstract void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException;

    public abstract void saveFile() throws ExitableException;

    public File getFile() {
        return truststoreFile;
    }

    public void createBackup() throws CreateBackupException {
        LOGGER.debug("Creating backup of file: {}", truststoreFile.getPath());
        String backupFilePath = truststoreFile.getAbsolutePath() + BACKUP_EXTENSION;
        try (FileOutputStream fileOutputStream = new FileOutputStream(backupFilePath)) {
            Files.copy(truststoreFile.toPath(), fileOutputStream);
        } catch (Exception e) {
            LOGGER.error("Cannot create backup of file: {} ", getFile().getPath());
            throw new CreateBackupException(e);
        }
    }
}
