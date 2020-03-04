/*============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.certification.conversion;

import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;

class RandomPasswordGenerator {

    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "_$#";
    private static final char[] SET_OF_CHARS = (ALPHA + ALPHA.toUpperCase() + NUMBERS + SPECIAL_CHARS).toCharArray();
    private static final char START_POSITION_IN_ASCII_CHARS = 0;
    private static final char END_POSITION_IN_ASCII_CHARS = 0;
    private static final boolean USE_LETTERS_ONLY = false;
    private static final boolean USE_NUMBERS_ONLY = false;

    Password generate(int passwordLength) {
        return new Password(RandomStringUtils.random(
            passwordLength,
            START_POSITION_IN_ASCII_CHARS,
            END_POSITION_IN_ASCII_CHARS,
            USE_LETTERS_ONLY,
            USE_NUMBERS_ONLY,
            SET_OF_CHARS,
            new SecureRandom()));
    }
}

