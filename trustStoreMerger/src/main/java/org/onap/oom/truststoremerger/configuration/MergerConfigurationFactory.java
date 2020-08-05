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

import java.util.Arrays;
import java.util.List;

public class MergerConfigurationFactory {

    static final String TRUSTSTORES_ENV = "TRUSTSTORES";
    static final String TRUSTSTORES_PASSWORDS_ENV = "TRUSTSTORES_PASSWORDS";
    private static final String DELIMITER = ":";

    private final EnvProvider envProvider;
    private final PathValidator pathValidator;

    public MergerConfigurationFactory(EnvProvider envProvider, PathValidator pathValidator) {
        this.envProvider = envProvider;
        this.pathValidator = pathValidator;
    }

    public MergerConfiguration createConfiguration() throws MergerConfigurationException {
        List<String> truststores = getTruststores();
        List<String> truststoresPasswords = getTruststoresPasswords();
        if (truststores.size() != truststoresPasswords.size())
            throw new MergerConfigurationException("Size of TRUSTSTORES does not match size of TRUSTSTORES_PASSWORDS environment variables");

        MergerConfiguration configuration =
                new MergerConfiguration(truststores, truststoresPasswords);
        return configuration;
    }

    private List<String> getTruststores() throws MergerConfigurationException {
        return envProvider.getEnv(TRUSTSTORES_ENV)
                .filter(s -> !s.isEmpty())
                .map(this::splitToList)
                .filter(this::validateTruststores)
                .orElseThrow(() -> new MergerConfigurationException("TRUSTSTORES environment variable does not contain valid truststores paths"));
    }

    private List<String> getTruststoresPasswords() throws MergerConfigurationException {
        return envProvider.getEnv(TRUSTSTORES_PASSWORDS_ENV)
                .map(this::splitToList)
                .filter(this::validateTruststoresPasswords)
                .orElseThrow(() -> new MergerConfigurationException("TRUSTSTORES_PASSWORDS environment variable does not contain valid passwords paths"));
    }

    private boolean validateTruststores(List<String> truststores) {
        return truststores.stream().allMatch(pathValidator::isTruststorePathValid);
    }

    private boolean validateTruststoresPasswords(List<String> truststoresPasswords) {
        return truststoresPasswords.stream().allMatch(pathValidator::isTruststorePasswordPathValid);
    }

    private List<String> splitToList(String stringToSplit) {
        return Arrays.asList(stringToSplit.split(DELIMITER, -1));
    }
}
