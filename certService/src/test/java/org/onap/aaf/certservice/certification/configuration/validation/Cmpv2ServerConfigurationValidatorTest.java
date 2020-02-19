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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertServiceApplication.class)
class Cmpv2ServerConfigurationValidatorTest {

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
    public void givenValidServerDetailsWhenValidatingShouldNotThrowAnyException() {
        //then
        assertDoesNotThrow(() -> validator.validate(server));
    }

    @Test
    public void givenWrongProtocolInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        server.setUrl("https://test.test.test:60000/");

        //then
        assertThrows(IllegalArgumentException.class, () -> {validator.validate(server);});
    }

    @Test
    public void givenWrongPortInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        server.setUrl("http://test.test.test:70000/");

        //then
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    @Test
    public void givenWrongCANameLengthInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        server.setCaName("");

        //then
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    @Test
    public void givenWrongIssuerDNLengthInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        server.setIssuerDN("123");

        //then
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    @Test
    public void givenWrongRVLengthInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        authentication.setRv("");

        //then
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    @Test
    public void givenWrongIAKLengthInURLServerDetailsWhenValidatingShouldThrowException() {
        //given
        authentication.setIak("");

        //then
        assertThrows(IllegalArgumentException.class, () -> validator.validate(server));
    }

    private void setServerConfiguration() {
        server = new Cmpv2Server();
        server.setCaMode(CaMode.CLIENT);
        server.setCaName("TEST");
        server.setIssuerDN("CN=ManagementCA");
        server.setUrl("http://127.0.0.1/ejbca/publicweb/cmp/cmp");
        server.setAuthentication(authentication);
    }

    private void setAuthentication() {
        authentication = new Authentication();
        authentication.setRv("testRV");
        authentication.setIak("testIAK");
    }
}