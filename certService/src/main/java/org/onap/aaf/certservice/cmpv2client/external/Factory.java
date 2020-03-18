/*
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
 * ===========================================================================
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
 * ============LICENSE_END====================================================
 *
 */

package org.onap.aaf.certservice.cmpv2client.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class Factory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
    private static final KeyPairGenerator KEY_PAIR_GENERATOR;
    private static final SecureRandom SECURE_RANDOM;
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_LENGTH = 2048;

    static {
        SECURE_RANDOM = new SecureRandom();
        KeyPairGenerator tempKeygen;
        try {
            tempKeygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            tempKeygen.initialize(KEY_LENGTH, SECURE_RANDOM);
        } catch (NoSuchAlgorithmException e) {
            tempKeygen = null;
            LOGGER.error("Given KEY_ALGORITHM is invalid.", e);
        }
        KEY_PAIR_GENERATOR = tempKeygen;
    }

    private Factory() {
    }

    public static KeyPair generateKeyPair() {
        return KEY_PAIR_GENERATOR.generateKeyPair();
    }
}
