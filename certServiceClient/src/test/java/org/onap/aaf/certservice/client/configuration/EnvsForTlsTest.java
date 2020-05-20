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

package org.onap.aaf.certservice.client.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EnvsForTlsTest {

    private static final String TEST_ENV = "testEnv";
    private EnvsForTls envsForTls;

    @BeforeEach
    public void setUp() {
        envsForTls = Mockito.spy(EnvsForTls.class);
    }

    @Test
    void shouldReturnSystemEnvKeyStorePathVariableWhenItWasDefined() {
        // given
        when(envsForTls.readEnv(TlsConfigurationEnvs.KEYSTORE_PATH)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForTls.getKeystorePath();

        // then
        assertThat(testEnv.isPresent()).isTrue();
        assertThat(testEnv.get()).isEqualTo(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvKeyStorePathVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForTls.getKeystorePath();

        // then
        assertThat(testEnv.isPresent()).isFalse();
    }

    @Test
    void shouldReturnSystemEnvKeyStorePasswordVariableWhenItWasDefined() {
        // given
        when(envsForTls.readEnv(TlsConfigurationEnvs.KEYSTORE_PASSWORD)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForTls.getKeystorePassword();

        // then
        assertThat(testEnv.isPresent()).isTrue();
        assertThat(testEnv.get()).isEqualTo(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvKeyStorePasswordVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForTls.getKeystorePassword();

        // then
        assertThat(testEnv.isPresent()).isFalse();
    }

    @Test
    void shouldReturnSystemEnvTrustStorePathVariableWhenItWasDefined() {
        // given
        when(envsForTls.readEnv(TlsConfigurationEnvs.TRUSTSTORE_PATH)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForTls.getTruststorePath();

        // then
        assertThat(testEnv.isPresent()).isTrue();
        assertThat(testEnv.get()).isEqualTo(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvTrustStorePathVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForTls.getTruststorePath();

        // then
        assertThat(testEnv.isPresent()).isFalse();
    }

    @Test
    void shouldReturnSystemEnvTrustStorePasswordVariableWhenItWasDefined() {
        // given
        when(envsForTls.readEnv(TlsConfigurationEnvs.TRUSTSTORE_PASSWORD)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForTls.getTruststorePassword();

        // then
        assertThat(testEnv.isPresent()).isTrue();
        assertThat(testEnv.get()).isEqualTo(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvTrustStorePasswordVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForTls.getTruststorePassword();

        // then
        assertThat(testEnv.isPresent()).isFalse();
    }
}
