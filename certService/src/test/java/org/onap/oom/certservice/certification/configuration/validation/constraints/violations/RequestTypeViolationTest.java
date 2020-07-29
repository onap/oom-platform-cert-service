/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.oom.certservice.certification.configuration.validation.constraints.violations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RequestTypeViolationTest {

    private final RequestTypeViolation violation = new RequestTypeViolation();

    @Test
    void givenValidRequestTypeShouldReturnTrue() {
        //given
        String validUrl = "http://127.0.0.1/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = violation.validate(validUrl);

        //then
        assertTrue(result);
    }

    @Test
    void givenInvalidRequestTypeShouldReturnFalse() {
        //given
        String invalidUrl = "htestps://127.0.0.1/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = violation.validate(invalidUrl);

        //then
        assertFalse(result);
    }
}
