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

package org.onap.oom.certservice.postprocessor.configuration.model;

import java.util.List;
import java.util.function.Predicate;
import org.onap.oom.certservice.postprocessor.configuration.path.validation.ValidationFunctions;

public enum EnvVariable {
    TRUSTSTORES_PATHS_ENV(true, ValidationFunctions.doesItContainValidCertificatesPaths()),
    TRUSTSTORES_PASSWORDS_PATHS_ENV(true, ValidationFunctions.doesItContainValidPasswordPaths()),
    KEYSTORE_SOURCE_PATHS_ENV(false, ValidationFunctions.doesItContainValidPathsToCopy()),
    KEYSTORE_DESTINATION_PATHS_ENV(false, ValidationFunctions.doesItContainValidPathsToCopy());

    boolean isMandatory;

    Predicate<List<String>> validationFunction;

    EnvVariable(boolean isMandatory, Predicate<List<String>> validationFunction) {
        this.isMandatory = isMandatory;
        this.validationFunction = validationFunction;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public Predicate<List<String>> getValidationFunction() {
        return validationFunction;
    }
}
