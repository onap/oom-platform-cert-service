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

package org.onap.aaf.certservice.client.model;

import org.onap.aaf.certservice.client.common.ClientConfigurationEnvs;
import org.onap.aaf.certservice.client.common.EnvValidationUtils;
import org.onap.aaf.certservice.client.common.EnvsForClient;
import org.onap.aaf.certservice.client.exceptions.ClientConfigurationException;

import java.util.Optional;

class ClientConfigurationFactory implements AbstractConfigurationFactory<ClientConfiguration> {

    private final EnvsForClient envsForClient;


    ClientConfigurationFactory(EnvsForClient envsForClient) {
        this.envsForClient = envsForClient;
    }


    @Override
    public ClientConfiguration create() throws ClientConfigurationException {

        ClientConfiguration configuration = new ClientConfiguration();

        Optional.ofNullable(envsForClient.getUrlToCertService()).filter(EnvValidationUtils::isEnvExists)
                .map(configuration::setUrlToCertService);

        Optional.ofNullable(envsForClient.getRequestTimeOut()).filter(EnvValidationUtils::isEnvExists)
                .map(timeout -> configuration.setRequestTimeout(Integer.valueOf(timeout)));

        Optional.ofNullable(envsForClient.getOutputPath()).filter(EnvValidationUtils::isEnvExists)
                .filter(EnvValidationUtils::isPathValid)
                .map(configuration::setCertsOutputPath)
                .orElseThrow(() -> new ClientConfigurationException(ClientConfigurationEnvs.OUTPUT_PATH + " is invalid."));

        Optional.ofNullable(envsForClient.getCaName()).filter(EnvValidationUtils::isEnvExists)
                .filter(EnvValidationUtils::isAlphaNumeric)
                .map(configuration::setCaName)
                .orElseThrow(() -> new ClientConfigurationException(ClientConfigurationEnvs.CA_NAME + " is invalid."));

        return configuration;
    }
}

