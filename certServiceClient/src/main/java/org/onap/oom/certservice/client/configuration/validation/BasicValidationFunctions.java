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

package org.onap.oom.certservice.client.configuration.validation;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

public class BasicValidationFunctions {

    public static boolean isPathValid(String path) {
        return path.matches("^/|(/[a-zA-Z0-9_-]+)+/?$");
    }

    public static boolean isAlphaNumeric(String caName) {
        return caName.matches("^[a-zA-Z0-9]*$");
    }

    public static boolean isSpecialCharPresent(String stringToCheck) {
        return Pattern.compile("[~#@*$+%!()?/{}<>\\|_^]").matcher(stringToCheck).find();
    }

    public static boolean isPortNumberPresent(String stringToCheck) {
        return Pattern.compile(":[0-9]{1,5}").matcher(stringToCheck).find();
    }

    public static boolean isIpAddressPresent(String stringToCheck) {
        return Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").matcher(stringToCheck).find();
    }

    public static boolean isHttpProtocolsPresent(String stringToCheck) {
        return Pattern.compile("[h][t][t][p][:][/][/]|[h][t][t][p][s][:][/][/]").matcher(stringToCheck).find();
    }

    public static boolean isCountryValid(String country) {
        return Arrays.asList(Locale.getISOCountries()).contains(country);
    }

    public static boolean isEmailAddressValid(String address) {
        return EmailValidator.getInstance().isValid(address);
    }

    public static boolean isIpAddressValid(String address) {
        return InetAddressValidator.getInstance().isValid(address);
    }

    public static boolean isDomainNameValid(String domain) {
        return DomainValidator.getInstance(true).isValid(domain);
    }

}
