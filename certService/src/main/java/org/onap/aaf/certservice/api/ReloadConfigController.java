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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfig;
import org.onap.aaf.certservice.certification.configuration.CmpServersConfigLoadingException;
import org.onap.aaf.certservice.certification.exception.ErrorResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "CertificationService")
public class ReloadConfigController {

    private final CmpServersConfig cmpServersConfig;

    @Autowired
    public ReloadConfigController(CmpServersConfig cmpServersConfig) {
        this.cmpServersConfig = cmpServersConfig;
    }

    @GetMapping(value = "/reload", produces = "application/json; charset=utf-8")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "configuration has been successfully reloaded"),
            @ApiResponse(responseCode = "500", description = "something went wrong during configuration loading",
                    content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))
    })
    @Operation(
            summary = "reload service configuration from file",
            description = "Web endpoint for performing configuration reload. Used to reload configuration file from file.",
            tags = { "CertificationService" })
    public ResponseEntity<String> reloadConfiguration() throws CmpServersConfigLoadingException {
        cmpServersConfig.reloadConfiguration();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
