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

package org.onap.oom.certservice.certification.conversion;

import org.bouncycastle.util.io.pem.PemObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.conversion.PemObjectFactory;
import org.onap.oom.certservice.certification.exception.DecryptionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.oom.certservice.certification.TestData.TEST_PEM;
import static org.onap.oom.certservice.certification.TestData.TEST_WRONG_PEM;
import static org.onap.oom.certservice.certification.TestUtils.pemObjectToString;


class PemObjectFactoryTest {


    private PemObjectFactory pemObjectFactory;

    @BeforeEach
    void setUp() {
        pemObjectFactory = new PemObjectFactory();
    }

    @Test
    void shouldTransformStringInToPemObjectAndBackToString() throws DecryptionException {
        // when
        PemObject pemObject = pemObjectFactory.createPemObject(TEST_PEM).orElseThrow(
                () -> new DecryptionException("Pem decryption failed")
        );
        String parsedPemObject = pemObjectToString(pemObject);

        // then
        assertEquals(TEST_PEM, parsedPemObject);
    }

    @Test
    void shouldThrowExceptionWhenParsingPemFailed() {
        // given
        String expectedMessage = "Unable to create PEM";

        // when
        Exception exception = assertThrows(
                DecryptionException.class, () -> pemObjectFactory.createPemObject(TEST_WRONG_PEM).orElseThrow(
                        () -> new DecryptionException(expectedMessage)
                )
        );

        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

}
