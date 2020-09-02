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
import java.security.KeyStore;
import java.security.KeyStoreException;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaCertificateManipulatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCertificateManipulatorFactory.class);

    private JavaCertificateManipulatorFactory() {
    }

    public static CertificateManipulator create(File certFile, String certPassword, String keystoreType)
        throws LoadTruststoreException, KeystoreInstanceException {
        try {
            return JavaCertificateManipulator
                .createWithLoadingFile(KeyStore.getInstance(keystoreType), certFile, certPassword);
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Java Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }
}

