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

package org.onap.oom.certservice.client.configuration.validation.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.onap.oom.certservice.client.configuration.validation.client.ClientEnvsValueValidators.isCaNameValid;
import static org.onap.oom.certservice.client.configuration.validation.client.ClientEnvsValueValidators.isPathValid;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClientEnvsValueValidatorsTest {
    @ParameterizedTest
    @ValueSource(strings = {"caname", "caname1", "123caName", "ca1name"})
    void shouldAcceptValidCaName(String caName) {
        assertThat(isCaNameValid(caName)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"44caname$", "#caname1", "1c[aname]", "ca1/name", "", " "})
    void shouldRejectInvalidCaName(String caName) {
        assertThat(isCaNameValid(caName)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/var/log", "/", "/var/log/", "/second_var", "/second-var"})
    void shouldAcceptValidPath(String path) {
        assertThat(isPathValid(path)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/var/log?", "", "var_", "var", "//", "/var//log"})
    void shouldRejectInvalidPath(String path) {
        assertThat(isPathValid(path)).isFalse();
    }

}
