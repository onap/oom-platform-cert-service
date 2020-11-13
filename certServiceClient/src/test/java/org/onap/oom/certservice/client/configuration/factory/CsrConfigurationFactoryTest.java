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

import java.util.List;
import org.assertj.core.api.Condition;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.configuration.CsrConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.EnvsForCsr;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.CsrConfiguration;

import java.util.Optional;
import org.onap.oom.certservice.client.configuration.model.San;
import org.onap.oom.certservice.client.configuration.validation.csr.CommonNameValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.onap.oom.certservice.client.api.ExitStatus.CSR_CONFIGURATION_EXCEPTION;

public class CsrConfigurationFactoryTest {

    private static final String COMMON_NAME_VALID = "onap.org";
    private static final String RAW_SAN1 = "ves-collector";
    private static final String RAW_SAN2 = "ves";
    private static final String RAW_SANS_VALID = String.format("%s,%s", RAW_SAN1, RAW_SAN2);
    private static final String COUNTRY_VALID = "US";
    private static final String LOCATION_VALID = "San-Francisco";
    private static final String ORGANIZATION_VALID = "Linux-Foundation";
    private static final String ORGANIZATION_UNIT_VALID = "ONAP";
    private static final String STATE_VALID = "California";
    private static final String COMMON_NAME_INVALID = "onap.org*&";
    private static final String COUNTRY_INVALID = "PLA";
    private static final String ORGANIZATION_INVALID = "Linux?Foundation";
    private static final String INVALID_SANS = "192.168.1.";

    private EnvsForCsr envsForCsr = mock(EnvsForCsr.class);
    private CommonNameValidator commonNameValidator = new CommonNameValidator();
    private SanMapper sanMapper = new SanMapper();
    private CsrConfigurationFactory testedFactory;
    private Condition<CsrConfigurationException> expectedExitCodeCondition = new Condition<>("Correct exit code") {
        @Override
        public boolean matches(CsrConfigurationException exception) {
            return exception.applicationExitStatus() == CSR_CONFIGURATION_EXCEPTION;
        }
    };

    @BeforeEach
    void setUp() {
        testedFactory = new CsrConfigurationFactory(envsForCsr, commonNameValidator, sanMapper);
    }

    @Test
    void shouldReturnCorrectConfiguration_WhenAllVariablesAreSetAndValid() throws CsrConfigurationException {
        // given
        mockEnvsWithAllValidParameters();
        San san1 = new San(RAW_SAN1, GeneralName.dNSName);
        San san2 = new San(RAW_SAN2, GeneralName.dNSName);
        List<San> sans = List.of(san1, san2);

        // when
        CsrConfiguration configuration = testedFactory.create();

        // then
        assertThat(configuration.getCommonName()).isEqualTo(COMMON_NAME_VALID);
        assertThat(configuration.getSans()).isEqualTo(sans);
        assertThat(configuration.getCountry()).isEqualTo(COUNTRY_VALID);
        assertThat(configuration.getLocation()).isEqualTo(LOCATION_VALID);
        assertThat(configuration.getOrganization()).isEqualTo(ORGANIZATION_VALID);
        assertThat(configuration.getOrganizationUnit()).isEqualTo(ORGANIZATION_UNIT_VALID);
        assertThat(configuration.getState()).isEqualTo(STATE_VALID);
    }

    @Test
    void shouldReturnCorrectConfiguration_WhenNotRequiredVariablesAreNotSet() throws CsrConfigurationException {
        // given
        mockEnvsWithValidRequiredParameters();

        // when
        CsrConfiguration configuration = testedFactory.create();

        // then
        assertThat(configuration.getCommonName()).isEqualTo(COMMON_NAME_VALID);
        assertThat(configuration.getCountry()).isEqualTo(COUNTRY_VALID);
        assertThat(configuration.getOrganization()).isEqualTo(ORGANIZATION_VALID);
        assertThat(configuration.getState()).isEqualTo(STATE_VALID);
    }


