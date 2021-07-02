/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.oom.certservice.CertServiceApplication;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.configuration.model.CrProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
class CmpServersConfigLoaderTest {
    private static final String EXISTING_CONFIG_FILENAME = "cmpServers.json";
    private static final String INVALID_CONFIG_FILENAME = "invalidCmpServers.json";
    private static final String NONEXISTENT_CONFIG_FILENAME = "nonExistingCmpServers.json";

    private static final Map<String, String> EXPECTED_FIRST_CMP_SERVER = Map.of(
            "CA_NAME", "TEST",
            "URL", "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
            "ISSUER_DN", "CN=ManagementCA",
            "CA_MODE", "CLIENT",
            "IAK", "xxx",
            "RV", "yyy",
            "CR_PROTECTION", "IAK_RV"

    );
    private static final Map<String, String> EXPECTED_SECOND_CMP_SERVER = Map.of(
            "CA_NAME", "TEST2",
            "URL", "http://127.0.0.1/ejbca/publicweb/cmp/cmpRA",
            "ISSUER_DN", "CN=ManagementCA2",
            "CA_MODE", "RA",
            "IAK", "xxx",
            "RV", "yyy",
            "CR_PROTECTION", "CR_CERT"
    );

    @Autowired
    private CmpServersConfigLoader configLoader;

    @Test
    void shouldLoadCmpServersConfigWhenFileAvailable() throws CmpServersConfigLoadingException {
        // Given
        String path = getResourcePath(EXISTING_CONFIG_FILENAME);

        // When
        List<Cmpv2Server> cmpServers = configLoader.load(path);

        // Then
        assertThat(cmpServers)
            .isNotNull()
            .hasSize(2);
        verifyThatCmpServerEquals(cmpServers.get(0), EXPECTED_FIRST_CMP_SERVER);
        verifyThatCmpServerEquals(cmpServers.get(1), EXPECTED_SECOND_CMP_SERVER);
    }

    @Test
    void shouldThrowExceptionWhenFileMissing() {
        // When
        Exception exception = assertThrows(
                CmpServersConfigLoadingException.class,
                () -> configLoader.load(NONEXISTENT_CONFIG_FILENAME));

        // Then
        assertThat(exception.getMessage()).contains("Exception occurred during CMP Servers configuration loading");
    }

    @Test
    void shouldThrowExceptionWhenConfigurationIsInvalid() {
        // Given
        String path = getResourcePath(INVALID_CONFIG_FILENAME);

        // When
        Exception exception = assertThrows(
                CmpServersConfigLoadingException.class,
                () -> configLoader.load(path));

        // Then
        assertThat(exception.getMessage()).contains("Validation of CMPv2 servers configuration failed");
        assertThat(exception.getCause().getMessage()).contains("authentication");
    }

    private String getResourcePath(String configFilename) {
        return getClass().getClassLoader().getResource(configFilename).getFile();
    }

    private void verifyThatCmpServerEquals(Cmpv2Server cmpv2Server, Map<String, String> expected) {
        assertThat(cmpv2Server.getCaName()).isEqualTo(expected.get("CA_NAME"));
        assertThat(cmpv2Server.getUrl()).isEqualTo(expected.get("URL"));
        assertThat(cmpv2Server.getIssuerDN()).hasToString(expected.get("ISSUER_DN"));
        assertThat(cmpv2Server.getCaMode().name()).isEqualTo(expected.get("CA_MODE"));
        assertThat(cmpv2Server.getAuthentication().getIak()).isEqualTo(expected.get("IAK"));
        assertThat(cmpv2Server.getAuthentication().getRv()).isEqualTo(expected.get("RV"));
        assertThat(cmpv2Server.getCrProtection()).isEqualTo(CrProtection.valueOf(expected.get("CR_PROTECTION")));
    }
}
