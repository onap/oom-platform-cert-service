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

package org.onap.oom.truststoremerger.configuration;

import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.KEYSTORE_DESTINATION_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.KEYSTORE_SOURCE_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;

import java.util.List;
import org.onap.oom.truststoremerger.configuration.exception.MergerConfigurationException;
import org.onap.oom.truststoremerger.configuration.exception.TruststoresPathsProviderException;
import org.onap.oom.truststoremerger.configuration.model.MergerConfiguration;
import org.onap.oom.truststoremerger.configuration.path.DelimitedPathsReader;

public class MergerConfigurationProvider {

    private final DelimitedPathsReader truststoresPathsReader;
    private final DelimitedPathsReader truststoresPasswordsPathsReader;
    private final DelimitedPathsReader copierPathsReader;

    public MergerConfigurationProvider(DelimitedPathsReader truststoresPathsReader,
        DelimitedPathsReader truststoresPasswordsPathsReader, DelimitedPathsReader copierPathsReader) {
        this.truststoresPathsReader = truststoresPathsReader;
        this.truststoresPasswordsPathsReader = truststoresPasswordsPathsReader;
        this.copierPathsReader = copierPathsReader;
    }

    public MergerConfiguration createConfiguration()
        throws MergerConfigurationException, TruststoresPathsProviderException {
        List<String> truststoresPaths = truststoresPathsReader.get(TRUSTSTORES_PATHS_ENV);
        List<String> truststoresPasswordsPaths = truststoresPasswordsPathsReader.get(TRUSTSTORES_PASSWORDS_PATHS_ENV);
        List<String> sourceKeystorePaths = copierPathsReader.get(KEYSTORE_SOURCE_PATHS_ENV);
        List<String> destinationKeystorePaths = copierPathsReader.get(KEYSTORE_DESTINATION_PATHS_ENV);

        areListsOfPathsSameSize(truststoresPaths, truststoresPasswordsPaths, TRUSTSTORES_PATHS_ENV,
            TRUSTSTORES_PASSWORDS_PATHS_ENV);
        areListsOfPathsSameSize(sourceKeystorePaths, destinationKeystorePaths, KEYSTORE_SOURCE_PATHS_ENV,
            KEYSTORE_DESTINATION_PATHS_ENV);

        return new MergerConfiguration(truststoresPaths, truststoresPasswordsPaths, sourceKeystorePaths,
            destinationKeystorePaths);
    }

    private void areListsOfPathsSameSize(List<String> firstParamList, List<String> secondParamList,
        String firstListEnvName, String secondListEnvName)
        throws MergerConfigurationException {
        if (firstParamList.size() != secondParamList.size()) {
            throw new MergerConfigurationException(
                "Size of " + firstListEnvName
                    + " does not match size of " + secondListEnvName + " environment variables");
        }
    }
}
