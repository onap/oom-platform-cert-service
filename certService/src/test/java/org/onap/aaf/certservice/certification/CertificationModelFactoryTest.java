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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.onap.aaf.certservice.certification.CertificationData.CA_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.ENTITY_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.INTERMEDIATE_CERT;
import static org.onap.aaf.certservice.certification.CertificationData.EXTRA_CA_CERT;


class CertificationModelFactoryTest {


    private CertificationModelFactory certificationModelFactory;

    @BeforeEach
    void setUp() {
        certificationModelFactory = new CertificationModelFactory();
    }

    @Test
    void shouldCreateProperCertificationModelWhenGivenProperCsrModelAndCaName() {
        // given
        final String testCaName = "testCA";
        CsrModel mockedCsrModel = mock(CsrModel.class);

        // when
        CertificationModel certificationModel = certificationModelFactory.createCertificationModel(mockedCsrModel ,testCaName);

        //then
        assertEquals(2, certificationModel.getCertificateChain().size());
        assertThat(certificationModel.getCertificateChain()).contains(INTERMEDIATE_CERT, ENTITY_CERT);
        assertEquals(2, certificationModel.getTrustedCertificates().size());
        assertThat(certificationModel.getTrustedCertificates()).contains(CA_CERT, EXTRA_CA_CERT);
    }

}
