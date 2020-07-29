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

import org.onap.oom.certservice.client.api.ExitStatus;
import org.onap.oom.certservice.client.api.ExitableException;

public class CsrConfigurationException extends ExitableException {
    private static final ExitStatus EXIT_STATUS = ExitStatus.CSR_CONFIGURATION_EXCEPTION;

    public CsrConfigurationException(String message) {
        super(message);
    }

    public ExitStatus applicationExitStatus() {
        return EXIT_STATUS;
    }
}
