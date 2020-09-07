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

public final class ConfigurationEnvs {

    private ConfigurationEnvs() {

    }

    public static final String TRUSTSTORES_PATHS_ENV = "TRUSTSTORES_PATHS";
    public static final String TRUSTSTORES_PASSWORDS_PATHS_ENV = "TRUSTSTORES_PASSWORDS_PATHS";
    public static final String KEYSTORE_SOURCE_PATHS_ENV = "KEYSTORE_SOURCE_PATHS";
    public static final String KEYSTORE_DESTINATION_PATHS_ENV = "KEYSTORE_DESTINATION_PATHS";

}
