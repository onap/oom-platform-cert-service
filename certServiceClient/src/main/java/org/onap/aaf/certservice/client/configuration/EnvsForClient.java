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

import java.util.Optional;

public class EnvsForClient {
    private final EnvProvider envProvider = new EnvProvider();

    public Optional<String> getUrlToCertService() {
        return readEnv(ClientConfigurationEnvs.REQUEST_URL);
    }

    public Optional<String> getRequestTimeOut() {
        return readEnv(ClientConfigurationEnvs.REQUEST_TIMEOUT);
    }

    public Optional<String> getOutputPath() {
        return readEnv(ClientConfigurationEnvs.OUTPUT_PATH);
    }

    public Optional<String> getCaName() {
        return readEnv(ClientConfigurationEnvs.CA_NAME);
    }

    private Optional<String> readEnv(ClientConfigurationEnvs envName) {
        return envProvider.readEnvVariable(envName.toString());
    }
}
