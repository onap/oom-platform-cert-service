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

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.aaf.certservice.certification.exception.CsrDecryptionException;
import org.onap.aaf.certservice.certification.exception.ErrorResponseModel;
import org.onap.aaf.certservice.certification.exception.KeyDecryptionException;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CertificationExceptionControllerTest {

    private CertificationExceptionController certificationExceptionController;

    @BeforeEach
    void setUp() {
        certificationExceptionController =
                new CertificationExceptionController();
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenCsrDecryptionException() {
        // given
        String expectedMessage = "Wrong certificate signing request (CSR) format";
        CsrDecryptionException csrDecryptionException = new CsrDecryptionException("test csr exception");

        // when
        ResponseEntity<String> responseEntity = certificationExceptionController.handle(csrDecryptionException);

        ErrorResponseModel response = new Gson().fromJson(responseEntity.getBody(), ErrorResponseModel.class);

        // then
        assertEquals(expectedMessage, response.getErrorMessage());
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenKeyDecryptionException() {
        // given
        String expectedMessage = "Wrong key (PK) format";
        KeyDecryptionException csrDecryptionException = new KeyDecryptionException("test pk exception");

        // when
        ResponseEntity<String> responseEntity = certificationExceptionController.handle(csrDecryptionException);

        ErrorResponseModel response = new Gson().fromJson(responseEntity.getBody(), ErrorResponseModel.class);

        // then
        assertEquals(expectedMessage, response.getErrorMessage());
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenCaNameIsNotPresentInConfig() {
        // given
        String expectedMessage = "Certification authority not found for given CAName";
        Cmpv2ServerNotFoundException csrDecryptionException = new Cmpv2ServerNotFoundException("test Ca exception");

        // when
        ResponseEntity<String> responseEntity = certificationExceptionController.handle(csrDecryptionException);

        ErrorResponseModel response = new Gson().fromJson(responseEntity.getBody(), ErrorResponseModel.class);

        // then
        assertEquals(expectedMessage, response.getErrorMessage());
    }
}
