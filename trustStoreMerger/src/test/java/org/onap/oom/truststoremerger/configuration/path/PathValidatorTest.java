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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.oom.truststoremerger.configuration.path.PathValidator;

import static org.assertj.core.api.Assertions.assertThat;

class PathValidatorTest {

    private final PathValidator validator = new PathValidator();

    @ParameterizedTest()
    @ValueSource(strings = {"/opt/app/truststore.pem", "/opt/app/truststore.jks",
            "/opt/app/truststore.p12", "/truststore.pem"})
    void shouldAcceptValidTruststorePaths(String path) {
        assertThat(validator.isTruststorePathValid(path)).isTrue();
    }

    @ParameterizedTest()
    @ValueSource(strings = {"/opt/app/truststore.pass", "/opt/app/truststore.invalid", "/",
            "truststore", "opt/app/truststore.p12", "/?.pem", "/.pem"})
    void shouldRejectInvalidTruststorePaths(String path) {
        assertThat(validator.isTruststorePathValid(path)).isFalse();
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", "/opt/app/truststore.pass", "/truststore.pass"})
    void shouldAcceptValidTruststorePasswordPaths(String path) {
        assertThat(validator.isTruststorePasswordPathValid(path)).isTrue();
    }

    @ParameterizedTest()
    @ValueSource(strings = {"/opt/app/truststore.pem", "/opt/app/truststore.jks",
            "/opt/app/truststore.p12", "/", "truststore", "opt/app/truststore.p12", "/?.pass", "/.pass"})
    void shouldRejectInvalidTruststorePasswordPaths(String path) {
        assertThat(validator.isTruststorePasswordPathValid(path)).isFalse();
    }

}
