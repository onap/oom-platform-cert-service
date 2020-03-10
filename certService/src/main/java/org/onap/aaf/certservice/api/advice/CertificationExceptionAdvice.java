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

package org.onap.aaf.certservice.api.advice;

import org.onap.aaf.certservice.api.CertificationController;
import org.onap.aaf.certservice.certification.exception.Cmpv2ClientAdapterException;
import org.onap.aaf.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.aaf.certservice.certification.exception.CsrDecryptionException;
import org.onap.aaf.certservice.certification.exception.ErrorResponseModel;
import org.onap.aaf.certservice.certification.exception.KeyDecryptionException;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CertificationController.class)
public class CertificationExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationExceptionAdvice.class);

    @ExceptionHandler(value = CsrDecryptionException.class)
    public ResponseEntity<ErrorResponseModel> handle(CsrDecryptionException exception) {
        LOGGER.error("Exception occurred during decoding certificate sign request:", exception);
        return getErrorResponseEntity(
                "Wrong certificate signing request (CSR) format",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = KeyDecryptionException.class)
    public ResponseEntity<ErrorResponseModel> handle(KeyDecryptionException exception) {
        LOGGER.error("Exception occurred during decoding key:", exception);
        return getErrorResponseEntity(
                "Wrong key (PK) format",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = Cmpv2ServerNotFoundException.class)
    public ResponseEntity<ErrorResponseModel> handle(Cmpv2ServerNotFoundException exception) {
        LOGGER.error("Exception occurred selecting CMPv2 server:", exception);
        return getErrorResponseEntity(
                "Certification authority not found for given CAName",
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponseModel> handle(RuntimeException exception) throws CmpClientException {
        throw new CmpClientException("Runtime exception occurred calling cmp client business logic", exception);
    }

    @ExceptionHandler(value = CmpClientException.class)
    public ResponseEntity<ErrorResponseModel> handle(CmpClientException exception) {
        LOGGER.error("Exception occurred calling cmp client:", exception);
        return getErrorResponseEntity(
                "Exception occurred during call to cmp client",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(value = Cmpv2ClientAdapterException.class)
    public ResponseEntity<ErrorResponseModel> handle(Cmpv2ClientAdapterException exception) {
        LOGGER.error("Exception occurred parsing cmp client response:", exception);
        return getErrorResponseEntity(
                "Exception occurred parsing cmp client response",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ResponseEntity<ErrorResponseModel> getErrorResponseEntity(String errorMessage, HttpStatus status) {
        ErrorResponseModel errorResponse = new ErrorResponseModel(errorMessage);
        return new ResponseEntity<>(
               errorResponse,
                status
        );
    }

}
