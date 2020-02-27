/*
 * ============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.configuration.model;

import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.configuration.ClientConfigurationEnvs;
import org.onap.aaf.certservice.client.configuration.EnvsForClient;
import org.onap.aaf.certservice.client.configuration.exception.ClientConfigurationException;
import org.onap.aaf.certservice.client.configuration.factory.ClientConfigurationFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientConfigurationFactoryTest {

    final String CA_NAME_VALID =  "caaaftest2";
    final String TIME_OUT_VALID = "30000";
    final String OUTPUT_PATH_VALID = "/opt/app/osaaf";
    final String URL_TO_CERT_SERVICE_VALID = "http://cert-service:8080/v1/certificate/";
    final String CA_NAME_INVALID =  "caaaftest2#$";
    final String OUTPUT_PATH_INVALID = "/opt//app/osaaf";

    private EnvsForClient envsForClient = mock(EnvsForClient.class);

    @Test
    void create_shouldReturnSuccessWhenAllVariablesAreSetAndValid() throws ClientConfigurationException {
        // given
        when(envsForClient.getCaName()).thenReturn(CA_NAME_VALID);
        when(envsForClient.getOutputPath()).thenReturn(OUTPUT_PATH_VALID);
        when(envsForClient.getRequestTimeOut()).thenReturn(TIME_OUT_VALID);
        when(envsForClient.getUrlToCertService()).thenReturn(URL_TO_CERT_SERVICE_VALID);

        // when
        ClientConfiguration configuration = new ClientConfigurationFactory(envsForClient).create();

        // then
        assertThat(configuration.getCaName()).isEqualTo(CA_NAME_VALID);
        assertThat(configuration.getRequestTimeout()).isEqualTo(Integer.valueOf(TIME_OUT_VALID));
        assertThat(configuration.getCertsOutputPath()).isEqualTo(OUTPUT_PATH_VALID);
        assertThat(configuration.getUrlToCertService()).isEqualTo(URL_TO_CERT_SERVICE_VALID);
    }

    @Test
    void create_shouldReturnSuccessWhenDefaultVariablesAreNotSet() throws ClientConfigurationException {
        // given
        when(envsForClient.getCaName()).thenReturn(CA_NAME_VALID);
        when(envsForClient.getOutputPath()).thenReturn(OUTPUT_PATH_VALID);

        // when
        ClientConfiguration configuration = new ClientConfigurationFactory(envsForClient).create();

        // then
        assertThat(configuration.getCaName()).isEqualTo(CA_NAME_VALID);
        assertThat(configuration.getRequestTimeout()).isEqualTo(Integer.valueOf(TIME_OUT_VALID));
        assertThat(configuration.getCertsOutputPath()).isEqualTo(OUTPUT_PATH_VALID);
        assertThat(configuration.getUrlToCertService()).isEqualTo(URL_TO_CERT_SERVICE_VALID);
    }

    @Test
    void create_shouldReturnClientExceptionWhenRequiredVariableIsNotSet() {
        // given
        when(envsForClient.getOutputPath()).thenReturn(OUTPUT_PATH_VALID);

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient);

        // when/then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.CA_NAME + " is invalid.");
    }

    @Test
    void create_shouldReturnClientExceptionWhenCANameContainsSpecialCharacters() {
        // given
        when(envsForClient.getCaName()).thenReturn(CA_NAME_INVALID);
        when(envsForClient.getOutputPath()).thenReturn(OUTPUT_PATH_VALID);
        when(envsForClient.getRequestTimeOut()).thenReturn(TIME_OUT_VALID);
        when(envsForClient.getUrlToCertService()).thenReturn(URL_TO_CERT_SERVICE_VALID);

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient);

        // when/then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.CA_NAME + " is invalid.");
    }

    @Test
    void create_shouldReturnClientExceptionWhenOutputPathContainsSpecialCharacters() {
        // given
        when(envsForClient.getCaName()).thenReturn(CA_NAME_VALID);
        when(envsForClient.getOutputPath()).thenReturn(OUTPUT_PATH_INVALID);
        when(envsForClient.getRequestTimeOut()).thenReturn(TIME_OUT_VALID);
        when(envsForClient.getUrlToCertService()).thenReturn(URL_TO_CERT_SERVICE_VALID);

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient);

        // when/then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.OUTPUT_PATH + " is invalid.");
    }
}
