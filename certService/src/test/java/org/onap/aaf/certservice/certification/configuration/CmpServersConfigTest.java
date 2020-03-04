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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

import java.util.List;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.aaf.certservice.certification.configuration.model.Authentication;
import org.onap.aaf.certservice.certification.configuration.model.CaMode;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;

@ExtendWith(MockitoExtension.class)
class CmpServersConfigTest {

    private static final String ERROR_MESSAGE = "Exception occurred during CMP Servers configuration loading";
    private static final String APP_CONFIG_PATH = "/fake/path/to/config";
    private static final List<Cmpv2Server> SAMPLE_CMP_SERVERS = generateTestConfiguration();

    @Mock
    private CmpServersConfigLoader cmpServersConfigLoader;

    private CmpServersConfig cmpServersConfig;

    @BeforeEach
    void setUp() {
        cmpServersConfig = new CmpServersConfig(APP_CONFIG_PATH, cmpServersConfigLoader);
    }

    @Test
    void shouldCallLoaderWithPathFromPropertiesWhenCreated() throws CmpServersConfigLoadingException {
        // When
        this.cmpServersConfig.init();      // Manual PostConstruct call

        // Then
        Mockito.verify(cmpServersConfigLoader).load(startsWith(APP_CONFIG_PATH));
    }

    @Test
    void shouldReturnLoadedServersWhenGetCalled() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenReturn(SAMPLE_CMP_SERVERS);
        this.cmpServersConfig.init();      // Manual PostConstruct call

        // When
        List<Cmpv2Server> receivedCmpServers = this.cmpServersConfig.getCmpServers();

        // Then
        assertThat(receivedCmpServers).containsAll(SAMPLE_CMP_SERVERS);
    }

    @Test
    void shouldReturnLoadedServersAfterReloadWhenGetCalled() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenReturn(SAMPLE_CMP_SERVERS);
        List<Cmpv2Server> receivedCmpServers = this.cmpServersConfig.getCmpServers();
        assertThat(receivedCmpServers).isNull();

        // When
        this.cmpServersConfig.reloadConfiguration();
        receivedCmpServers = this.cmpServersConfig.getCmpServers();

        // Then
        assertThat(receivedCmpServers).containsAll(SAMPLE_CMP_SERVERS);
    }

    @Test
    void shouldRethrowExceptionWhenReloaded() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenThrow(new CmpServersConfigLoadingException(
            ERROR_MESSAGE));

        // Then
        assertThrows(
            CmpServersConfigLoadingException.class,
            () -> cmpServersConfig.reloadConfiguration());
    }

    @Test
    void shouldPassMessageToRethrownErrorWhenReloadingFails() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenThrow(new CmpServersConfigLoadingException(ERROR_MESSAGE));

        // When
        Exception exception = assertThrows(
            CmpServersConfigLoadingException.class,
            () -> cmpServersConfig.reloadConfiguration());

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldNotReturnIakAndRvWhenToStringMethodIsUsed() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenReturn(SAMPLE_CMP_SERVERS);
        this.cmpServersConfig.init();      // Manual PostConstruct call

        // When
        List<Cmpv2Server> receivedCmpServers = this.cmpServersConfig.getCmpServers();

        // Then
        receivedCmpServers.forEach((server) -> assertThat(server.toString())
            .doesNotContain(
                server.getAuthentication().getIak(),
                server.getAuthentication().getRv()
            ));
    }

    @Test
    void shouldRethrowErrorWhenLoadingFails() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenThrow(new CmpServersConfigLoadingException(ERROR_MESSAGE));

        // Then
        assertThrows(
            CmpServersConfigLoadingException.class,
            () -> cmpServersConfig.loadConfiguration());
    }

    @Test
    void shouldPassMessageToRethrownErrorWhenLoadingFails() throws CmpServersConfigLoadingException {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenThrow(new CmpServersConfigLoadingException(ERROR_MESSAGE));

        // When
        Exception exception = assertThrows(
            CmpServersConfigLoadingException.class,
            () -> cmpServersConfig.loadConfiguration());

        // Then
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    private static List<Cmpv2Server> generateTestConfiguration() {
        Cmpv2Server testServer1 = new Cmpv2Server();
        testServer1.setCaName("TEST_CA1");
        testServer1.setIssuerDN(new X500Name("CN=testIssuer"));
        testServer1.setUrl("http://test.ca.server");
        Authentication testAuthentication1 = new Authentication();
        testAuthentication1.setIak("testIak");
        testAuthentication1.setRv("testRv");
        testServer1.setAuthentication(testAuthentication1);
        testServer1.setCaMode(CaMode.RA);

        Cmpv2Server testServer2 = new Cmpv2Server();
        testServer2.setCaName("TEST_CA2");
        testServer2.setIssuerDN(new X500Name("CN=testIssuer2"));
        testServer2.setUrl("http://test.ca.server");
        Authentication testAuthentication2 = new Authentication();
        testAuthentication2.setIak("test2Iak");
        testAuthentication2.setRv("test2Rv");
        testServer2.setAuthentication(testAuthentication2);
        testServer2.setCaMode(CaMode.CLIENT);

        return List.of(testServer1, testServer2);
    }

}
