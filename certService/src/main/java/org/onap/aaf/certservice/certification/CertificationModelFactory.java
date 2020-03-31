/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.aaf.certservice.certification;

import org.onap.aaf.certservice.certification.configuration.Cmpv2ServerProvider;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.exception.DecryptionException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificationModelFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationModelFactory.class);

    private final CsrModelFactory csrModelFactory;
    private final Cmpv2ServerProvider cmpv2ServerProvider;
    private final CertificationProvider certificationProvider;

    @Autowired
    CertificationModelFactory(
            CsrModelFactory csrModelFactory,
            Cmpv2ServerProvider cmpv2ServerProvider,
            CertificationProvider certificationProvider
    ) {
        this.cmpv2ServerProvider = cmpv2ServerProvider;
        this.csrModelFactory = csrModelFactory;
        this.certificationProvider = certificationProvider;
    }

    public CertificationModel createCertificationModel(String encodedCsr, String encodedPrivateKey, String caName)
            throws DecryptionException, CmpClientException {
        CsrModel csrModel = csrModelFactory.createCsrModel(
                new CsrModelFactory.StringBase64(encodedCsr),
                new CsrModelFactory.StringBase64(encodedPrivateKey)
        );
        LOGGER.debug("Received CSR meta data: \n{}", csrModel);

        Cmpv2Server cmpv2Server = cmpv2ServerProvider.getCmpv2Server(caName);
        LOGGER.debug("Found server for given CA name: \n{}", cmpv2Server);

        LOGGER.info("Sending sign request for certification model for CA named: {}, and certificate signing request:\n{}",
                caName, csrModel);
        return certificationProvider.signCsr(csrModel, cmpv2Server);
    }

}
