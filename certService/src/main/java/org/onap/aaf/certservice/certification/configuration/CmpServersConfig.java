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

import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.springframework.context.event.EventListener;

@RefreshScope
@Configuration
public class CmpServersConfig {

    private static final String CMP_SERVERS_CONFIG_FILENAME = "cmpServers.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(CmpServersConfig.class);
    private static final String REFRESHING_CONFIGURATION = "Refreshing configuration";

    @Value("${app.config.path}")
    private String configPath;

    private CmpServersConfigLoader cmpServersConfigLoader;
    private List<Cmpv2Server> cmpServers;

    @Autowired
    public CmpServersConfig(CmpServersConfigLoader cmpServersConfigLoader) {
        this.cmpServersConfigLoader = cmpServersConfigLoader;
    }

    @PostConstruct
    void loadConfiguration() {
        String configFilePath = configPath + File.separator + CMP_SERVERS_CONFIG_FILENAME;
        this.cmpServers = Collections.unmodifiableList(cmpServersConfigLoader.load(configFilePath));
    }

    @EventListener
    public void onRefreshScope(final RefreshScopeRefreshedEvent event) {
        LOGGER.info(REFRESHING_CONFIGURATION);
        loadConfiguration();
    }

    public List<Cmpv2Server> getCmpServers() {
        return cmpServers;
    }
}
