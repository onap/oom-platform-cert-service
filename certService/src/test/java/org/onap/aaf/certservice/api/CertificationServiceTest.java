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

package org.onap.aaf.certservice.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aaf.certservice.certification.CsrModelFactory;
import org.onap.aaf.certservice.certification.CsrModelFactory.StringBase64;
import org.onap.aaf.certservice.certification.exceptions.CsrDecryptionException;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CertificationServiceTest {

    private CertificationService certificationService;

    @Mock
    private CsrModelFactory csrModelFactory;

    @BeforeEach
    void serUp() {
        MockitoAnnotations.initMocks(this);
        certificationService = new CertificationService(csrModelFactory);
    }

    @Test
    void shouldReturnDataAboutCsrBaseOnEncodedParameters() throws CsrDecryptionException {
        // given
        final String testStringCsr = "testData";
        CsrModel mockedCsrModel = mock(CsrModel.class);
        when(mockedCsrModel.toString()).thenReturn(testStringCsr);
        when(csrModelFactory.createCsrModel(any(StringBase64.class), any(StringBase64.class)))
                .thenReturn(mockedCsrModel);

        // when
        ResponseEntity<String> testResponse =
                certificationService.signCertificate("TestCa", "encryptedCSR", "encryptedPK");

        // then
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        assertTrue(
                testResponse.toString().contains(testStringCsr)
        );
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCsrModelFails() throws CsrDecryptionException {
        // given
        when(csrModelFactory.createCsrModel(any(StringBase64.class), any(StringBase64.class)))
                .thenThrow(new CsrDecryptionException("creation fail",new IOException()));

        // when
        ResponseEntity<String> testResponse =
                certificationService.signCertificate("TestCa", "encryptedCSR", "encryptedPK");

        String expectedMessage = "Wrong certificate signing request (CSR) format";

        // then
        assertEquals(HttpStatus.BAD_REQUEST, testResponse.getStatusCode());
        assertTrue(
                testResponse.toString().contains(expectedMessage)
        );

    }

}
