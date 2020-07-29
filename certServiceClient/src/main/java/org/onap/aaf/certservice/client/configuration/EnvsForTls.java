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

package org.onap.oom.certservice.client.configuration;

import java.util.Optional;

public class EnvsForTls {
    private final EnvProvider envProvider = new EnvProvider();

    public Optional<String> getKeystorePath() {
        return readEnv(TlsConfigurationEnvs.KEYSTORE_PATH);
    }

    public Optional<String> getKeystorePassword() {
        return readEnv(TlsConfigurationEnvs.KEYSTORE_PASSWORD);
    }

    public Optional<String> getTruststorePath() {
        return readEnv(TlsConfigurationEnvs.TRUSTSTORE_PATH);
    }

    public Optional<String> getTruststorePassword() {
        return readEnv(TlsConfigurationEnvs.TRUSTSTORE_PASSWORD);
    }

    Optional<String> readEnv(TlsConfigurationEnvs envName) {
        return envProvider.readEnvVariable(envName.toString());
    }
}
