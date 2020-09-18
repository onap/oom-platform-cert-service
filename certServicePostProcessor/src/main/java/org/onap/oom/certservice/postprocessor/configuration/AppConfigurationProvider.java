/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.certservice.postprocessor.configuration;


import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.KEYSTORE_DESTINATION_PATHS;
import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.KEYSTORE_SOURCE_PATHS;
import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.TRUSTSTORES_PASSWORDS_PATHS;
import static org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable.TRUSTSTORES_PATHS;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.onap.oom.certservice.postprocessor.configuration.path.env.EnvReader;
import org.onap.oom.certservice.postprocessor.configuration.exception.ConfigurationException;
import org.onap.oom.certservice.postprocessor.configuration.model.AppConfiguration;
import org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable;
import org.onap.oom.certservice.postprocessor.configuration.path.DelimitedPathsSplitter;

public class AppConfigurationProvider {

    private final EnvReader envReader;
    private final DelimitedPathsSplitter pathsSplitter;

    public AppConfigurationProvider(DelimitedPathsSplitter pathsSplitter, EnvReader envReader) {
        this.envReader = envReader;
        this.pathsSplitter = pathsSplitter;
    }

    public AppConfiguration createConfiguration() {
        List<String> truststoresPaths = getPaths(TRUSTSTORES_PATHS);
        List<String> truststoresPasswordsPaths = getPaths(TRUSTSTORES_PASSWORDS_PATHS);
        List<String> sourceKeystorePaths = getPaths(KEYSTORE_SOURCE_PATHS);
        List<String> destinationKeystorePaths = getPaths(KEYSTORE_DESTINATION_PATHS);

        ensureSameSize(truststoresPaths, truststoresPasswordsPaths, TRUSTSTORES_PATHS.name(),
            TRUSTSTORES_PASSWORDS_PATHS.name());
        ensureSameSize(sourceKeystorePaths, destinationKeystorePaths, KEYSTORE_SOURCE_PATHS.name(),
            KEYSTORE_DESTINATION_PATHS.name());

        return new AppConfiguration(truststoresPaths, truststoresPasswordsPaths, sourceKeystorePaths,
            destinationKeystorePaths);
    }

    private List<String> getPaths(EnvVariable envVariable) {
        Optional<String> envValue = envReader.getEnv(envVariable.name());
        isMandatoryEnvPresent(envVariable, envValue);
        return envValue.isPresent() ? pathsSplitter.getValidatedPaths(envVariable, envValue) : Collections.emptyList();
    }

    private void isMandatoryEnvPresent(EnvVariable envVariable, Optional<String> envValue) {
        if (envVariable.isMandatory() && envValue.isEmpty()) {
            throw new ConfigurationException(envVariable + " mandatory environment variable is not defined");
        }
    }

    private void ensureSameSize(List<String> firstList, List<String> secondList, String firstListEnvName,
                                String secondListEnvName) {
        if (firstList.size() != secondList.size()) {
            throw new ConfigurationException(
                "Size of " + firstListEnvName
                    + " does not match size of " + secondListEnvName + " environment variables");
        }
    }
}
