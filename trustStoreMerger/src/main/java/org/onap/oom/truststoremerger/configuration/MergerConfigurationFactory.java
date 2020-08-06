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

import org.onap.oom.truststoremerger.certification.file.TruststoresPathsProvider;
import org.onap.oom.truststoremerger.certification.file.TruststoresPathsProviderException;

import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_ENV;
import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_ENV;

import java.util.List;

public class MergerConfigurationFactory {

    private final TruststoresPathsProvider pathsProvider;

    public MergerConfigurationFactory(TruststoresPathsProvider pathsProvider) {
        this.pathsProvider = pathsProvider;
    }

    public MergerConfiguration createConfiguration()
        throws MergerConfigurationException, TruststoresPathsProviderException {
        List<String> truststores = pathsProvider.getTruststores();
        List<String> truststoresPasswords = pathsProvider.getTruststoresPasswords();

        if (truststores.size() != truststoresPasswords.size()) {
            throw new MergerConfigurationException(
                "Size of " + TRUSTSTORES_ENV
                    + " does not match size of " + TRUSTSTORES_PASSWORDS_ENV + " environment variables");
        }

        return new MergerConfiguration(truststores, truststoresPasswords);
    }
}
