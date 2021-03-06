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

package org.onap.oom.certservice.postprocessor.configuration.path;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.onap.oom.certservice.postprocessor.configuration.exception.CertificatesPathsValidationException;
import org.onap.oom.certservice.postprocessor.configuration.model.EnvVariable;

public class DelimitedPathsSplitter {

    private static final String DELIMITER = ":";
    private static final int NEGATIVE_SPLIT_LIMIT = -1;

    public List<String> getValidatedPaths(EnvVariable envVariable, Optional<String> envValue)
        throws CertificatesPathsValidationException {
        return envValue.filter(this::hasValue)
            .map(this::splitToList)
            .filter(envVariable.getValidationFunction())
            .orElseThrow(() -> new CertificatesPathsValidationException(
                envVariable + " environment variable does not contain valid paths"));
    }

    private boolean hasValue(String envValue) {
        return !envValue.isEmpty();
    }

    private List<String> splitToList(String stringToSplit) {
        return Arrays.asList(stringToSplit.split(DELIMITER, NEGATIVE_SPLIT_LIMIT));
    }
}
