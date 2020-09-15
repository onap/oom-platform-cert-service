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

package org.onap.oom.certservice.postprocessor.configuration.path.validation;

import java.util.List;
import java.util.function.Predicate;

public final class ValidationFunctions {

    private static final String CERTIFICATE_PATH_REGEX = "^(/[a-zA-Z0-9_-]+)+\\.(pem|jks|p12)";
    private static final String CERTIFICATE_PASSWORD_PATH_REGEX = "^(/[a-zA-Z0-9_-]+)+\\.pass";

    private ValidationFunctions() {
    }

    public static Predicate<List<String>> doesItContainValidPasswordPaths() {
        return paths -> paths.stream().allMatch(ValidationFunctions::isCertificatePasswordPathValid);
    }

    public static Predicate<List<String>> doesItContainValidCertificatesPaths() {
        return paths -> paths.stream().allMatch(ValidationFunctions::isCertificatePathValid);
    }

    public static Predicate<List<String>> doesItContainValidPathsToCopy() {
        return paths -> paths.stream().allMatch(path ->
            doesMatch(path, CERTIFICATE_PASSWORD_PATH_REGEX) || isCertificatePathValid(path));
    }

    private static boolean isCertificatePathValid(String path) {
        return doesMatch(path, CERTIFICATE_PATH_REGEX);
    }

    private static boolean isCertificatePasswordPathValid(String path) {
        return path.isEmpty() || doesMatch(path, CERTIFICATE_PASSWORD_PATH_REGEX);
    }

    private static boolean doesMatch(String path, String regex) {
        return path.matches(regex);
    }
}
