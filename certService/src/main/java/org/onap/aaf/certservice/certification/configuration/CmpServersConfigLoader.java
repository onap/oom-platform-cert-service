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

package org.onap.aaf.certservice.certification.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.aaf.certservice.certification.configuration.model.CmpServers;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
class CmpServersConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmpServersConfigLoader.class);

    List<Cmpv2Server> load(String path) {
        List<Cmpv2Server> result = new ArrayList<>();
        try {
            result = loadConfigFromFile(path).getCmpv2Servers();
            LOGGER.info(String.format("CMP Servers configuration successfully loaded from file '%s'", path));
        } catch (IOException e) {
            LOGGER.error("Exception occurred during CMP Servers configuration loading: ", e);
        }
        return result;
    }

    private CmpServers loadConfigFromFile(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(path), CmpServers.class);
    }
}
