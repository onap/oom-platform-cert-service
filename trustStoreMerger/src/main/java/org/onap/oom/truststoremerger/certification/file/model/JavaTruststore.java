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

package org.onap.oom.truststoremerger.certification.file.model;

import java.io.File;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.certification.file.provider.JavaCertificateStoreController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTruststore extends Truststore {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTruststore.class);
    private final JavaCertificateStoreController storeController;
    private final String password;

    public JavaTruststore(File truststoreFile, String password, JavaCertificateStoreController storeController) {
        super(truststoreFile);
        this.password = password;
        this.storeController = storeController;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {} ", this.getFile().getPath());
        return storeController.getTruststoreCertificates();
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        storeController.addCertificates(certificates);
    }

    @Override
    public void saveFile() throws WriteTruststoreFileException {
        storeController.saveFile(this.getFile(), this.getPassword());
    }
}
