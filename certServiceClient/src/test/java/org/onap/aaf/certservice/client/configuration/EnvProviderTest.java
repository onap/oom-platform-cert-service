/*
 * ============LICENSE_START=======================================================
 * aaf-certservice-client
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
package org.onap.aaf.certservice.client.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EnvProviderTest {
    private static final String TEST_ENV = "testEnv";
    private static final String TEST_ENV_VALUE = "prod";

    private EnvProvider envProvider;

    @BeforeEach
    public void setUp(){
         envProvider = Mockito.spy(EnvProvider.class);
    }

    @Test
    public void shouldReturnSystemEnvVariableWhenItWasDefined(){
        // given
        when(envProvider.getSystemEnv(TEST_ENV)).thenReturn(TEST_ENV_VALUE);

        // when
        final Optional<String> testEnv = envProvider.readEnvVariable(TEST_ENV);

        // then
        assertThat(testEnv.isPresent()).isTrue();
        assertThat(testEnv.get()).isEqualTo(TEST_ENV_VALUE);
    }

    @Test
    public void shouldReportThatSystemEnvVariableIsNotPresentWhenItWasNotDefined(){
        // when
        final Optional<String> testEnv = envProvider.readEnvVariable(TEST_ENV);

        // then
        assertThat(testEnv.isPresent()).isFalse();
    }
}
