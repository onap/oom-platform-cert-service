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
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JksTruststore extends TruststoreFileWithPassword {

    private static final Logger LOGGER = LoggerFactory.getLogger(JksTruststore.class);
    private static final String KEYSTORE_INSTANCE_JKS = "JKS";
    private final ExitableKeystoreInstance keystoreInstance;

    public JksTruststore(File truststoreFile, String password)
        throws KeystoreInstanceException, LoadTruststoreException {
        super(truststoreFile, password);
        keystoreInstance = getExitableKeyStoreInstance();
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {} ", this.getTruststoreFile().getPath());
        return keystoreInstance.getTruststoreCertificates();
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        keystoreInstance.addCertificates(certificates);
    }

    @Override
    public void saveFile() throws WriteTruststoreFileException {
        keystoreInstance.saveFile(this.getTruststoreFile(), this.getPassword());
    }

    private ExitableKeystoreInstance getExitableKeyStoreInstance()
        throws KeystoreInstanceException, LoadTruststoreException {
        try {
            return new ExitableKeystoreInstance(KeyStore.getInstance(KEYSTORE_INSTANCE_JKS), this.getTruststoreFile(),
                this.getPassword());
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot initialize Keystore instance");
            throw new KeystoreInstanceException(e);
        }
    }

}
