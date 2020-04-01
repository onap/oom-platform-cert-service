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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.onap.aaf.certservice.certification.CertificationModelFactory;
import org.onap.aaf.certservice.certification.exception.DecryptionException;
import org.onap.aaf.certservice.certification.exception.ErrorResponseModel;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
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
@Tag(name = "CertificationService")
public class CertificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationController.class);

    private final CertificationModelFactory certificationModelFactory;

    @Autowired
    CertificationController(CertificationModelFactory certificationModelFactory) {
        this.certificationModelFactory = certificationModelFactory;
    }

    /**
     * Request for signing certificate by given CA.
     *
     * @param caName            the name of Certification Authority that will sign root certificate
     * @param encodedCsr        Certificate Sign Request encoded in Base64 form
     * @param encodedPrivateKey Private key for CSR, needed for PoP, encoded in Base64 form
     * @return JSON containing trusted certificates and certificate chain
     */
    @GetMapping(value = "v1/certificate/{caName}", produces = "application/json; charset=utf-8")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "certificate successfully signed"),
            @ApiResponse(responseCode = "400", description = "given CSR or/and PK is incorrect",
                    content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "CA not found for given name",
                    content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "something went wrong during connecting to cmp client",
                    content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))
    })
    @Operation(
            summary = "sign certificate",
            description = "Web endpoint for requesting certificate signing. Used by system components to gain certificate signed by CA.",
            tags = {"CertificationService"})
    public ResponseEntity<CertificationModel> signCertificate(
            @Parameter(description = "Name of certification authority that will sign CSR.")
            @PathVariable String caName,
            @Parameter(description = "Certificate signing request in form of PEM object encoded in Base64 (with header and footer).")
            @RequestHeader("CSR") String encodedCsr,
            @Parameter(description = "Private key in form of PEM object encoded in Base64 (with header and footer).")
            @RequestHeader("PK") String encodedPrivateKey
    ) throws DecryptionException, CmpClientException {
        caName = caName.replaceAll("[\n|\r|\t]", "_");
        LOGGER.info("Received certificate signing request for CA named: {}", caName);
        CertificationModel certificationModel = certificationModelFactory
                .createCertificationModel(encodedCsr, encodedPrivateKey, caName);
        return new ResponseEntity<>(certificationModel, HttpStatus.OK);
    }

}
