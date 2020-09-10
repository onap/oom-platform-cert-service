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

package org.onap.oom.truststoremerger.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.onap.oom.truststoremerger.merger.exception.CreateBackupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileTools {

    private static final Logger LOG = LoggerFactory.getLogger(FileTools.class);
    private static final String BACKUP_EXTENSION = ".bak";

    public void createBackup(File file) throws CreateBackupException {
        LOG.debug("Try to create a backup of the file: {}", file.getPath());
        String backupFilePath = file.getAbsolutePath() + BACKUP_EXTENSION;
        try (FileOutputStream fileOutputStream = new FileOutputStream(backupFilePath)) {
            Files.copy(file.toPath(), fileOutputStream);
        } catch (Exception e) {
            LOG.error("Could not create backup of the file: '{}'", file.getPath());
            throw new CreateBackupException(e);
        }
        LOG.debug("Backup file created: '{}'", backupFilePath);
    }

    public void copy(File source, File destination) throws IOException {
        LOG.debug("Try to copy from '{}' to '{}'.", source.getAbsolutePath(), destination.getAbsolutePath());
        FileUtils.copyFile(source, destination);
        LOG.debug("File copied from '{}' to '{}'.", source.getAbsolutePath(),
            destination.getAbsolutePath());
    }
}
