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

import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;
import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;

import java.util.List;
import org.onap.oom.truststoremerger.configuration.exception.MergerConfigurationException;
import org.onap.oom.truststoremerger.configuration.exception.TruststoresPathsProviderException;
import org.onap.oom.truststoremerger.configuration.model.MergerConfiguration;
import org.onap.oom.truststoremerger.configuration.path.TruststoresPathsProvider;

public class MergerConfigurationProvider {

    private final TruststoresPathsProvider pathsProvider;

    public MergerConfigurationProvider(TruststoresPathsProvider pathsProvider) {
        this.pathsProvider = pathsProvider;
    }

    public MergerConfiguration createConfiguration()
        throws MergerConfigurationException, TruststoresPathsProviderException {
        List<String> truststores = pathsProvider.getTruststores();
        List<String> truststoresPasswords = pathsProvider.getTruststoresPasswords();

        if (truststores.size() != truststoresPasswords.size()) {
            throw new MergerConfigurationException(
                "Size of " + TRUSTSTORES_PATHS_ENV
                    + " does not match size of " + TRUSTSTORES_PASSWORDS_PATHS_ENV + " environment variables");
        }

        return new MergerConfiguration(truststores, truststoresPasswords);
    }
}
