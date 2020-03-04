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

package org.onap.aaf.certservice.certification.configuration.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.aaf.certservice.CertServiceApplication;
import org.onap.aaf.certservice.certification.configuration.model.Authentication;
import org.onap.aaf.certservice.certification.configuration.model.CaMode;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
class Cmpv2ServerConfigurationValidatorTest {

    private static final String EMPTY_STRING = "";

    @Autowired
    private Cmpv2ServerConfigurationValidator validator;

    private Authentication authentication;
    private Cmpv2Server server;

    @BeforeEach
    private void init() {
        setAuthentication();
        setServerConfiguration();
    }

    @Test
    public void shouldNotThrowExceptionWhenServerConfigurationIsValid() {
        // Then
        assertDoesNotThrow(() -> validator.validate(server));
    }

    @Test
    public void shouldThrowExceptionWhenWrongProtocolInURL() {
        // Given
        server.setUrl("https://test.test.test:60000/");

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenWrongPortInURL() {
        // Given
        server.setUrl("http://test.test.test:70000/");

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenWrongCANameLength() {
        // Given
        server.setCaName(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenWrongRVLength() {
        // Given
        authentication.setRv(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }


    @Test
    public void shouldThrowExceptionWhenWrongIAKLength() {
        // Given
        authentication.setIak(EMPTY_STRING);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenCaNameIsNull() {
        // Given
        server.setCaName(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenIssuerDnIsNull() {
        // Given
        server.setIssuerDN(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenCaModeIsNull() {
        // Given
        server.setCaMode(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenUrlIsNull() {
        // Given
        server.setUrl(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenAuthenticationIsNull() {
        // Given
        server.setAuthentication(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenIakIsNull() {
        // Given
        authentication.setIak(null);

        // Then
        assertExceptionIsThrown();
    }

    @Test
    public void shouldThrowExceptionWhenRvIsNull() {
        // Given
        authentication.setRv(null);

        // Then
        assertExceptionIsThrown();
    }

    private void assertExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    private void setServerConfiguration() {
        server = new Cmpv2Server();
        server.setCaMode(CaMode.CLIENT);
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
