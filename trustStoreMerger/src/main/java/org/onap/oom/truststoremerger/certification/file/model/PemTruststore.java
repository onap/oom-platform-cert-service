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
import org.onap.oom.truststoremerger.certification.file.provider.PemCertificateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemTruststore extends Truststore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PemTruststore.class);
    private final PemCertificateController pemCertificateController;

    public PemTruststore(File truststoreFile, PemCertificateController pemCertificateController) {
        super(truststoreFile);
        this.pemCertificateController = pemCertificateController;
    }

    @Override
    public List<CertificateWithAlias> getCertificates() throws ExitableException {
        LOGGER.debug("Reading certificates from file: {}", this.getFile().getPath());
        return pemCertificateController.getNotEmptyCertificateList();
    }

    @Override
    public void addCertificate(List<CertificateWithAlias> certificates) throws ExitableException {
        LOGGER.debug("Adding certificates to file: {}", this.getFile().getPath());
        pemCertificateController.addCertificate(certificates);
    }

    @Override
    public void saveFile() throws ExitableException {
        LOGGER.debug("Saving file: {} ", this.getFile().getPath());
        pemCertificateController.saveFile();
    }




}
