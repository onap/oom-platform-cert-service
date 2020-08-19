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

package org.onap.oom.truststoremerger.certification.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;
import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;


@ExtendWith(MockitoExtension.class)
class TruststoresPathsProviderTest {

    private static final String VALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String VALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:";
    private static final String INVALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.invalid";
    private static final String INVALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:/.pass";

    @Mock
    private EnvProvider envProvider;
    private TruststoresPathsProvider truststoresPathsProvider;

    @BeforeEach
    void setUp() {
        truststoresPathsProvider = new TruststoresPathsProvider(envProvider, new PathValidator());
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresValid() throws TruststoresPathsProviderException {
        mockTruststoresEnv(VALID_TRUSTSTORES);

        assertThat(truststoresPathsProvider.getTruststores())
                .contains("/opt/app/certificates/truststore.jks",
                        "/opt/app/certificates/truststore.pem");
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsValid() throws TruststoresPathsProviderException {
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS);

        assertThat(truststoresPathsProvider.getTruststoresPasswords())
                .contains("/opt/app/certificates/truststore.pass",
                        "");
    }

    @Test
    void shouldThrowExceptionWhenTruststoresEmpty() {
        mockTruststoresEnv("");

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststores);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststoresPathsInvalid() {
        mockTruststoresEnv(INVALID_TRUSTSTORES);

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststores);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePasswordPathsInvalid() {
        mockTruststoresPasswordsEnv(INVALID_TRUSTSTORES_PASSWORDS);

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststoresPasswords);
    }

    private void mockTruststoresEnv(String truststores) {
        mockEnv(truststores, TRUSTSTORES_PATHS_ENV);
    }

    private void mockTruststoresPasswordsEnv(String truststoresPasswords) {
        mockEnv(truststoresPasswords, TRUSTSTORES_PASSWORDS_PATHS_ENV);
    }

    private void mockEnv(String envValue, String envName) {
        when(envProvider.getEnv(envName))
                .thenReturn(Optional.of(envValue));
    }
}
