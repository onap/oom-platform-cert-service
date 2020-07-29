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

import org.onap.oom.certservice.client.configuration.CsrConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.EnvsForCsr;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.CsrConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsrConfigurationFactory extends AbstractConfigurationFactory<CsrConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrConfigurationFactory.class);
    private final EnvsForCsr envsForCsr;

    public CsrConfigurationFactory(EnvsForCsr envsForCsr) {
        this.envsForCsr = envsForCsr;
    }

    @Override
    public CsrConfiguration create() throws CsrConfigurationException {

        CsrConfiguration configuration = new CsrConfiguration();

        envsForCsr.getCommonName()
                .filter(this::isCommonNameValid)
                .map(configuration::setCommonName)
                .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.COMMON_NAME + " is invalid."));

        envsForCsr.getOrganization()
                .filter(org -> !isSpecialCharsPresent(org))
                .map(configuration::setOrganization)
                .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.ORGANIZATION + " is invalid."));

        envsForCsr.getState()
                .map(configuration::setState)
                .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.STATE + " is invalid."));

        envsForCsr.getCountry()
                .filter(this::isCountryValid)
                .map(configuration::setCountry)
                .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.COUNTRY + " is invalid."));

        envsForCsr.getOrganizationUnit()
                .map(configuration::setOrganizationUnit);

        envsForCsr.getLocation()
                .map(configuration::setLocation);

        envsForCsr.getSubjectAlternativesName()
                .map(configuration::setSubjectAlternativeNames);

        LOGGER.info("Successful validation of CSR configuration. Configuration data: {}", configuration.toString());

        return configuration;
    }
}
