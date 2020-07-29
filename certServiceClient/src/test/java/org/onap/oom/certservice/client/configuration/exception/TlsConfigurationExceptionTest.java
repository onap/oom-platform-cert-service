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

package org.onap.oom.certservice.client.configuration.exception;

import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.api.ExitStatus;

import static org.assertj.core.api.Assertions.assertThat;


class TlsConfigurationExceptionTest {

    @Test
    void containsProperExitStatus() {
        // Given
        ExitStatus exitStatus = null;

        // When
        try {
            throw new TlsConfigurationException("Test message");
        } catch (TlsConfigurationException e) {
            exitStatus = e.applicationExitStatus();
        }

        // Then
        assertThat(exitStatus)
                .isNotNull()
                .isEqualTo(ExitStatus.TLS_CONFIGURATION_EXCEPTION);
    }
}
