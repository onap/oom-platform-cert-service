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

import static org.onap.oom.truststoremerger.api.CertificateConstants.JKS_INSTANCE;
import static org.onap.oom.truststoremerger.api.CertificateConstants.PKCS12_INSTANCE;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateStoreControllerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateStoreControllerFactory.class);

    public JavaCertificateStoreController createLoadedJksCertificateStoreController(File certFile, String certPassword)
        throws LoadTruststoreException, KeystoreInstanceException {
        return createLoadedCertificateStoreController(certFile, certPassword, JKS_INSTANCE);
    }

    public JavaCertificateStoreController createLoadedPkcs12CertificateStoreController(File certFile, String certPassword)
        throws KeystoreInstanceException, LoadTruststoreException {
        return createLoadedCertificateStoreController(certFile, certPassword, PKCS12_INSTANCE);
    }

    private JavaCertificateStoreController createLoadedCertificateStoreController(File certFile, String certPassword,
        String instanceType)
        throws LoadTruststoreException, KeystoreInstanceException {
        try {
            JavaCertificateStoreController javaCertificateStoreController = new JavaCertificateStoreController(
                KeyStore.getInstance(instanceType), certFile, certPassword);
            javaCertificateStoreController.loadFile();
            return javaCertificateStoreController;
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Java Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }
}

