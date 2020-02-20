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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.onap.aaf.certservice.CertServiceApplication;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
@TestPropertySource(properties = {"app.config.path=/fake/path/to/config"})
class CmpServersConfigTest {

    private static final List<Cmpv2Server> SAMPLE_CMP_SERVERS = List.of(
            new Cmpv2Server(),
            new Cmpv2Server()
    );

    @MockBean
    private CmpServersConfigLoader cmpServersConfigLoader;

    @Autowired
    private CmpServersConfig cmpServersConfig;

    @Test
    public void shouldCallLoaderWithPathFromPropertiesWhenCreated() {
        Mockito.verify(cmpServersConfigLoader).load(startsWith("/fake/path/to/config"));
    }

    @Test
    public void shouldReturnLoadedServersWhenGetCalled() {
        // Given
        Mockito.when(cmpServersConfigLoader.load(any())).thenReturn(SAMPLE_CMP_SERVERS);
        this.cmpServersConfig.loadConfiguration();      // Manual PostConstruct call

        // When
        List<Cmpv2Server> receivedCmpServers = this.cmpServersConfig.getCmpServers();

        // Then
        assertThat(receivedCmpServers).hasSize(SAMPLE_CMP_SERVERS.size());
    }
}