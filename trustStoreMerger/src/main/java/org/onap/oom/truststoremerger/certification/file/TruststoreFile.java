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
import java.nio.file.Files;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;
import org.onap.oom.truststoremerger.certification.file.exception.CreateBackupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TruststoreFile {

    private static final String BACKUP_EXTENSION = ".bak";
    private static final Logger LOGGER = LoggerFactory.getLogger(P12Truststore.class);
    private File truststoreFile;

    TruststoreFile(File truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    public abstract List<CertificateWrapper> getCertificates() throws ExitableException;

    public abstract void addCertificate(List<CertificateWrapper> certificates) throws ExitableException;

    public File getTruststoreFile() {
        return truststoreFile;
    }

    public void createBackup() throws CreateBackupException {
        LOGGER.debug("Creating backup of file: {}", truststoreFile.getPath());
        try {
            String backupFilePath = truststoreFile.getAbsolutePath() + BACKUP_EXTENSION;
            FileOutputStream fileOutputStream = new FileOutputStream(backupFilePath);
            Files.copy(truststoreFile.toPath(), fileOutputStream);
        } catch (Exception e) {
            LOGGER.error("Cannot create backup of file: {} ", getTruststoreFile().getPath());
            throw new CreateBackupException(e);
        }
    }
}
