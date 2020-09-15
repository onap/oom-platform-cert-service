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

package org.onap.oom.certservice.postprocessor.merger.model;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import org.onap.oom.certservice.postprocessor.merger.exception.KeystoreInstanceException;
import org.onap.oom.certservice.postprocessor.merger.exception.LoadTruststoreException;
import org.onap.oom.certservice.postprocessor.merger.exception.PasswordReaderException;
import org.onap.oom.certservice.postprocessor.common.PasswordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTruststoreFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTruststoreFactory.class);

    private JavaTruststoreFactory() {
    }

    public static Truststore create(File certFile, String truststorePasswordPath, String keystoreType)
        throws LoadTruststoreException, KeystoreInstanceException, PasswordReaderException {
        String password = PasswordReader.readPassword(new File(truststorePasswordPath));
        try {
            return JavaTruststore
                .createWithLoadingFile(KeyStore.getInstance(keystoreType), certFile, password);
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Java Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }
}

