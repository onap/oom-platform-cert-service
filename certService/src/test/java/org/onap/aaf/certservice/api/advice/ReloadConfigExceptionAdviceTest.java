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

package org.onap.oom.certservice.api.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.configuration.CmpServersConfigLoadingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReloadConfigExceptionAdviceTest {

    private static final String ERROR_MESSAGE = "Exception occurred during CMP Servers configuration loading";

    private ReloadConfigExceptionAdvice reloadConfigExceptionAdvice;

    @BeforeEach
    void setUp() {
        reloadConfigExceptionAdvice =
                new ReloadConfigExceptionAdvice();
    }

    @Test
    void shouldReturnErrorStatusAndMessageWhenExceptionOccurred() {
        // Given
        CmpServersConfigLoadingException exception = new CmpServersConfigLoadingException(ERROR_MESSAGE);

        // When
        ResponseEntity<String> response = reloadConfigExceptionAdvice.handle(exception);

        // Then
        assertEquals(ERROR_MESSAGE, response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
