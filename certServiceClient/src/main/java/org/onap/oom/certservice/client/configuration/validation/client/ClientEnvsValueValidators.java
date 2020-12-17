/*
 * ============LICENSE_START=======================================================
 * oom-certservice-client
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
package org.onap.oom.certservice.client.configuration.validation.client;

public final class ClientEnvsValueValidators {
    private static final String CA_NAME_REGEX = "^[a-zA-Z0-9_.~-]{1,128}$";
    private static final String VALID_PATH_REGEX = "^/|(/[a-zA-Z0-9_-]+)+/?$";

    public static boolean isCaNameValid(String caName) {
        return caName.matches(CA_NAME_REGEX);
    }

    public static boolean isPathValid(String path) {
        return path.matches(VALID_PATH_REGEX);
    }
}
