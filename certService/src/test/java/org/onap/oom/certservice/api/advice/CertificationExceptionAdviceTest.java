/*
 * ============LICENSE_START=======================================================
 * PROJECT
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package org.onap.oom.certservice.api.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.exception.Cmpv2ClientAdapterException;
import org.onap.oom.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.oom.certservice.certification.exception.CsrDecryptionException;
import org.onap.oom.certservice.certification.exception.ErrorResponseModel;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CertificationExceptionAdviceTest {

    private CertificationExceptionAdvice certificationExceptionAdvice;

    @BeforeEach
    void setUp() {
        certificationExceptionAdvice =
                new CertificationExceptionAdvice();
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenCsrDecryptionException() {
        // Given
        String expectedMessage = "Wrong certificate signing request (CSR) format";
        CsrDecryptionException csrDecryptionException = new CsrDecryptionException("test csr exception");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(csrDecryptionException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getErrorMessage());
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenKeyDecryptionException() {
        // Given
        String expectedMessage = "Wrong key (PK) format";
        KeyDecryptionException csrDecryptionException = new KeyDecryptionException("test pk exception");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(csrDecryptionException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getErrorMessage());
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenGivenCaNameIsNotPresentInConfig() {
        // Given
        String expectedMessage = "Certification authority not found for given CAName";
        Cmpv2ServerNotFoundException csrDecryptionException = new Cmpv2ServerNotFoundException("test Ca exception");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(csrDecryptionException);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getErrorMessage());
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenCallingCmpClientFail() {
        // Given
        String expectedMessage = "Exception occurred during call to cmp client";
        CmpClientException cmpClientException = new CmpClientException("Calling CMPv2 client failed");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(cmpClientException);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().startsWith(expectedMessage));
    }

    @Test
    void shouldReturnResponseEntityWithAppropriateErrorMessageWhenModelTransformationInAdapterFail() {
        // Given
        String expectedMessage = "Exception occurred parsing cmp client response";
        Cmpv2ClientAdapterException cmpv2ClientAdapterException = new Cmpv2ClientAdapterException(new Throwable());

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(cmpv2ClientAdapterException);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().startsWith(expectedMessage));
    }

    @Test
    void shouldThrowCmpClientExceptionWhenNotHandledRunTimeExceptionOccur() {
        // Given
        String expectedMessage = "Runtime exception occurred calling cmp client business logic";
        RuntimeException runtimeException = new RuntimeException("Unknown runtime exception");

        // When
        Exception exception = assertThrows(
                CmpClientException.class, () ->
                        certificationExceptionAdvice.handle(runtimeException)
        );

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldReturnResponseEntityWithCmpErrorMessage() {
        // Given
        String expectedMessage = "CMPv2 server returned following error: EJBCA fault";
        CmpServerException exception = new CmpServerException("EJBCA fault");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().startsWith(expectedMessage));
    }

    @Test
    void shouldReturnResponseEntityWithCertificateDecryptionMessage() {
        // Given
        String expectedMessage = "Wrong certificate format";
        CertificateDecryptionException exception = new CertificateDecryptionException("Incorrect certificate, decryption failed");

        // When
        ResponseEntity<ErrorResponseModel> response = certificationExceptionAdvice.handle(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getErrorMessage());
    }

}
