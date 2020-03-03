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


package org.onap.aaf.certservice.certification.configuration.validation.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Cmpv2URLValidatorTest {

    private final Cmpv2URLValidator validator = new Cmpv2URLValidator();

    @Test
    public void givenCorrectURLWhenValidatingShouldReturnTrue() {
        //given
        String URL = "http://127.0.0.1/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = validator.isValid(URL, null);

        //then
        assertTrue(result);
    }

    @Test
    public void givenIncorrectURLWhenValidatingShouldReturnFalse() {
        //given
        String URL = "httttp://127.0.0.1:80000/ejbca/publicweb/cmp/cmp";

        //when
        boolean result = validator.isValid(URL, null);

        //then
        assertFalse(result);
    }
}