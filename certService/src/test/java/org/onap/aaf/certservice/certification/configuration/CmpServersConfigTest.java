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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.aaf.certservice.CertServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
class CmpServersConfigTest {

    @Autowired
    private CmpServersConfig cmpServersConfig;

    @Test
    public void shouldLoadCmpServersConfig() {
        // Then
        assertThat(cmpServersConfig.getCmpServers()).isNotNull();
        assertThat(cmpServersConfig.getCmpServers().size()).isEqualTo(2);
        assertThat(cmpServersConfig.getCmpServers().get(0).getCaName()).isEqualTo("TEST");
        assertThat(cmpServersConfig.getCmpServers().get(1).getCaName()).isEqualTo("TEST2");
    }
}