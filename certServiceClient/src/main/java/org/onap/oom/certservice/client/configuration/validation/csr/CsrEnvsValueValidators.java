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

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

public final class CsrEnvsValueValidators {

    private static final boolean ALLOW_LOCAL_DOMAINS = true;

    private static final String SCHEME = "([A-Za-z][A-Za-z0-9+\\-.]*):";

    private static final String OR = "|";

    private static final String AUTHORITY_WITH_PATH = "?:(//)(?:((?:[A-Za-z0-9\\-._~!$&'()*+,;=:]|%[0-9A-Fa-f]{2})*)"
        + "@)?((?:\\[(?:(?:(?:(?:[0-9A-Fa-f]{1,4}:){6}|::(?:[0-9A-Fa-f]{1,4}:){5}|(?:[0-9A-Fa-f]{1,4})?::"
        + "(?:[0-9A-Fa-f]{1,4}:){4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,1}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){3}|(?:"
        + "(?:[0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){2}|(?:(?:[0-9A-Fa-f]{1,4}:){0,"
        + "3}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,4}:|(?:(?:[0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})?::)"
        + "(?:[0-9A-Fa-f]{1,4}:[0-9A-Fa-f]{1,4}|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
        + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))|(?:(?:[0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,"
        + "4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})?::)|[Vv][0-9A-Fa-f]+\\.[A-Za-z0-9\\-._~!$&'()*+,;=:]+)"
        + "\\]|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|"
        + "(?:[A-Za-z0-9\\-._~!$&'()*+,;=]|%[0-9A-Fa-f]{2})*))(?::([0-9]*))?((?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;"
        + "=:@]|%[0-9A-Fa-f]{2})*)*)";

    private static final String PATH_BEGIN_WITH_SLASH = "/((?:(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/"
        + "(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)?)";

    private static final String PATH_WITHOUT_SLASH = "((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/"
        + "(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)";

    private static final String QUERY = "(?:\\?((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?";

    private static final String FRAGMENT = "(?:\\#((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?";

    /**
     * Compliant with the RFC3986
     * <p>
     * URI = scheme ":" hier-part [ "?" query ] [ "#" fragment ]
     * <p>
     * hier-part  = "//" authority path-abempty / path-absolute / path-rootless / path-empty
     */
    private static final String RFC3986_URI_MATCH_PATTERN =
        SCHEME + "(" + AUTHORITY_WITH_PATH + OR + PATH_BEGIN_WITH_SLASH + OR + PATH_WITHOUT_SLASH + OR + "" + ")"
            + QUERY + FRAGMENT;

    private static final String SPECIAL_CHAR_PRESENCE_REGEX = "[~#@*$+%!()?/{}<>\\|_^]";

    private CsrEnvsValueValidators() {
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
        return DomainValidator.getInstance(ALLOW_LOCAL_DOMAINS).isValid(domain);
    }

    public static boolean isUriValid(String uri) {
        return uri.matches(RFC3986_URI_MATCH_PATTERN);
    }

    public static boolean isSpecialCharPresent(String stringToCheck) {
        return Pattern.compile(SPECIAL_CHAR_PRESENCE_REGEX).matcher(stringToCheck).find();
    }
}
