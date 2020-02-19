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

import static org.junit.jupiter.api.Assertions.*;

class PortNumberViolationTest {

    private PortNumberViolation violation = new PortNumberViolation();

    @Test
    public void givenValidPortShouldReturnTrue() {
        //given
        String validURL1 = "http://127.0.0.1:8080/ejbca/publicweb/cmp/cmp";
        String validURL2 = "http://127.0.0.1:1/ejbca/publicweb/cmp/cmp";
        String validURL3 = "http://127.0.0.1:65535/ejbca/publicweb/cmp/cmp";

        //when
        boolean result1 = violation.validate(validURL1);
        boolean result2 = violation.validate(validURL2);
        boolean result3 = violation.validate(validURL3);

        //then
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
    }

    @Test
    public void givenEmptyPortShouldReturnTrue() {
        //given
        String validURL = "http://127.0.0.1/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = violation.validate(validURL);

        //then
        assertTrue(result);
    }

    @Test
    public void givenInvalidPortShouldReturnFalse() {
        //given
        String invalidURL1 = "http://127.0.0.1:0/ejbca/publicweb/cmp/cmp";
        String invalidURL2 = "http://127.0.0.1:65536/ejbca/publicweb/cmp/cmp";

        //when
        boolean result1 = violation.validate(invalidURL1);
        boolean result2 = violation.validate(invalidURL2);

        //then
        assertFalse(result1);
        assertFalse(result2);
    }
}