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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.function.Function;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.ValidatedSan;

class SanMapperTest {

    private Function<String, ValidatedSan> cut = new SanMapper().create();

    @Test
    void shouldCorrectlyMapIpAddress() {
        // given
        String san = "192.178.2.3";
        // when
        ValidatedSan result = cut.apply(san);
        // then
        assertThat(result.getSan()).isEqualTo(san);
        assertThat(result.getTypeOfSan()).isEqualTo(GeneralName.iPAddress);
    }

    @Test
    void shouldCorrectlyMapEmailAddress() {
        // given
        String san = "foo@bar.com";
        // when
        ValidatedSan result = cut.apply(san);
        // then
        assertThat(result.getSan()).isEqualTo(san);
        assertThat(result.getTypeOfSan()).isEqualTo(GeneralName.rfc822Name);
    }

    @Test
    void shouldCorrectlyMapDomain() {
        // given
        String san = "onap.org";
        // when
        ValidatedSan result = cut.apply(san);
        // then
        assertThat(result.getSan()).isEqualTo(san);
        assertThat(result.getTypeOfSan()).isEqualTo(GeneralName.dNSName);
    }

    @Test
    void shouldThrowExceptionOnIncorrectString() {
        // given
        String san = "http://198.168.0.2";
        // when, then
        assertThatExceptionOfType(CsrConfigurationException.class)
            .isThrownBy(() -> cut.apply(san))
            .withMessage("San :" + san + " does not match any requirements");
    }
}