    @Test
    void shouldThrowCsrConfigurationException_WhenCommonNameInvalid() {
        // given
        mockEnvsWithInvalidCommonName();

        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(testedFactory::create)
                .withMessageContaining(CsrConfigurationEnvs.COMMON_NAME + " is invalid.")
                .has(expectedExitCodeCondition);
    }

    @Test
    void shouldThrowCsrConfigurationException_WhenOrganizationInvalid() {
        // given
        mockEnvsWithInvalidOrganization();

        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(testedFactory::create)
                .withMessageContaining(CsrConfigurationEnvs.ORGANIZATION + " is invalid.")
                .has(expectedExitCodeCondition);

    }

    @Test
    void shouldThrowCsrConfigurationException_WhenCountryInvalid() {
        // given
        mockEnvsWithInvalidCountry();

        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(testedFactory::create)
                .withMessageContaining(CsrConfigurationEnvs.COUNTRY + " is invalid.")
                .has(expectedExitCodeCondition);

    }

    @Test
    void shouldThrowCsrConfigurationExceptionWhenStateInvalid() {
        // given
        mockEnvsWithInvalidState();
        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(testedFactory::create)
                .withMessageContaining(CsrConfigurationEnvs.STATE + " is invalid.")
                .has(expectedExitCodeCondition);
    }

    @Test
    void shouldThrowCsrConfigurationExceptionWhenSansInvalid() {
        // given
        mockEnvsWithInvalidSans();
        // when/then
        assertThatExceptionOfType(CsrConfigurationException.class)
                .isThrownBy(testedFactory::create)
                .withMessageContaining("SAN :" + INVALID_SANS + " does not match any requirements")
                .has(expectedExitCodeCondition);
    }

    private void mockEnvsWithAllValidParameters() {
        mockEnvsWithValidRequiredParameters();
        mockEnvsWithValidOptionalParameters();
    }

    private void mockEnvsWithValidOptionalParameters() {
        when(envsForCsr.getOrganizationUnit()).thenReturn(Optional.of(ORGANIZATION_UNIT_VALID));
        when(envsForCsr.getLocation()).thenReturn(Optional.of(LOCATION_VALID));
        when(envsForCsr.getSubjectAlternativesName()).thenReturn(Optional.of(RAW_SANS_VALID));
    }

    private void mockEnvsWithValidRequiredParameters() {
        when(envsForCsr.getCommonName()).thenReturn(Optional.of(COMMON_NAME_VALID));
        when(envsForCsr.getCountry()).thenReturn(Optional.of(COUNTRY_VALID));
        when(envsForCsr.getOrganization()).thenReturn(Optional.of(ORGANIZATION_VALID));
        when(envsForCsr.getState()).thenReturn(Optional.of(STATE_VALID));
    }

    private void mockEnvsWithInvalidCommonName() {
        mockEnvsWithAllValidParameters();
        when(envsForCsr.getCommonName()).thenReturn(Optional.of(COMMON_NAME_INVALID));
    }

    private void mockEnvsWithInvalidCountry() {
        mockEnvsWithAllValidParameters();
        when(envsForCsr.getCountry()).thenReturn(Optional.of(COUNTRY_INVALID));
    }

    private void mockEnvsWithInvalidOrganization() {
        mockEnvsWithAllValidParameters();
        when(envsForCsr.getOrganization()).thenReturn(Optional.of(ORGANIZATION_INVALID));
    }

    private void mockEnvsWithInvalidState() {
        mockEnvsWithAllValidParameters();
        when(envsForCsr.getState()).thenReturn(Optional.empty());
    }

    private void mockEnvsWithInvalidSans() {
        mockEnvsWithAllValidParameters();
        when(envsForCsr.getSubjectAlternativesName()).thenReturn(Optional.of(INVALID_SANS));
    }
}
