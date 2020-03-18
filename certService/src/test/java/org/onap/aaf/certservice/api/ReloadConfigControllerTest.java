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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfig;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfigLoadingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ReloadConfigControllerTest {

    private static final String ERROR_MESSAGE = "Exception occurred during CMP Servers configuration loading";

    private ReloadConfigController reloadConfigController;

    @Mock
    public CmpServersConfig cmpServersConfig;

    @BeforeEach
    void setUp() {
        this.reloadConfigController = new ReloadConfigController(cmpServersConfig);
    }

    @Test
    void shouldReturnStatusOkWhenSuccessfullyReloaded() throws CmpServersConfigLoadingException {
        // When
        ResponseEntity<String> response = reloadConfigController.reloadConfiguration();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRethrowSameErrorWhenFailedToReload() throws CmpServersConfigLoadingException {
        // Given
        doThrow(new CmpServersConfigLoadingException(ERROR_MESSAGE)).when(cmpServersConfig).reloadConfiguration();

        // When
        Exception exception = assertThrows(
                CmpServersConfigLoadingException.class,
                () -> reloadConfigController.reloadConfiguration());

        // Then
        Assertions.assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE);
    }


}
