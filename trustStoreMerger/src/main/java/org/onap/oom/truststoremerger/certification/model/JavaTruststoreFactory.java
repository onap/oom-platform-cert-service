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

package org.onap.oom.truststoremerger.certification.model;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import org.onap.oom.truststoremerger.certification.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.exception.LoadTruststoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTruststoreFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTruststoreFactory.class);

    private JavaTruststoreFactory() {
    }

    public static TruststoreEntity create(File certFile, String certPassword, String instanceType)
        throws LoadTruststoreException, KeystoreInstanceException {
        try {
            JavaTruststore javaTruststore = new JavaTruststore(
                KeyStore.getInstance(instanceType), certFile, certPassword);
            javaTruststore.loadFile();
            return javaTruststore;
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Java Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }
}

