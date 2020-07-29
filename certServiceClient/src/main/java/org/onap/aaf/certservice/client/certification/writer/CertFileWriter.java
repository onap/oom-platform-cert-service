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

package org.onap.oom.certservice.client.certification.writer;

import org.onap.oom.certservice.client.certification.exception.CertFileWriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class CertFileWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertFileWriter.class);
    private final String destPath;

    private CertFileWriter(String destPath) {
        this.destPath = destPath;
    }

    public static CertFileWriter createWithDir(String destPath) {
        createDirIfNotExists(destPath);
        return new CertFileWriter(destPath);
    }

    public void saveData(byte[] data, String filename) throws CertFileWriterException {
        LOGGER.debug("Attempt to save file {} in path {}", filename, destPath);
        try (FileOutputStream outputStream = new FileOutputStream(Path.of(destPath, filename).toString())) {
            outputStream.write(data);
        } catch (IOException e) {
            LOGGER.error("File creation failed, exception message: {}", e.getMessage());
            throw new CertFileWriterException(e);
        }
    }

    private static void createDirIfNotExists(String destPath) {
        File destFolderPath = new File(destPath);
        if (!destFolderPath.exists()) {
            LOGGER.debug("Destination path not exists, subdirectories are created");
            destFolderPath.mkdirs();
        }
    }
}
