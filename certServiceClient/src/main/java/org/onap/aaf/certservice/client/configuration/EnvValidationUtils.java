/*
 * ============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.configuration;

import java.util.regex.Pattern;

public final class EnvValidationUtils {

    private EnvValidationUtils() {}

    public static Boolean isPathValid(String path) {
        return path.matches("^/|(/[a-zA-Z0-9_-]+)+/?$");
    }

    public static Boolean isAlphaNumeric(String caName) {
        return caName.matches("^[a-zA-Z0-9]*$");
    }

    public static Boolean isCountryValid(String country) {
        return country.matches("^([A-Z][A-Z])$");
    }

    public static Boolean isCommonNameValid(String commonName) {
        return !isSpecialCharsPresent(commonName) &&
                !isHttpProtocolsPresent(commonName) &&
                !isIpAddressPresent(commonName) &&
                !isPortNumberPresent(commonName);
    }

    static Boolean isPortNumberPresent(String stringToCheck) {
        return Pattern.compile(":[0-9]{1,5}").matcher(stringToCheck).find();
    }

    static Boolean isIpAddressPresent(String stringToCheck) {
        return Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").matcher(stringToCheck).find();
    }

    static Boolean isHttpProtocolsPresent(String stringToCheck) {
        return Pattern.compile("[h][t][t][p][:][/][/]|[h][t][t][p][s][:][/][/]").matcher(stringToCheck).find();
    }

    public static Boolean isSpecialCharsPresent(String stringToCheck) {
        return Pattern.compile("[~#@*$+%!()?/{}<>\\|_^]").matcher(stringToCheck).find();
    }
}
