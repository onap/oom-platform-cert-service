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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P12Truststore extends TruststoreFileWithPassword {

    private static final Logger LOGGER = LoggerFactory.getLogger(P12Truststore.class);

    private static final String KEYSTORE_INSTANCE_P12 = "PKCS12";

    public P12Truststore(File truststoreFile, String password) {
        super(truststoreFile, password);
    }

    @Override
    public List<CertificateWrapper> getCertificates() throws ExitableException {
        ExitableKeystoreInstance keyStore = getExitableKeyStoreInstance();
        return keyStore.getTruststoreCertificates(this.getTruststoreFile(), this.getPassword());
    }

    @Override
    public void addCertificate(List<CertificateWrapper> certificates) throws ExitableException {
        ExitableKeystoreInstance keyStore = getExitableKeyStoreInstance();
        keyStore.addCertificates(certificates, this.getTruststoreFile(), this.getPassword());
    }

    private ExitableKeystoreInstance getExitableKeyStoreInstance() throws KeystoreInstanceException {
        try {
            return new ExitableKeystoreInstance(KeyStore.getInstance(KEYSTORE_INSTANCE_P12));
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }

}
