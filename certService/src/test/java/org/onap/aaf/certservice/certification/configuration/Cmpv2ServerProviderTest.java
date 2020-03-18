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

package org.onap.aaf.certservice.certification.configuration;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.aaf.certservice.certification.configuration.model.Authentication;
import org.onap.aaf.certservice.certification.configuration.model.CaMode;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.exception.Cmpv2ServerNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cmpv2ServerProviderTest {

    private static final String TEST_CA = "testCA";

    private Cmpv2ServerProvider cmpv2ServerProvider;

    @Mock
    private CmpServersConfig cmpServersConfig;

    @BeforeEach
    void setUp() {
        cmpv2ServerProvider =
                new Cmpv2ServerProvider(cmpServersConfig);
    }

    @Test
    void shouldReturnOptionalWithServerWhenServerWithGivenCaNameIsPresentInConfig() {
        // Given
        Cmpv2Server testServer = createTestServer();
        when(cmpServersConfig.getCmpServers()).thenReturn(Collections.singletonList(testServer));

        // When
        Cmpv2Server receivedServer = cmpv2ServerProvider
                .getCmpv2Server(TEST_CA);

        // Then
        assertThat(receivedServer).isEqualToComparingFieldByField(testServer);
    }

    @Test
    void shouldReturnEmptyOptionalWhenServerWithGivenCaNameIsNotPresentInConfig() {
        // Given
        String expectedMessage = "No server found for given CA name";
        when(cmpServersConfig.getCmpServers()).thenReturn(Collections.emptyList());

        // When
        Exception exception = assertThrows(
                Cmpv2ServerNotFoundException.class, () ->
                        cmpv2ServerProvider.getCmpv2Server(TEST_CA)
        );

        // Then
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private Cmpv2Server createTestServer() {
        Cmpv2Server testServer = new Cmpv2Server();
        testServer.setCaName(TEST_CA);
        testServer.setIssuerDN(new X500Name("CN=testIssuer"));
        testServer.setUrl("http://test.ca.server");
        Authentication testAuthentication = new Authentication();
        testAuthentication.setIak("testIak");
        testAuthentication.setRv("testRv");
        testServer.setAuthentication(testAuthentication);
        testServer.setCaMode(CaMode.RA);

        return testServer;
    }
}
