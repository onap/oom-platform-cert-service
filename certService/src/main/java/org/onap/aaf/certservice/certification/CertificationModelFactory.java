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

import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.onap.aaf.certservice.certification.CertificationData.CA_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.ENTITY_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.INTERMEDIATE_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.EXTRA_CA_CERT;

@Service
public class CertificationModelFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationModelFactory.class);


    public CertificationModel createCertificationModel(CsrModel csr, String caName) {
        LOGGER.info("Generating certificates for CA named: {}, and certificate signing request:\n{}",
                caName, csr);
        return new CertificationModel(
                Arrays.asList(ENTITY_CERT, INTERMEDIATE_CERT),
                Arrays.asList(CA_CERT, EXTRA_CA_CERT)
        );
    }

}
