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

package org.onap.oom.certservice.client.configuration.validation.csr;

import static org.onap.oom.certservice.client.configuration.validation.csr.CsrEnvsValueValidators.isSpecialCharPresent;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CommonNameValidator implements Predicate<String> {

    private static final String PORT_POSTFIX_REGEX = ":[0-9]{1,5}";
    private static final String IPV4_ADDRESS_REGEX = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
    private static final String HTTP_HTTPS_SCHEME_REGEX = "[h][t][t][p][:][/][/]|[h][t][t][p][s][:][/][/]";

    public boolean test(String commonName) {
        return !isSpecialCharPresent(commonName)
            && !isHttpProtocolsPresent(commonName)
            && !isIpAddressPresent(commonName)
            && !isPortNumberPresent(commonName);
    }

    private boolean isPortNumberPresent(String stringToCheck) {
        return Pattern.compile(PORT_POSTFIX_REGEX).matcher(stringToCheck).find();
    }

    private boolean isIpAddressPresent(String stringToCheck) {
        return Pattern.compile(IPV4_ADDRESS_REGEX).matcher(stringToCheck).find();
    }

    private boolean isHttpProtocolsPresent(String stringToCheck) {
        return Pattern.compile(HTTP_HTTPS_SCHEME_REGEX).matcher(stringToCheck).find();
    }

}
