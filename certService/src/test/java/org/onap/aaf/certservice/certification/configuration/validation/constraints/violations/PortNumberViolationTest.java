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

package org.onap.aaf.certservice.certification.configuration.validation.constraints.violations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortNumberViolationTest {

    private final PortNumberViolation violation = new PortNumberViolation();

    @Test
    void givenValidPortShouldReturnTrue() {
        //given
        String validUrl1 = "http://127.0.0.1:8080/ejbca/publicweb/cmp/cmp";
        String validUrl2 = "http://127.0.0.1:1/ejbca/publicweb/cmp/cmp";
        String validUrl3 = "http://127.0.0.1:65535/ejbca/publicweb/cmp/cmp";

        //when
        boolean result1 = violation.validate(validUrl1);
        boolean result2 = violation.validate(validUrl2);
        boolean result3 = violation.validate(validUrl3);

        //then
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
    }

    @Test
    void givenEmptyPortShouldReturnTrue() {
        //given
        String validUrl = "http://127.0.0.1/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = violation.validate(validUrl);

        //then
        assertTrue(result);
    }

    @Test
    void givenInvalidPortShouldReturnFalse() {
        //given
        String invalidUrl1 = "http://127.0.0.1:0/ejbca/publicweb/cmp/cmp";
        String invalidUrl2 = "http://127.0.0.1:65536/ejbca/publicweb/cmp/cmp";

        //when
        boolean result1 = violation.validate(invalidUrl1);
        boolean result2 = violation.validate(invalidUrl2);

        //then
        assertFalse(result1);
        assertFalse(result2);
    }
}
