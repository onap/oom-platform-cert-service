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


import org.onap.oom.certservice.client.certification.ArtifactsCreatorProvider;
import org.onap.oom.certservice.client.configuration.exception.ClientConfigurationException;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.ConfigurationModel;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class AbstractConfigurationFactory<T extends ConfigurationModel> {

    abstract T create() throws ClientConfigurationException, CsrConfigurationException;

    public boolean isPathValid(String path) {
        return path.matches("^/|(/[a-zA-Z0-9_-]+)+/?$");
    }

    public boolean isCaNameValid(String caName) {
        return caName.matches("^[a-zA-Z0-9_.~-]{1,128}$");
    }

    public boolean isCommonNameValid(String commonName) {
        return !isSpecialCharsPresent(commonName)
                && !isHttpProtocolsPresent(commonName)
                && !isIpAddressPresent(commonName)
                && !isPortNumberPresent(commonName);
    }

    public boolean isSpecialCharsPresent(String stringToCheck) {
        return Pattern.compile("[~#@*$+%!()?/{}<>\\|_^]").matcher(stringToCheck).find();
    }

    public boolean isCountryValid(String country) {
        return Arrays.asList(Locale.getISOCountries()).contains(country);
    }

    public boolean isOutputTypeValid(String outputType) {
        return Arrays.stream(ArtifactsCreatorProvider.values())
                .map(ArtifactsCreatorProvider::toString)
                .anyMatch(name -> name.equals(outputType));
    }

    private boolean isPortNumberPresent(String stringToCheck) {
        return Pattern.compile(":[0-9]{1,5}").matcher(stringToCheck).find();
    }

    private boolean isIpAddressPresent(String stringToCheck) {
        return Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").matcher(stringToCheck).find();
    }

    private boolean isHttpProtocolsPresent(String stringToCheck) {
        return Pattern.compile("[h][t][t][p][:][/][/]|[h][t][t][p][s][:][/][/]").matcher(stringToCheck).find();
    }
}
