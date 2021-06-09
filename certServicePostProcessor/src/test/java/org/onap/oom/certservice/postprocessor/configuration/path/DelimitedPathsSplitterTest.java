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

package org.onap.oom.certservice.postprocessor.configuration.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.TRUSTSTORES_PASSWORDS_PATHS;
import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.TRUSTSTORES_PATHS;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.postprocessor.configuration.exception.CertificatesPathsValidationException;
import org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable;

@ExtendWith(MockitoExtension.class)
class DelimitedPathsSplitterTest {

    private static final String VALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String VALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:";
    private static final String VALID_TRUSTSTORES_PASSWORDS_WITH_EMPTY_IN_THE_MIDDLE = "/opt/app/certificates/truststore.pass::/etc/truststore.pass";
    private static final String INVALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.invalid";
    private static final String INVALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:/.pass";

    private DelimitedPathsSplitter delimitedPathsSplitter;

    @BeforeEach
    void setUp() {
        delimitedPathsSplitter = new DelimitedPathsSplitter();
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresValid() {
        // when, then
        assertThat(delimitedPathsSplitter.getValidatedPaths(TRUSTSTORES_PATHS, Optional.of(VALID_TRUSTSTORES)))
            .containsSequence("/opt/app/certificates/truststore.jks",
                "/opt/app/certificates/truststore.pem");
    }

    @Test
    void shouldThrowExceptionWhenTruststoresPathsEnvIsEmpty() {
        // when, then
        assertCorrectExceptionIsThrownFor(TRUSTSTORES_PATHS, "");
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststoresPathsInvalid() {
        // when, then
        assertCorrectExceptionIsThrownFor(TRUSTSTORES_PATHS, INVALID_TRUSTSTORES);
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsValid() {
        // when, then
        assertThat(delimitedPathsSplitter
            .getValidatedPaths(TRUSTSTORES_PASSWORDS_PATHS, Optional.of(VALID_TRUSTSTORES_PASSWORDS)))
            .containsSequence("/opt/app/certificates/truststore.pass", "");
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsContainsEmptyPathsInTheMiddle() {
        // when, then
        assertThat(delimitedPathsSplitter.getValidatedPaths(TRUSTSTORES_PASSWORDS_PATHS,
            Optional.of(VALID_TRUSTSTORES_PASSWORDS_WITH_EMPTY_IN_THE_MIDDLE))).containsSequence(
            "/opt/app/certificates/truststore.pass",
            "",
            "/etc/truststore.pass"
        );
    }

    @Test
    void shouldThrowExceptionWhenTruststoresPasswordsPathEnvIsEmpty() {
        // when, then
        assertCorrectExceptionIsThrownFor(TRUSTSTORES_PASSWORDS_PATHS, "");
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePasswordPathsInvalid() {
        // when, then
        assertCorrectExceptionIsThrownFor(TRUSTSTORES_PASSWORDS_PATHS, INVALID_TRUSTSTORES_PASSWORDS);
    }

    private void assertCorrectExceptionIsThrownFor(EnvVariable envVariable, String envValue) {
        final Optional<String> envValueOptional = Optional.of(envValue);
        assertThatExceptionOfType(CertificatesPathsValidationException.class)
            .isThrownBy(() -> delimitedPathsSplitter
                .getValidatedPaths(envVariable, envValueOptional));
    }
}
