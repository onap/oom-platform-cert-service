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

package org.onap.oom.truststoremerger.configuration.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidCertificatesPaths;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidPasswordPaths;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.configuration.exception.TruststoresPathsProviderException;
import org.onap.oom.truststoremerger.configuration.path.env.EnvProvider;

@ExtendWith(MockitoExtension.class)
class DelimitedPathsReaderTest {

    private static final String VALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String VALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:";
    private static final String VALID_TRUSTSTORES_PASSWORDS_WITH_EMPTY_IN_THE_MIDDLE = "/opt/app/certificates/truststore.pass::/etc/truststore.pass";
    private static final String INVALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.invalid";
    private static final String INVALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:/.pass";

    @Mock
    private EnvProvider envProvider;
    private DelimitedPathsReader delimitedPathsReader;

    @Test
    void shouldReturnCorrectListWhenTruststoresValid() throws TruststoresPathsProviderException {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidCertificatesPaths());
        mockTruststoresEnv(VALID_TRUSTSTORES);

        // when, then
        assertThat(delimitedPathsReader.get(TRUSTSTORES_PATHS_ENV))
            .containsSequence("/opt/app/certificates/truststore.jks",
                "/opt/app/certificates/truststore.pem");
    }

    @Test
    void shouldThrowExceptionWhenTruststoresPathsEnvIsEmpty() throws TruststoresPathsProviderException {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidCertificatesPaths());
        mockTruststoresEnv("");

        // when, then
        assertThatExceptionOfType(TruststoresPathsProviderException.class)
            .isThrownBy(() -> delimitedPathsReader.get(TRUSTSTORES_PATHS_ENV));
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststoresPathsInvalid() {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidCertificatesPaths());
        mockTruststoresEnv(INVALID_TRUSTSTORES);

        // when, then
        assertThatExceptionOfType(TruststoresPathsProviderException.class)
            .isThrownBy(() -> delimitedPathsReader.get(TRUSTSTORES_PATHS_ENV));
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsValid() throws TruststoresPathsProviderException {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidPasswordPaths());
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS);

        // when, then
        assertThat(delimitedPathsReader.get(TRUSTSTORES_PASSWORDS_PATHS_ENV))
            .containsSequence("/opt/app/certificates/truststore.pass", "");
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsContainsEmptyPathsInTheMiddle()
        throws TruststoresPathsProviderException {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidPasswordPaths());
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS_WITH_EMPTY_IN_THE_MIDDLE);

        // when, then
        assertThat(delimitedPathsReader.get(TRUSTSTORES_PASSWORDS_PATHS_ENV)).containsSequence(
            "/opt/app/certificates/truststore.pass",
            "",
            "/etc/truststore.pass"
        );
    }

    @Test
    void shouldThrowExceptionWhenTruststoresPasswordsPathEnvIsEmpty() {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidPasswordPaths());
        mockTruststoresPasswordsEnv("");

        // when, then
        assertThatExceptionOfType(TruststoresPathsProviderException.class)
            .isThrownBy(() -> delimitedPathsReader.get(TRUSTSTORES_PASSWORDS_PATHS_ENV));
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePasswordPathsInvalid() {
        // given
        delimitedPathsReader = new DelimitedPathsReader(envProvider, doesItContainValidPasswordPaths());
        mockTruststoresPasswordsEnv(INVALID_TRUSTSTORES_PASSWORDS);

        // when, then
        assertThatExceptionOfType(TruststoresPathsProviderException.class)
            .isThrownBy(() -> delimitedPathsReader.get(TRUSTSTORES_PASSWORDS_PATHS_ENV));
    }

    private void mockTruststoresEnv(String truststores) {
        mockEnv(TRUSTSTORES_PATHS_ENV, truststores);
    }

    private void mockTruststoresPasswordsEnv(String truststoresPasswords) {
        mockEnv(TRUSTSTORES_PASSWORDS_PATHS_ENV, truststoresPasswords);
    }

    private void mockEnv(String envName, String truststores) {
        when(envProvider.getEnv(envName)).thenReturn(Optional.of(truststores));
    }
}
