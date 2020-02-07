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

package org.onap.aaf.certservice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class CertificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationService.class);
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @GetMapping("/csr/{caName}")
    public ResponseEntity<String> getEncodesCSR(
            @PathVariable String caName,
            @RequestHeader("CSR") String encodedCSR,
            @RequestHeader("PK") String encodedPrivateKey
    ) {

        String csr = decode(encodedCSR);
        String privateKey = decode(encodedPrivateKey);

        LOGGER.info("Received CSR for CA named: {}",caName);
        LOGGER.debug("Decoded received CSR: \n{}", csr);

        return new ResponseEntity<>(csr, HttpStatus.OK);

    }

    private String decode(String encodedData) {
        return new String(DECODER.decode(encodedData));
    }

}
