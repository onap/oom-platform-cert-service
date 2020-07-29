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

package org.onap.oom.certservice.client.configuration.factory;

import org.onap.oom.certservice.client.configuration.ClientConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.EnvsForClient;
import org.onap.oom.certservice.client.configuration.exception.ClientConfigurationException;
import org.onap.oom.certservice.client.configuration.model.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ClientConfigurationFactory extends AbstractConfigurationFactory<ClientConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigurationFactory.class);
    private final EnvsForClient envsForClient;

    public ClientConfigurationFactory(EnvsForClient envsForClient) {
        this.envsForClient = envsForClient;
    }

    @Override
    public ClientConfiguration create() throws ClientConfigurationException {

        ClientConfiguration configuration = new ClientConfiguration();


        envsForClient.getUrlToCertService()
                .map(configuration::setUrlToCertService);

        envsForClient.getRequestTimeOut()
                .map(timeout -> configuration.setRequestTimeout(Integer.valueOf(timeout)));

        envsForClient.getOutputPath()
                .filter(this::isPathValid)
                .map(configuration::setCertsOutputPath)
                .orElseThrow(() -> new ClientConfigurationException(ClientConfigurationEnvs.OUTPUT_PATH + " is invalid."));

        envsForClient.getCaName()
                .filter(this::isAlphaNumeric)
                .map(configuration::setCaName)
                .orElseThrow(() -> new ClientConfigurationException(ClientConfigurationEnvs.CA_NAME + " is invalid."));

        Optional<String> outputType = envsForClient.getOutputType();

        if (outputType.isPresent()) {
            outputType.filter(this::isOutputTypeValid)
                    .map(configuration::setOutputType)
                    .orElseThrow(() -> new ClientConfigurationException(ClientConfigurationEnvs.OUTPUT_TYPE + " is invalid."));
        }

        LOGGER.info("Successful validation of Client configuration. Configuration data: {}", configuration.toString());

        return configuration;
    }
}

