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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.function.Function;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.San;

class SanMapperTest {

    private Function<String, San> sanMapper = new SanMapper();

    @ParameterizedTest
    @ValueSource(strings = {"192.178.2.3", "10.183.34.201", "ff:ff:ff:ff:ff:ff:ff:ff", "ff:ff::"})
    void shouldCorrectlyMapIpAddress(String san) {
        // when
        San result = sanMapper.apply(san);
        // then
        assertThat(result.getValue()).isEqualTo(san);
        assertThat(result.getType()).isEqualTo(GeneralName.iPAddress);
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo@bar.com", "sample@example.com", "onap@domain.pl", "alex.supertramp@onap.com",
        "al.super^tramp@onap.org"})
    void shouldCorrectlyMapEmailAddress(String san) {
        // when
        San result = sanMapper.apply(san);
        // then
        assertThat(result.getValue()).isEqualTo(san);
        assertThat(result.getType()).isEqualTo(GeneralName.rfc822Name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample.com", "Sample.com", "onap.org", "SRI-NIC.ARPA", "ves-collector", "sample"})
    void shouldCorrectlyMapDomain(String san) {
        // when
        San result = sanMapper.apply(san);
        // then
        assertThat(result.getValue()).isEqualTo(san);
        assertThat(result.getType()).isEqualTo(GeneralName.dNSName);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "", "192.168.0.", "10.183.34.201:8080", "incoreectdomaim@onap.ux", "<sample@example.com>",
        "onap@domain"})
    void shouldThrowExceptionOnIncorrectString(String san) {
        // when, then
        assertThatExceptionOfType(CsrConfigurationException.class)
            .isThrownBy(() -> sanMapper.apply(san))
            .withMessage("SAN :" + san + " does not match any requirements");
    }
}
