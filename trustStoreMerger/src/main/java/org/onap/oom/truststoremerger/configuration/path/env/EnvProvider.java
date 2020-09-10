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

package org.onap.oom.truststoremerger.configuration.path.env;

import java.util.Optional;
import org.onap.oom.truststoremerger.configuration.exception.MandatoryEnvMissingException;
import org.onap.oom.truststoremerger.configuration.model.EnvVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvProvider.class);

    public Optional<String> readEnv(EnvVariable envVariable) {
        String value =
            envVariable.isMandatory() ? readMandatorySystemEnv(envVariable) : readSystemEnv(envVariable);
        LOGGER.info("Read variable: {} , value: {}", envVariable.name(), value);
        return Optional.ofNullable(value);
    }

    String readMandatorySystemEnv(EnvVariable envVariable) throws MandatoryEnvMissingException {
        return Optional.ofNullable(readSystemEnv(envVariable))
            .orElseThrow(() -> new MandatoryEnvMissingException(envVariable +
                "environment variable does not provided"));
    }

    String readSystemEnv(EnvVariable envVariable) {
        return System.getenv(envVariable.name());
    }
}
