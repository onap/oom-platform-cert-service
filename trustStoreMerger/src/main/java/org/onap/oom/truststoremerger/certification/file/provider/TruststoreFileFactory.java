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

import org.onap.oom.truststoremerger.certification.file.JksTruststore;
import org.onap.oom.truststoremerger.certification.file.P12Truststore;
import org.onap.oom.truststoremerger.certification.file.PemTruststore;
import org.onap.oom.truststoremerger.certification.file.TruststoreFile;

import java.io.File;

public class TruststoreFileFactory {

    private static final String JKS_EXTENSION = ".jks";
    private static final String P12_EXTENSION = ".p12";
    private static final String PEM_EXTENSION = ".pem";
    private static final String FILE_DOES_NOT_EXIST_MSG_TEMPLATE = "File: %s does not exist";
    private static final String UNKNOWN_TRUSTSTORE_TYPE_MSG_TEMPLATE = "Unknown truststore extension type: %s";

    private final FileManager fileManager;
    private final PasswordReader passwordReader;

    public TruststoreFileFactory(FileManager fileManager, PasswordReader passwordReader) {
        this.fileManager = fileManager;
        this.passwordReader = passwordReader;
    }

    TruststoreFile create(String truststoreFilePath, String truststorePasswordPath) throws TruststoreFileFactoryException, PasswordReaderException {
        File truststoreFile = new File(truststoreFilePath);
        if (!fileManager.checkIfFileExists(truststoreFile)) {
            throw new TruststoreFileFactoryException(String.format(FILE_DOES_NOT_EXIST_MSG_TEMPLATE, truststoreFile));
        }
        String extension = fileManager.getExtension(truststoreFile);
        switch (extension) {
            case JKS_EXTENSION:
                return createJksTruststore(truststoreFile, truststorePasswordPath);
            case P12_EXTENSION:
                return createP12Truststore(truststoreFile, truststorePasswordPath);
            case PEM_EXTENSION:
                return createPemTruststore(truststoreFile);
            default:
                throw new TruststoreFileFactoryException(String.format(UNKNOWN_TRUSTSTORE_TYPE_MSG_TEMPLATE, extension));
        }
    }

    private JksTruststore createJksTruststore(File truststoreFile, String truststorePasswordPath) throws PasswordReaderException {
        String password = passwordReader.readPassword(new File(truststorePasswordPath));
        return new JksTruststore(truststoreFile, password);
    }

    private P12Truststore createP12Truststore(File truststoreFile, String truststorePasswordPath) throws PasswordReaderException {
        String password = passwordReader.readPassword(new File(truststorePasswordPath));
        return new P12Truststore(truststoreFile, password);
    }

    private PemTruststore createPemTruststore(File truststoreFile) {
        return new PemTruststore(truststoreFile);
    }
}
