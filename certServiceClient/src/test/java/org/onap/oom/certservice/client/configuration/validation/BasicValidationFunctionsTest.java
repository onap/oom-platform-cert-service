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
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isAlphaNumeric;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isCountryValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isDomainNameValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isEmailAddressValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isIpAddressValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isPathValid;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BasicValidationFunctionsTest {

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

    @ParameterizedTest
    @ValueSource(strings = {"PL", "DE", "PN", "US", "IO", "CA", "KH", "CO", "DK", "EC", "CZ", "CN", "BR", "BD", "BE"})
    void shouldAcceptValidCountryCode(String countryCode) {
        assertThat(isCountryValid(countryCode)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "QQ", "AFG", "D", "&*", "!", "ONAP", "p", "pl", "us", "afg"})
    void shouldRejectInvalidCountryCode(String countryCode) {
        assertThat(isCountryValid(countryCode)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"caname", "caname1", "123caName", "ca1name"})
    void shouldAcceptValidAlphanumeric(String caName) {
        assertThat(isAlphaNumeric(caName)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"44caname$", "#caname1", "1c_aname", "ca1-name"})
    void shouldRejectInvalidAlphanumeric(String caName) {
        assertThat(isAlphaNumeric(caName)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample@example.com", "onap@lolo.pl", "alex.supertramp@onap.com",
        "al.super^tramp@onap.org"})
    void shouldAcceptValidEmailAddr(String emailAddr) {
        assertThat(isEmailAddressValid(emailAddr)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"<sample@example.com>", "onap@lolo", "(mailto)user@onap.com", "mailto:axe@axe.de",
        "incoreectdomaim@onap.ux"})
    void shouldRejectInvalidEmailAddr(String address) {
        assertThat(isEmailAddressValid(address)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"192.168.0.1", "10.183.34.201", "ff:ff:ff:ff::", "ff:ff:ff:ff:ff:ff:ff:ff"})
    void shouldAcceptValidIpAddress(String address) {
        assertThat(isIpAddressValid(address)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"192.168.0.", "ff:ff:ee:a1:", "fg:ff:ff:ff::", "http://10.183.34.201",
        "10.183.34.201:8080"})
    void shouldRejectInvalidIpAddress(String address) {
        assertThat(isIpAddressValid(address)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample.com", "Sample.com", "onap.org", "SRI-NIC.ARPA", "ves-collector", "sample"})
    void shouldAcceptValidDomainName(String domain) {
        assertThat(isDomainNameValid(domain)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "", "sample@onap.org", "192.168.0.1", "http://sample.com"})
    void shouldRejectInvalidDomainNames(String domain) {
        assertThat(isDomainNameValid(domain)).isFalse();
    }

}
