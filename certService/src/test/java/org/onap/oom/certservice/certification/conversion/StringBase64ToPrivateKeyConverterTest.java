/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nokia.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.certservice.certification.conversion;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;

import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.oom.certservice.certification.TestData.TEST_PEM;
import static org.onap.oom.certservice.certification.TestData.TEST_PK;

class StringBase64ToPrivateKeyConverterTest {

    private static final String RSA = "RSA";
    public static final String PKCS_8 = "PKCS#8";

    @Test
    void shouldUseProperAlgorithmWhenConverting() throws KeyDecryptionException {
        // Given
        StringBase64ToPrivateKeyConverter stringBase64ToPrivateKeyConverter = new StringBase64ToPrivateKeyConverter();
        String encodedPK = new String(Base64.encode(TEST_PK.getBytes()));
        // When
        PrivateKey privateKey = stringBase64ToPrivateKeyConverter.convert(new StringBase64(encodedPK));
        // Then
        assertEquals(RSA, privateKey.getAlgorithm());
    }

    @Test
    void shouldUsePkcs8FormatWhenConverting() throws KeyDecryptionException {
        // Given
        StringBase64ToPrivateKeyConverter stringBase64ToPrivateKeyConverter = new StringBase64ToPrivateKeyConverter();
        String encodedPK = new String(Base64.encode(TEST_PK.getBytes()));
        // When
        PrivateKey privateKey = stringBase64ToPrivateKeyConverter.convert(new StringBase64(encodedPK));
        // Then
        assertEquals(PKCS_8, privateKey.getFormat());
    }

    @Test
    void shouldCorrectlyConvertWhenPrivateKeyPemIsProper() throws KeyDecryptionException {
        // Given
        StringBase64ToPrivateKeyConverter stringBase64ToPrivateKeyConverter = new StringBase64ToPrivateKeyConverter();
        String encodedPK = new String(Base64.encode(TEST_PK.getBytes()));
        // When
        PrivateKey privateKey = stringBase64ToPrivateKeyConverter.convert(new StringBase64(encodedPK));
        // Then
        assertNotNull(privateKey.getEncoded());
    }

    @Test
    void shouldThrowExceptionWhenPrivateKeyPemIsNotProperPrivateKey() {
        // Given
        StringBase64ToPrivateKeyConverter stringBase64ToPrivateKeyConverter = new StringBase64ToPrivateKeyConverter();
        StringBase64 privateKey = new StringBase64(TEST_PEM);
        // When
        Exception exception = assertThrows(
                KeyDecryptionException.class, () -> stringBase64ToPrivateKeyConverter.convert(privateKey));

        String expectedMessage = "Incorrect Key, decryption failed";
        String actualMessage = exception.getMessage();
        // Then
        assertTrue(actualMessage.contains(expectedMessage));
    }

}
