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

package org.onap.oom.truststoremerger.merger.model;

import static org.onap.oom.truststoremerger.api.CertificateConstants.JKS_TYPE;
import static org.onap.oom.truststoremerger.api.CertificateConstants.PKCS12_TYPE;

import java.io.File;
import org.onap.oom.truststoremerger.common.PasswordReader;
import org.onap.oom.truststoremerger.merger.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.merger.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.merger.exception.PasswordReaderException;
import org.onap.oom.truststoremerger.merger.exception.TruststoreFileFactoryException;
import org.onap.oom.truststoremerger.common.ExtensionResolver;

public class TruststoreFactory {

    private static final String JKS_EXTENSION = ".jks";
    private static final String P12_EXTENSION = ".p12";
    private static final String PEM_EXTENSION = ".pem";
    private static final String FILE_DOES_NOT_EXIST_MSG_TEMPLATE = "File: %s does not exist";
    private static final String UNKNOWN_TRUSTSTORE_TYPE_MSG_TEMPLATE = "Unknown truststore extension type: %s";


    private TruststoreFactory() {
    }

    public static Truststore create(String truststoreFilePath, String truststorePasswordPath)
        throws TruststoreFileFactoryException, PasswordReaderException, KeystoreInstanceException, LoadTruststoreException {
        File truststoreFile = new File(truststoreFilePath);
        if (!ExtensionResolver.checkIfFileExists(truststoreFile)) {
            throw new TruststoreFileFactoryException(String.format(FILE_DOES_NOT_EXIST_MSG_TEMPLATE, truststoreFile));
        }
        return createTypedTruststore(truststoreFile, truststorePasswordPath);
    }

    private static Truststore createTypedTruststore(File truststoreFile, String truststorePasswordPath)
        throws KeystoreInstanceException, PasswordReaderException, LoadTruststoreException, TruststoreFileFactoryException {
        String extension = ExtensionResolver.get(truststoreFile);
        switch (extension) {
            case JKS_EXTENSION:
                return createJavaTruststore(truststoreFile, truststorePasswordPath, JKS_TYPE);
            case P12_EXTENSION:
                return createJavaTruststore(truststoreFile, truststorePasswordPath, PKCS12_TYPE);
            case PEM_EXTENSION:
                return new PemTruststore(truststoreFile);
            default:
                throw new TruststoreFileFactoryException(
                    String.format(UNKNOWN_TRUSTSTORE_TYPE_MSG_TEMPLATE, extension));
        }
    }

    private static Truststore createJavaTruststore(File truststoreFile, String truststorePasswordPath, String keystoreType)
        throws PasswordReaderException, LoadTruststoreException, KeystoreInstanceException {

        return JavaTruststoreFactory
            .create(truststoreFile, truststorePasswordPath, keystoreType);
    }

}
