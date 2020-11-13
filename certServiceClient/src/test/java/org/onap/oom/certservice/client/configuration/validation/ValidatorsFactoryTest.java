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

package org.onap.oom.certservice.client.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidatorsFactoryTest {

    ValidatorsFactory cut = new ValidatorsFactory();

    @ParameterizedTest
    @ValueSource(strings = {"JKS", "P12", "PEM"})
    void shouldAcceptValidOutputType(String outputType) {
        assertThat(cut.outputTypeValidator().test(outputType)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"jks", "p12", "pem", "", "pass", "!@$#pp"})
    void shouldRejectInvalidOutputType(String outputType) {
        assertThat(cut.outputTypeValidator().test(outputType)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"example.com", "www.example.com"})
    void shouldAcceptValidCommonName(String commonName) {
        assertThat(cut.commonNameValidator().test(commonName)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "http://example.com", "example.com:8080", "0.0.0.0", "@#$%.com"})
    void shouldRejectInvalidCommonName(String commonName) {
        assertThat(cut.commonNameValidator().test(commonName)).isFalse();
    }

}
