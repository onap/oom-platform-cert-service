/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.aaf.certservice.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfig;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class ReadinessControllerTest {

    @Mock
    private CmpServersConfig cmpServersConfig;

    @Test
    public void shouldReturnStatusOkWhenConfigIsReady() {
        // Given
        Mockito.when(cmpServersConfig.isReady()).thenReturn(true);

        // Then
        assertThat(new ReadinessController(cmpServersConfig).checkReady().getStatusCode()).isEqualTo(HttpStatus.OK);
        ;
    }

    @Test
    public void shouldReturnStatusServiceUnavailableWhenConfigIsNotReady() {
        // Given
        Mockito.when(cmpServersConfig.isReady()).thenReturn(false);

        // Then
        assertThat(new ReadinessController(cmpServersConfig).checkReady().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        ;
    }

}
