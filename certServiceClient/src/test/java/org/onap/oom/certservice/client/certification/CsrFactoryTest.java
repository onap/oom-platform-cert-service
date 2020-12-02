/*============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.certification;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.util.List;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.certification.exception.CsrGenerationException;
import org.onap.oom.certservice.client.certification.exception.KeyPairGenerationException;
import org.onap.oom.certservice.client.configuration.model.CsrConfiguration;
import org.onap.oom.certservice.client.configuration.model.San;

class CsrFactoryTest {

    private CsrConfiguration config = mock(CsrConfiguration.class);

    @Test
    void createEncodedCsr_shouldSucceedWhenAllFieldsAreSetCorrectly()
        throws KeyPairGenerationException, CsrGenerationException {

        KeyPair keyPair = createKeyPair();

        mockRequiredConfigFields();
        mockOptionalConfigFields();

        assertThat(new CsrFactory(config).createCsrInPem(keyPair)).isNotEmpty();
    }

    @Test
    void createEncodedCsr_shouldSucceedWhenRequiredFieldsAreSetCorrectly()
        throws KeyPairGenerationException, CsrGenerationException {

        KeyPair keyPair = createKeyPair();

        mockRequiredConfigFields();
        mockOptionalConfigFieldsEmpty();

        assertThat(new CsrFactory(config).createCsrInPem(keyPair)).isNotEmpty();
    }

    private KeyPair createKeyPair() {
        return new KeyPairFactory(EncryptionAlgorithmConstants.RSA_ENCRYPTION_ALGORITHM,
            EncryptionAlgorithmConstants.KEY_SIZE).create();
    }

    private void mockRequiredConfigFields() {
        when(config.getCommonName()).thenReturn("onap.org");
        when(config.getOrganization()).thenReturn("Linux-Foundation");
        when(config.getCountry()).thenReturn("US");
        when(config.getState()).thenReturn("California");
    }

    private void mockOptionalConfigFields() {
        San san1 = new San("onapexample.com", GeneralName.dNSName);
        San san2 = new San("onapexample.com.pl", GeneralName.dNSName);

        when(config.getLocation()).thenReturn("San-Francisco");
        when(config.getSans()).thenReturn(List.of(san1, san2));
        when(config.getOrganizationUnit()).thenReturn("ONAP");
    }

    private void mockOptionalConfigFieldsEmpty() {
        when(config.getLocation()).thenReturn(null);
        when(config.getSans()).thenReturn(null);
        when(config.getOrganizationUnit()).thenReturn(null);
    }
}

