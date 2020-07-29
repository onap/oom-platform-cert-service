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

package org.onap.oom.certservice.api.advice;

import org.onap.oom.certservice.api.ReloadConfigController;
import org.onap.oom.certservice.certification.configuration.CmpServersConfigLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ReloadConfigController.class)
public final class ReloadConfigExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadConfigExceptionAdvice.class);

    @ExceptionHandler(value = CmpServersConfigLoadingException.class)
    public ResponseEntity<String> handle(CmpServersConfigLoadingException exception) {
        LOGGER.error(exception.getMessage(), exception.getCause());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
