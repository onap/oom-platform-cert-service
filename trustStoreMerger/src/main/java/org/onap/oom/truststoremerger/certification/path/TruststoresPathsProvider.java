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

package org.onap.oom.truststoremerger.certification.path;

import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;
import static org.onap.oom.truststoremerger.api.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class TruststoresPathsProvider {

    private static final String DELIMITER = ":";
    private static final int NEGATIVE_SPLIT_LIMIT = -1;

    private final EnvProvider envProvider;
    private final PathValidator pathValidator;

    public TruststoresPathsProvider(EnvProvider envProvider, PathValidator pathValidator) {
        this.envProvider = envProvider;
        this.pathValidator = pathValidator;
    }

    public List<String> getTruststores() throws TruststoresPathsProviderException {
        return envProvider.getEnv(TRUSTSTORES_PATHS_ENV)
            .filter(Predicate.not(String::isEmpty))
            .map(this::splitToList)
            .filter(this::validateTruststores)
            .orElseThrow(() -> new TruststoresPathsProviderException(
                TRUSTSTORES_PATHS_ENV + " environment variable does not contain valid truststores paths"));
    }

    public List<String> getTruststoresPasswords() throws TruststoresPathsProviderException {
        return envProvider.getEnv(TRUSTSTORES_PASSWORDS_PATHS_ENV)
            .map(this::splitToList)
            .filter(this::validateTruststoresPasswords)
            .orElseThrow(() -> new TruststoresPathsProviderException(
                TRUSTSTORES_PASSWORDS_PATHS_ENV + " environment variable does not contain valid passwords paths"));
    }

    private boolean validateTruststores(List<String> truststores) {
        return truststores.stream()
            .allMatch(pathValidator::isTruststorePathValid);
    }

    private boolean validateTruststoresPasswords(List<String> truststoresPasswords) {
        return truststoresPasswords.stream()
            .allMatch(pathValidator::isTruststorePasswordPathValid);
    }

    private List<String> splitToList(String stringToSplit) {
        return Arrays.asList(stringToSplit.split(DELIMITER, NEGATIVE_SPLIT_LIMIT));
    }
}
