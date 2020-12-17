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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AbstractConfigurationFactoryTest {

    private final AbstractConfigurationFactory cut = mock(AbstractConfigurationFactory.class, Mockito.CALLS_REAL_METHODS);

    @ParameterizedTest
    @ValueSource(strings = {"/var/log", "/", "/var/log/", "/second_var", "/second-var"})
    void shouldAcceptValidPath(String path) {
        assertThat(cut.isPathValid(path)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/var/log?", "", "var_", "var", "//", "/var//log"})
    void shouldRejectInvalidPath(String path) {
        assertThat(cut.isPathValid(path)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PL", "DE", "PN", "US", "IO", "CA", "KH", "CO", "DK", "EC", "CZ", "CN", "BR", "BD", "BE"})
    void shouldAcceptValidCountryCode(String countryCode) {
        assertThat(cut.isCountryValid(countryCode)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "QQ", "AFG", "D", "&*", "!", "ONAP", "p", "pl", "us", "afg"})
    void shouldRejectInvalidCountryCode(String countryCode) {
        assertThat(cut.isCountryValid(countryCode)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"caname", "caname1", "123caName", "ca1name", "ca_name", "ca-name", "ca.na~me"})
    void shouldAcceptValidCaName(String caName) {
        assertThat(cut.isCaNameValid(caName)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"44caname$", "#caname1", "1c[aname]", "ca1/name", "", " "})
    void shouldRejectInvalidCaName(String caName) {
        assertThat(cut.isCaNameValid(caName)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"example.com", "www.example.com"})
    void shouldAcceptValidCommonName(String commonName) {
        assertThat(cut.isCommonNameValid(commonName)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "http://example.com", "example.com:8080", "0.0.0.0", "@#$%.com"})
    void shouldRejectInvalidCommonName(String commonName) {
        assertThat(cut.isCommonNameValid(commonName)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"JKS", "P12", "PEM"})
    void shouldAcceptValidOutputType(String outputType) {
        assertThat(cut.isOutputTypeValid(outputType)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"jks", "p12", "pem", "", "pass", "!@$#pp"})
    void shouldRejectInvalidOutputType(String outputType) {
        assertThat(cut.isOutputTypeValid(outputType)).isFalse();
    }
}
