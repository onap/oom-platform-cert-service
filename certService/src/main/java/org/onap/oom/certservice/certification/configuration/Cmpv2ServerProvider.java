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

package org.onap.oom.certservice.certification.configuration;

import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Cmpv2ServerProvider {

    private final CmpServersConfig cmpServersConfig;

    @Autowired
    Cmpv2ServerProvider(CmpServersConfig cmpServersConfig) {
        this.cmpServersConfig = cmpServersConfig;
    }

    public Cmpv2Server getCmpv2Server(String caName) {
        return cmpServersConfig.getCmpServers().stream().filter(server -> server.getCaName().equals(caName)).findFirst()
                       .orElseThrow(() -> new Cmpv2ServerNotFoundException("No server found for given CA name"));
    }

}
