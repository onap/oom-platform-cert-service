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

class EnvsForCsrTest {
    private static final String TEST_ENV = "testEnv";
    private EnvsForCsr envsForCsr;

    @BeforeEach
    public void setUp() {
        envsForCsr = Mockito.spy(EnvsForCsr.class);
    }

    @Test
    void shouldReturnSystemEnvCommonNameVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.COMMON_NAME)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getCommonName();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    void shouldReportThatSystemEnvCommonNameVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getCommonName();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvOrganizationVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.ORGANIZATION)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getOrganization();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    void shouldReportThatSystemEnvOrganizationVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getOrganization();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvOuVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.ORGANIZATION_UNIT)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getOrganizationUnit();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvOuVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getOrganizationUnit();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvLocationVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.LOCATION)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getLocation();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    void shouldReportThatSystemEnvLocationVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getLocation();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvStateVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.STATE)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getState();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    void shouldReportThatSystemEnvStateVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getState();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvCountryVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.COUNTRY)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getCountry();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    void shouldReportThatSystemEnvCountryVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getCountry();

        // then
        assertThat(testEnv).isNotPresent();
    }

    @Test
    void shouldReturnSystemEnvSansVariableWhenItWasDefined() {
        // given
        when(envsForCsr.readEnv(CsrConfigurationEnvs.SANS)).thenReturn(Optional.of(TEST_ENV));

        // when
        final Optional<String> testEnv = envsForCsr.getSubjectAlternativesName();

        // then
        assertThat(testEnv)
                .isPresent()
                .contains(TEST_ENV);
    }

    @Test
    public void shouldReportThatSystemEnvSansVariableIsNotPresentWhenItWasNotDefined() {
        // when
        final Optional<String> testEnv = envsForCsr.getSubjectAlternativesName();

        // then
        assertThat(testEnv).isNotPresent();
    }
}
