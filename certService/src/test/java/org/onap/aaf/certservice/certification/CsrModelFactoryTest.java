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

package org.onap.aaf.certservice.certification;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.certification.CsrModelFactory.StringBase64;
import org.onap.aaf.certservice.certification.exceptions.CsrDecryptionException;
import org.onap.aaf.certservice.certification.exceptions.DecryptionException;
import org.onap.aaf.certservice.certification.exceptions.KeyDecryptionException;
import org.onap.aaf.certservice.certification.model.CsrModel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.aaf.certservice.certification.TestData.TEST_CSR;
import static org.onap.aaf.certservice.certification.TestData.TEST_PK;
import static org.onap.aaf.certservice.certification.TestData.TEST_WRONG_CSR;
import static org.onap.aaf.certservice.certification.TestData.TEST_WRONG_PEM;


class CsrModelFactoryTest {

    private CsrModelFactory csrModelFactory;

    @BeforeEach
    void setUp() {
        csrModelFactory = new CsrModelFactory();
    }

    @Test
    void shouldDecryptCsrAndReturnStringWithDataAboutIt() throws DecryptionException {
        // given
        String encoderCsr = new String(Base64.encode(TEST_CSR.getBytes()));
        String encoderPK = new String(Base64.encode(TEST_PK.getBytes()));

        // when
        CsrModel decryptedCsr = csrModelFactory
                .createCsrModel(new StringBase64(encoderCsr), new StringBase64(encoderPK));

        // then
        assertTrue(
                decryptedCsr.toString()
                        .contains(
                                "C=US,ST=California,L=San-Francisco,O=Linux-Foundation,"
                                        + "OU=ONAP,CN=onap.org,E=tester@onap.org")
                        &&
                        decryptedCsr.toString()
                                .contains("SANs: [gerrit.onap.org, test.onap.org, onap.com]")
        );
    }

    @Test
    void shouldThrowCsrDecryptionExceptionWhenCsrIsIncorrect() {
        // given
        String encoderPK = new String(Base64.encode(TEST_PK.getBytes()));
        String wrongCsr = new String(Base64.encode(TEST_WRONG_CSR.getBytes()));

        // when
        Exception exception = assertThrows(
                CsrDecryptionException.class, () -> csrModelFactory
                        .createCsrModel(new StringBase64(wrongCsr), new StringBase64(encoderPK))
        );

        String expectedMessage = "Incorrect CSR, decryption failed";
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowKeyDecryptionExceptionWhenKeyIsIncorrect() {
        // given
        String encoderPK = new String(Base64.encode(TEST_WRONG_PEM.getBytes()));
        String wrongCsr = new String(Base64.encode(TEST_CSR.getBytes()));

        // when
        Exception exception = assertThrows(
                KeyDecryptionException.class, () -> csrModelFactory
                        .createCsrModel(new StringBase64(wrongCsr), new StringBase64(encoderPK))
        );

        String expectedMessage = "Incorrect Key, decryption failed";
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void shouldThrowCsrDecryptionExceptionWhenCsrIsNotInBase64Encoding() {
        // given
        String encoderPK = new String(Base64.encode(TEST_PK.getBytes()));
        String wrongCsr = "Not Base 64 Csr";

        // when
        Exception exception = assertThrows(
                CsrDecryptionException.class, () -> csrModelFactory
                        .createCsrModel(new StringBase64(wrongCsr), new StringBase64(encoderPK))
        );

        String expectedMessage = "Incorrect CSR, decryption failed";
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowKeyDecryptionExceptionWhenPKIsNotInBase64Encoding() {
        // given
        String encoderPK = "Not Base64 Key";
        String wrongCsr = new String(Base64.encode(TEST_CSR.getBytes()));

        // when
        Exception exception = assertThrows(
                KeyDecryptionException.class, () -> csrModelFactory
                        .createCsrModel(new StringBase64(wrongCsr), new StringBase64(encoderPK))
        );

        String expectedMessage = "Incorrect Key, decryption failed";
        String actualMessage = exception.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
