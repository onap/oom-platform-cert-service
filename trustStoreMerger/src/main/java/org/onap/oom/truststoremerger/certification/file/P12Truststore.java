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
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.provider.CertificateStoreController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P12Truststore extends TruststoreFileWithPassword {

    private static final Logger LOGGER = LoggerFactory.getLogger(P12Truststore.class);

    private final CertificateStoreController storeController;

    public P12Truststore(File truststoreFile, String password, CertificateStoreController storeController) {
        super(truststoreFile, password);
        this.storeController = storeController;
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {} ", this.getTruststoreFile().getPath());
        return storeController.getTruststoreCertificates();
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        storeController.addCertificates(certificates);
    }

    @Override
    public void saveFile() throws ExitableException {
        storeController.saveFile(this.getTruststoreFile(), this.getPassword());
    }

}
