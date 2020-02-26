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

import com.google.gson.Gson;
import org.onap.aaf.certservice.certification.CertificationModelFactory;
import org.onap.aaf.certservice.certification.CsrModelFactory;
import org.onap.aaf.certservice.certification.CsrModelFactory.StringBase64;
import org.onap.aaf.certservice.certification.exception.DecryptionException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CertificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationService.class);

    private final CsrModelFactory csrModelFactory;
    private final CertificationModelFactory certificationModelFactory;

    @Autowired
    CertificationService(CsrModelFactory csrModelFactory, CertificationModelFactory certificationModelFactory) {
        this.csrModelFactory = csrModelFactory;
        this.certificationModelFactory = certificationModelFactory;
    }

    /**
     * Request for signing certificate by given CA.
     *
     *
     * @param caName the name of Certification Authority that will sign root certificate
     * @param encodedCsr Certificate Sign Request encoded in Base64 form
     * @param encodedPrivateKey Private key for CSR, needed for PoP, encoded in Base64 form
     * @return JSON containing trusted certificates and certificate chain
     */
    @GetMapping(value = "v1/certificate/{caName}", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> signCertificate(
            @PathVariable String caName,
            @RequestHeader("CSR") String encodedCsr,
            @RequestHeader("PK") String encodedPrivateKey
    ) throws DecryptionException {

        caName = caName.replaceAll("[\n|\r|\t]", "_");
        LOGGER.info("Received certificate signing request for CA named: {}", caName);
        CsrModel csrModel = csrModelFactory.createCsrModel(
                new StringBase64(encodedCsr),
                new StringBase64(encodedPrivateKey)
        );
        LOGGER.debug("Received CSR meta data: \n{}", csrModel);
        CertificationModel certificationModel = certificationModelFactory
                .createCertificationModel(csrModel, caName);
        return new ResponseEntity<>(new Gson().toJson(certificationModel), HttpStatus.OK);

    }


}
