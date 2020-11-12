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

import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isHttpProtocolsPresent;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isIpAddressPresent;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isPortNumberPresent;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isSpecialCharPresent;

import java.util.Arrays;
import java.util.function.Predicate;
import org.onap.oom.certservice.client.certification.ArtifactsCreatorProvider;

public class ValidatorsFactory {

    public Predicate<String> commonNameValidator() {
        return commonName ->
            !isSpecialCharPresent(commonName)
                && !isHttpProtocolsPresent(commonName)
                && !isIpAddressPresent(commonName)
                && !isPortNumberPresent(commonName);
    }

    public Predicate<String> outputTypeValidator() {
        return outputType -> Arrays.stream(ArtifactsCreatorProvider.values())
            .map(ArtifactsCreatorProvider::toString)
            .anyMatch(name -> name.equals(outputType));
    }

}
