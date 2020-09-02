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

package org.onap.oom.truststoremerger.configuration.path;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EnvProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvProvider.class);

    Optional<String> getEnv(String name) {
        String value = System.getenv(name);
        LOGGER.info("Read variable: {} , value: {}", name, value);
        return Optional.ofNullable(System.getenv(name));
    }
}
