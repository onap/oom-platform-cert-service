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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "CertificationService")
public final class ReadinessController {

    private final CmpServersConfig cmpServersConfig;

    @Autowired
    public ReadinessController(CmpServersConfig cmpServersConfig) {
        this.cmpServersConfig = cmpServersConfig;
    }

    @GetMapping(value = "/ready", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration is loaded and service is ready to use"),
            @ApiResponse(responseCode = "503", description = "Configuration loading failed and service is unavailable")
    })
    @Operation(
            summary = "Check if CertService application is ready",
            description = "Web endpoint for checking if service is ready to be used.",
            tags = {"CertificationService"})
    public ResponseEntity<String> checkReady() {
        if (cmpServersConfig.isReady()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
