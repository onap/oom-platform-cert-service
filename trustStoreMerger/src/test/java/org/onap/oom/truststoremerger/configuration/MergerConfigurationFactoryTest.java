/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.truststoremerger.configuration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.configuration.MergerConfigurationFactory.TRUSTSTORES_ENV;
import static org.onap.oom.truststoremerger.configuration.MergerConfigurationFactory.TRUSTSTORES_PASSWORDS_ENV;

@ExtendWith(MockitoExtension.class)
class MergerConfigurationFactoryTest {

    private static final String VALID_TRUSTSTORES_1 = "/opt/app/certificates/truststore.jks";
    private static final String VALID_TRUSTSTORES_PASSWORDS_1 = "/opt/app/certificates/truststore.pass";
    private static final String VALID_TRUSTSTORES_2 = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String VALID_TRUSTSTORES_PASSWORDS_2 = "/opt/app/certificates/truststore.pass:";
    private static final String INVALID_TRUSTSTORES_2 = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.invalid";
    private static final String INVALID_TRUSTSTORES_PASSWORDS_2 = "/opt/app/certificates/truststore.pass:/.pass";

    @Mock
    EnvProvider envProvider;
    private MergerConfigurationFactory factory;

    @BeforeEach
    void setUp() {
        PathValidator pathValidator = new PathValidator();
        factory = new MergerConfigurationFactory(envProvider, pathValidator);
    }

    @Test
    void shouldReturnCorrectMergerConfiguration_OneFilePair() throws MergerConfigurationException {
        mockTruststoresEnv(VALID_TRUSTSTORES_1);
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS_1);

        MergerConfiguration configuration = factory.createConfiguration();

        assertThat(configuration.getTruststoreFilePaths().size()).isEqualTo(1);
        assertThat(configuration.getTruststoreFilePasswordPaths().size()).isEqualTo(1);
    }

    @Test
    void shouldReturnCorrectMergerConfiguration_TwoFilePairs() throws MergerConfigurationException {
        mockTruststoresEnv(VALID_TRUSTSTORES_2);
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS_2);

        MergerConfiguration configuration = factory.createConfiguration();

        assertThat(configuration.getTruststoreFilePaths().size()).isEqualTo(2);
        assertThat(configuration.getTruststoreFilePasswordPaths().size()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenTruststoresEmpty() {
        mockTruststoresEnv("");

        assertThatExceptionOfType(MergerConfigurationException.class)
                .isThrownBy(factory::createConfiguration);
    }

    @Test
    void shouldThrowExceptionWhenTruststoresLenghtDiffrentThanTruststoresPasswordsLength() {
        mockTruststoresEnv(VALID_TRUSTSTORES_1);
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS_2);

        assertThatExceptionOfType(MergerConfigurationException.class)
                .isThrownBy(factory::createConfiguration);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePathsInvalid() {
        mockTruststoresEnv(INVALID_TRUSTSTORES_2);

        assertThatExceptionOfType(MergerConfigurationException.class)
                .isThrownBy(factory::createConfiguration);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePasswordPathsInvalid() {
        mockTruststoresEnv(VALID_TRUSTSTORES_2);
        mockTruststoresPasswordsEnv(INVALID_TRUSTSTORES_PASSWORDS_2);

        assertThatExceptionOfType(MergerConfigurationException.class)
                .isThrownBy(factory::createConfiguration);
    }

    private void mockTruststoresPasswordsEnv(String truststoresPasswords) {
        mockEnv(truststoresPasswords, TRUSTSTORES_PASSWORDS_ENV);
    }

    private void mockTruststoresEnv(String truststores) {
        mockEnv(truststores, TRUSTSTORES_ENV);
    }

    private void mockEnv(String envValue, String envName) {
        when(envProvider.getEnv(envName))
                .thenReturn(Optional.of(envValue));
    }

}
