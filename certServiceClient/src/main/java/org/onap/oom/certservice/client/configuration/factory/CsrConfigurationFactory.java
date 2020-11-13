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

import static org.onap.oom.certservice.client.configuration.validation.csr.CsrEnvsValueValidators.isSpecialCharPresent;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.onap.oom.certservice.client.configuration.CsrConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.EnvsForCsr;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.CsrConfiguration;
import org.onap.oom.certservice.client.configuration.validation.csr.CommonNameValidator;
import org.onap.oom.certservice.client.configuration.validation.csr.CsrEnvsValueValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsrConfigurationFactory implements ConfigurationFactory<CsrConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrConfigurationFactory.class);
    private static final String SANS_DELIMITER = ",";

    private final EnvsForCsr envsForCsr;
    private final CommonNameValidator commonNameValidator;
    private final SanMapper sanMapper;

    public CsrConfigurationFactory(EnvsForCsr envsForCsr, CommonNameValidator commonNameValidator,
        SanMapper sanMapper) {
        this.envsForCsr = envsForCsr;
        this.commonNameValidator = commonNameValidator;
        this.sanMapper = sanMapper;
    }

    @Override
    public CsrConfiguration create() throws CsrConfigurationException {

        CsrConfiguration configuration = new CsrConfiguration();

        envsForCsr.getCommonName()
            .filter(commonNameValidator)
            .map(configuration::setCommonName)
            .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.COMMON_NAME + " is invalid."));

        envsForCsr.getOrganization()
            .filter(org -> !isSpecialCharPresent(org))
            .map(configuration::setOrganization)
            .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.ORGANIZATION + " is invalid."));

        envsForCsr.getState()
            .map(configuration::setState)
            .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.STATE + " is invalid."));

        envsForCsr.getCountry()
            .filter(CsrEnvsValueValidators::isCountryValid)
            .map(configuration::setCountry)
            .orElseThrow(() -> new CsrConfigurationException(CsrConfigurationEnvs.COUNTRY + " is invalid."));

        envsForCsr.getOrganizationUnit()
            .map(configuration::setOrganizationUnit);

        envsForCsr.getLocation()
            .map(configuration::setLocation);

        envsForCsr.getSubjectAlternativesName()
            .map(sans -> Arrays.asList(sans.split(SANS_DELIMITER)))
            .map(list -> list.stream().map(sanMapper).collect(Collectors.toList()))
            .map(configuration::setSans);

        LOGGER.info("Successful validation of CSR configuration. Configuration data: {}", configuration.toString());

        return configuration;
    }

}
