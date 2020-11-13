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

package org.onap.oom.certservice.client.configuration.factory;

import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isDomainNameValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isEmailAddressValid;
import static org.onap.oom.certservice.client.configuration.validation.BasicValidationFunctions.isIpAddressValid;

import java.util.function.Function;
import org.bouncycastle.asn1.x509.GeneralName;
import org.onap.oom.certservice.client.configuration.exception.CsrConfigurationException;
import org.onap.oom.certservice.client.configuration.model.San;

public class SanMapper implements Function<String, San> {

    public San apply(String san) {
        if (isEmailAddressValid(san)) {
            return new San(san, GeneralName.rfc822Name);
        } else if (isIpAddressValid(san)) {
            return new San(san, GeneralName.iPAddress);
        } else if (isDomainNameValid(san)) {
            return new San(san, GeneralName.dNSName);
        } else {
            throw new CsrConfigurationException("San :" + san + " does not match any requirements");
        }
    }
}
