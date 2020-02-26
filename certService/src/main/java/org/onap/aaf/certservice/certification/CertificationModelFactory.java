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
import org.onap.aaf.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.onap.aaf.certservice.certification.CertificationData.CA_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.ENTITY_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.INTERMEDIATE_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.EXTRA_CA_CERT;

@Service
public class CertificationModelFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationModelFactory.class);

    private final Cmpv2ServerProvider cmpv2ServerProvider;

    @Autowired
    CertificationModelFactory(Cmpv2ServerProvider cmpv2ServerProvider) {
        this.cmpv2ServerProvider = cmpv2ServerProvider;
    }

    public CertificationModel createCertificationModel(CsrModel csr, String caName) {
        LOGGER.info("Generating certification model for CA named: {}, and certificate signing request:\n{}",
                caName, csr);

        return cmpv2ServerProvider
                .getCmpv2Server(caName)
                .map(this::generateCertificationModel)
                .orElseThrow(() -> new Cmpv2ServerNotFoundException("No server found for given CA name"));
    }

    private CertificationModel generateCertificationModel(Cmpv2Server cmpv2Server) {
        LOGGER.debug("Found server for given CA name: \n{}", cmpv2Server);
        return new CertificationModel(
                Arrays.asList(ENTITY_CERT, INTERMEDIATE_CERT),
                Arrays.asList(CA_CERT, EXTRA_CA_CERT)
        );
    }
}
