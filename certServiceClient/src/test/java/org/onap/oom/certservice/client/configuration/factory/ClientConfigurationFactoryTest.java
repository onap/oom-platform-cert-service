/*
 * ============LICENSE_START=======================================================
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

package org.onap.oom.certservice.client.configuration.factory;

import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.configuration.ClientConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.EnvsForClient;
import org.onap.oom.certservice.client.configuration.exception.ClientConfigurationException;
import org.onap.oom.certservice.client.configuration.model.ClientConfiguration;

import java.util.Optional;
import org.onap.oom.certservice.client.configuration.validation.ValidatorsFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientConfigurationFactoryTest {

    private static final String CA_NAME_VALID = "catest2";
    private static final String TIME_OUT_VALID = "30000";
    private static final String OUTPUT_PATH_VALID = "/opt/app/oom";
    private static final String URL_TO_CERT_SERVICE_VALID = "https://cert-service:8443/v1/certificate/";
    private static final String URL_TO_CERT_SERVICE_DEFAULT = "https://oom-cert-service:8443/v1/certificate/";
    private static final String CA_NAME_INVALID = "catest2#$";
    private static final String OUTPUT_PATH_INVALID = "/opt//app/oom";
    private static final String OUTPUT_TYPE_VALID = "JKS";
    private static final String OUTPUT_TYPE_INVALID = "JKSS";
    private static final String OUTPUT_TYPE_DEFAULT = "P12";

    private EnvsForClient envsForClient = mock(EnvsForClient.class);
    private ValidatorsFactory validatorsFactory = new ValidatorsFactory();


    @Test
    void create_shouldReturnSuccessWhenAllVariablesAreSetAndValid() throws ClientConfigurationException {
        // given
        when(envsForClient.getCaName()).thenReturn(Optional.of(CA_NAME_VALID));
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_VALID));
        when(envsForClient.getRequestTimeOut()).thenReturn(Optional.of(TIME_OUT_VALID));
        when(envsForClient.getUrlToCertService()).thenReturn(Optional.of(URL_TO_CERT_SERVICE_VALID));
        when(envsForClient.getOutputType()).thenReturn(Optional.of(OUTPUT_TYPE_VALID));

        // when
        ClientConfiguration configuration = new ClientConfigurationFactory(envsForClient, validatorsFactory).create();
        System.out.println(configuration.toString());

        // then
        assertThat(configuration.getCaName()).isEqualTo(CA_NAME_VALID);
        assertThat(configuration.getRequestTimeoutInMs()).isEqualTo(Integer.valueOf(TIME_OUT_VALID));
        assertThat(configuration.getCertsOutputPath()).isEqualTo(OUTPUT_PATH_VALID);
        assertThat(configuration.getUrlToCertService()).isEqualTo(URL_TO_CERT_SERVICE_VALID);
        assertThat(configuration.getOutputType()).isEqualTo(OUTPUT_TYPE_VALID);
    }

    @Test
    void create_shouldReturnSuccessWhenDefaultVariablesAreNotSet() throws ClientConfigurationException {
        // given
        when(envsForClient.getCaName()).thenReturn(Optional.of(CA_NAME_VALID));
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_VALID));

        // when
        ClientConfiguration configuration = new ClientConfigurationFactory(envsForClient, validatorsFactory).create();

        // then
        assertThat(configuration.getCaName()).isEqualTo(CA_NAME_VALID);
        assertThat(configuration.getRequestTimeoutInMs()).isEqualTo(Integer.valueOf(TIME_OUT_VALID));
        assertThat(configuration.getCertsOutputPath()).isEqualTo(OUTPUT_PATH_VALID);
        assertThat(configuration.getUrlToCertService()).isEqualTo(URL_TO_CERT_SERVICE_DEFAULT);
        assertThat(configuration.getOutputType()).isEqualTo(OUTPUT_TYPE_DEFAULT);
    }

    @Test
    void create_shouldReturnClientExceptionWhenRequiredVariableIsNotSet() {
        // given
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_VALID));

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient, validatorsFactory);

        // then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.CA_NAME + " is invalid.");
    }

    @Test
    void create_shouldReturnClientExceptionWhenCaNameContainsSpecialCharacters() {
        // given
        when(envsForClient.getCaName()).thenReturn(Optional.of(CA_NAME_INVALID));
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_VALID));
        when(envsForClient.getRequestTimeOut()).thenReturn(Optional.of(TIME_OUT_VALID));
        when(envsForClient.getUrlToCertService()).thenReturn(Optional.of(URL_TO_CERT_SERVICE_VALID));

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient, validatorsFactory);

        // when/then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.CA_NAME + " is invalid.");
    }

    @Test
    void create_shouldReturnClientExceptionWhenOutputPathContainsSpecialCharacters() {
        // given
        when(envsForClient.getCaName()).thenReturn(Optional.of(CA_NAME_VALID));
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_INVALID));
        when(envsForClient.getRequestTimeOut()).thenReturn(Optional.of(TIME_OUT_VALID));
        when(envsForClient.getUrlToCertService()).thenReturn(Optional.of(URL_TO_CERT_SERVICE_VALID));

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient, validatorsFactory);

        //then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.OUTPUT_PATH + " is invalid.");
    }

    @Test
    void create_shouldReturnClientExceptionWhenOutputTypeIsInvalid() {
        // given
        when(envsForClient.getCaName()).thenReturn(Optional.of(CA_NAME_VALID));
        when(envsForClient.getOutputPath()).thenReturn(Optional.of(OUTPUT_PATH_VALID));
        when(envsForClient.getRequestTimeOut()).thenReturn(Optional.of(TIME_OUT_VALID));
        when(envsForClient.getUrlToCertService()).thenReturn(Optional.of(URL_TO_CERT_SERVICE_VALID));
        when(envsForClient.getOutputType()).thenReturn(Optional.of(OUTPUT_TYPE_INVALID));

        // when
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(envsForClient, validatorsFactory);

        //then
        assertThatExceptionOfType(ClientConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(ClientConfigurationEnvs.OUTPUT_TYPE + " is invalid.");
    }
}
