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

package org.onap.oom.truststoremerger.configuration.path.env;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.KEYSTORE_DESTINATION_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.TRUSTSTORES_PASSWORDS_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.TRUSTSTORES_PATHS_ENV;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onap.oom.truststoremerger.configuration.exception.MandatoryEnvMissingException;

class EnvProviderTest {

    private static final String SAMPLE_PASS_PATH = "/sample/path/trust.pass";
    EnvProvider provider;

    @BeforeEach
    void setUp() {
        provider = Mockito.spy(EnvProvider.class);
    }

    @Test
    void shouldReturnEmptyOptionalOnMissingOptionalEnv() {
        // when
        Optional<String> result = provider.readEnv(KEYSTORE_DESTINATION_PATHS_ENV);
        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowExceptionOnMissingMandatoryEnv() {
        // when, then
        assertThatExceptionOfType(MandatoryEnvMissingException.class)
            .isThrownBy(() -> provider.readEnv(TRUSTSTORES_PATHS_ENV));
    }

    @Test
    void shouldReturnOptionalEnv() {
        // given
        when(provider.readSystemEnv(TRUSTSTORES_PASSWORDS_PATHS_ENV)).thenReturn(SAMPLE_PASS_PATH);
        // when
        Optional<String> result = provider.readEnv(TRUSTSTORES_PASSWORDS_PATHS_ENV);
        // then
        assertThat(result).isEqualTo(Optional.of(SAMPLE_PASS_PATH));
    }

    @Test
    void shouldReturnMandatoryEnv() {
        // given
        when(provider.readSystemEnv(KEYSTORE_DESTINATION_PATHS_ENV)).thenReturn(SAMPLE_PASS_PATH);
        // when
        Optional<String> result = provider.readEnv(KEYSTORE_DESTINATION_PATHS_ENV);
        // then
        assertThat(result).isEqualTo(Optional.of(SAMPLE_PASS_PATH));
    }
}
