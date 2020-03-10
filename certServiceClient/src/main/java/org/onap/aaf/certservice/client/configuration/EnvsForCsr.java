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

public class EnvsForCsr {
    private final EnvProvider envProvider = new EnvProvider();

    public Optional<String> getCommonName() {
        return readEnv(CsrConfigurationEnvs.COMMON_NAME);
    }

    public Optional<String> getOrganization() {
        return readEnv(CsrConfigurationEnvs.ORGANIZATION);
    }

    public Optional<String> getOrganizationUnit() {
        return readEnv(CsrConfigurationEnvs.ORGANIZATION_UNIT);
    }

    public Optional<String> getLocation() {
        return readEnv(CsrConfigurationEnvs.LOCATION);
    }

    public Optional<String> getState() {
        return readEnv(CsrConfigurationEnvs.STATE);
    }

    public Optional<String> getCountry() {
        return readEnv(CsrConfigurationEnvs.COUNTRY);
    }

    public Optional<String> getSubjectAlternativesName() {
        return readEnv(CsrConfigurationEnvs.SANS);
    }

    private Optional<String> readEnv(CsrConfigurationEnvs envName) {
        return envProvider.readEnvVariable(envName.toString());
    }
}
