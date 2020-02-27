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
import org.onap.aaf.certservice.client.configuration.CsrConfigurationEnvs;
import org.onap.aaf.certservice.client.configuration.EnvsForCsr;
import org.onap.aaf.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.aaf.certservice.client.configuration.factory.CsrConfigurationFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsrConfigurationFactoryTest {

    final String COMMON_NAME_VALID = "onap.org";
    final String SANS_VALID = "test-name";
    final String COUNTRY_VALID = "US";
    final String LOCATION_VALID = "San-Francisco";
    final String ORGANIZATION_VALID =  "Linux-Foundation";
    final String ORGANIZATION_UNIT_VALID = "ONAP";
    final String STATE_VALID = "California";
    final String COMMON_NAME_INVALID = "onap.org*&";

    private EnvsForCsr envsForCsr = mock(EnvsForCsr.class);


    @Test
    void create_shouldReturnSuccessWhenAllVariablesAreSetAndValid() throws CsrConfigurationException {
        // given
        when(envsForCsr.getCommonName()).thenReturn(COMMON_NAME_VALID);
        when(envsForCsr.getSubjectAlternativesName()).thenReturn(SANS_VALID);
        when(envsForCsr.getCountry()).thenReturn(COUNTRY_VALID);
        when(envsForCsr.getLocation()).thenReturn(LOCATION_VALID);
        when(envsForCsr.getOrganization()).thenReturn(ORGANIZATION_VALID);
        when(envsForCsr.getOrganizationUnit()).thenReturn(ORGANIZATION_UNIT_VALID);
        when(envsForCsr.getState()).thenReturn(STATE_VALID);

        // when
        CsrConfiguration configuration = new CsrConfigurationFactory(envsForCsr).create();

        // then
        assertThat(configuration.getCommonName()).isEqualTo(COMMON_NAME_VALID);
        assertThat(configuration.getSubjectAlternativeNames()).isEqualTo(SANS_VALID);
        assertThat(configuration.getCountry()).isEqualTo(COUNTRY_VALID);
        assertThat(configuration.getLocation()).isEqualTo(LOCATION_VALID);
        assertThat(configuration.getOrganization()).isEqualTo(ORGANIZATION_VALID);
        assertThat(configuration.getOrganizationUnit()).isEqualTo(ORGANIZATION_UNIT_VALID);
        assertThat(configuration.getState()).isEqualTo(STATE_VALID);
    }

    @Test
    void create_shouldReturnSuccessWhenNotRequiredVariablesAreNotSet() throws CsrConfigurationException {
        // given
        when(envsForCsr.getCommonName()).thenReturn(COMMON_NAME_VALID);
        when(envsForCsr.getState()).thenReturn(STATE_VALID);
        when(envsForCsr.getCountry()).thenReturn(COUNTRY_VALID);
        when(envsForCsr.getOrganization()).thenReturn(ORGANIZATION_VALID);

        // when
        CsrConfiguration configuration = new CsrConfigurationFactory(envsForCsr).create();

        // then
        assertThat(configuration.getCommonName()).isEqualTo(COMMON_NAME_VALID);
        assertThat(configuration.getCountry()).isEqualTo(COUNTRY_VALID);
        assertThat(configuration.getOrganization()).isEqualTo(ORGANIZATION_VALID);
        assertThat(configuration.getState()).isEqualTo(STATE_VALID);
    }


    @Test
    void create_shouldReturnCsrConfigurationExceptionWhenCommonNameContainsSpecialCharacters() {
        // given
        when(envsForCsr.getCommonName()).thenReturn(COMMON_NAME_INVALID);
        when(envsForCsr.getSubjectAlternativesName()).thenReturn(SANS_VALID);
        when(envsForCsr.getCountry()).thenReturn(COUNTRY_VALID);
        when(envsForCsr.getLocation()).thenReturn(LOCATION_VALID);
        when(envsForCsr.getOrganization()).thenReturn(ORGANIZATION_VALID);
        when(envsForCsr.getOrganizationUnit()).thenReturn(ORGANIZATION_UNIT_VALID);
        when(envsForCsr.getState()).thenReturn(SANS_VALID);

        // when
        CsrConfigurationFactory configurationFactory = new CsrConfigurationFactory(envsForCsr);

        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(configurationFactory::create)
                .withMessageContaining(CsrConfigurationEnvs.COMMON_NAME + " is invalid.");
    }
}
