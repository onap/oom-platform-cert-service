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

package org.onap.oom.truststoremerger.certification.file.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaStorageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaStorageManager.class);

    private final KeyStore keyStore;
    private final File storeFile;
    private final String password;

    private JavaStorageManager(KeyStore keyStore, File storeFile, String password) {
        this.keyStore = keyStore;
        this.storeFile = storeFile;
        this.password = password;
    }

    public static JavaStorageManager createAndLoadFile(KeyStore keyStore, File storeFile, String password)
        throws LoadTruststoreException {
        JavaStorageManager javaStorageManager = new JavaStorageManager(keyStore, storeFile, password);
        javaStorageManager.loadFile();
        return javaStorageManager;
    }

    public void saveFile() throws WriteTruststoreFileException {
        try (FileOutputStream outputStream = new FileOutputStream(storeFile)) {
            keyStore.store(outputStream, password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot write truststore file");
            throw new WriteTruststoreFileException(e);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public String getFilePath() {
        return storeFile.getPath();
    }

    private void loadFile() throws LoadTruststoreException {
        try {
            keyStore.load(new FileInputStream(storeFile), password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot load file: {}", storeFile.getPath());
            throw new LoadTruststoreException(e);
        }
    }
}
