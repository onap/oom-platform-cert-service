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

package org.onap.oom.certservice.certification.configuration.validation;


import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.oom.certservice.CertServiceApplication;
import org.onap.oom.certservice.certification.configuration.model.Authentication;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
class Cmpv2ServersConfigurationValidatorTest {

    private static final String EMPTY_STRING = "";

    @Autowired
    private Cmpv2ServersConfigurationValidator validator;

    private Authentication authentication;
    private Cmpv2Server server;
    private List<Cmpv2Server> servers;

    @BeforeEach
    private void init() {
        setAuthentication();
        setServerConfiguration();
        servers = new ArrayList<>();
        servers.add(server);
    }

    @Test
    void shouldThrowExceptionWhenCaNamesAreNotUnique() {
        // Given
        servers.add(server);

        // When
        Exception exception = assertThrows(
                InvalidParameterException.class,
                () -> validator.validate(servers));

        // Then
        assertThat(exception.getMessage()).contains("CA names are not unique within given CMPv2 servers");
    }

    @Test
    void shouldThrowExceptionWhenWrongProtocolInUrl() {
        // Given
        server.setUrl("https://test.test.test:60000/");

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenWrongPortInUrl() {
        // Given
        server.setUrl("http://test.test.test:70000/");

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenWrongCaNameLength() {
        // Given
        server.setCaName(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenWrongRvLength() {
        // Given
        authentication.setRv(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }


    @Test
    void shouldThrowExceptionWhenWrongIakLength() {
        // Given
        authentication.setIak(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenCaNameIsNull() {
        // Given
        server.setCaName(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenIssuerDnIsNull() {
        // Given
        server.setIssuerDN(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenUrlIsNull() {
        // Given
        server.setUrl(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        // Given
        server.setAuthentication(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenIakIsNull() {
        // Given
        authentication.setIak(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldThrowExceptionWhenRvIsNull() {
        // Given
        authentication.setRv(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    void shouldNotThrowExceptionWhenServerConfigurationIsValid() {
        // Then
        assertDoesNotThrow(() -> validator.validate(servers));
    }

    private void assertExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(servers));
    }

    private void setServerConfiguration() {
        server = new Cmpv2Server();
        server.setCaName("TEST");
        server.setIssuerDN(new X500Name("CN=ManagementCA"));
        server.setUrl("http://127.0.0.1/ejbca/publicweb/cmp/cmp");
        server.setAuthentication(authentication);
    }

    private void setAuthentication() {
        authentication = new Authentication();
        authentication.setRv("testRV");
        authentication.setIak("testIAK");
    }

}
