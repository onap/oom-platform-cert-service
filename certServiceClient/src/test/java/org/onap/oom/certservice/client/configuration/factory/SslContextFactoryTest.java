/*
 * ============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.configuration.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.client.configuration.EnvsForTls;
import org.onap.oom.certservice.client.configuration.exception.TlsConfigurationException;

import javax.net.ssl.SSLContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SslContextFactoryTest {

    public static final String INVALID_KEYSTORE_PATH = "nonexistent/keystore";
    public static final String VALID_KEYSTORE_NAME = "keystore.jks";
    public static final String VALID_KEYSTORE_PASSWORD = "secret";
    public static final String INVALID_KEYSTORE_PASSWORD = "wrong_secret";
    public static final String INVALID_TRUSTSTORE_PATH = "nonexistent/truststore";
    public static final String VALID_TRUSTSTORE_PASSWORD = "secret";
    public static final String INVALID_TRUSTSTORE_PASSWORD = "wrong_secret";
    public static final String VALID_TRUSTSTORE_NAME = "truststore.jks";
    @Mock
    private EnvsForTls envsForTls;

    @Test
    public void shouldThrowExceptionWhenKeystorePathEnvIsMissing() {
        // Given
        when(envsForTls.getKeystorePath()).thenReturn(Optional.empty());
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        Exception exception = assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
        assertThat(exception.getMessage()).contains("KEYSTORE_PATH");
    }

    @Test
    public void shouldThrowExceptionWhenKeystorePasswordEnvIsMissing() {
        // Given
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of("keystore"));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.empty());
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        Exception exception = assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
        assertThat(exception.getMessage()).contains("KEYSTORE_PASSWORD");
    }

    @Test
    public void shouldThrowExceptionWhenTruststorePathEnvIsMissing() {
        // Given
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of("keystore"));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of("password"));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.empty());
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        Exception exception = assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
        assertThat(exception.getMessage()).contains("TRUSTSTORE_PATH");
    }

    @Test
    public void shouldThrowExceptionWhenTruststorePasswordEnvIsMissing() {
        // Given
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of("keystore"));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of("password"));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of("truststore"));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.empty());
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        Exception exception = assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
        assertThat(exception.getMessage()).contains("TRUSTSTORE_PASSWORD");
    }

    @Test
    public void shouldThrowExceptionWhenKeystoreIsMissing() {
        // Given
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of(INVALID_KEYSTORE_PATH));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of("secret"));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of("truststore.jks"));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.of("secret"));
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
    }

    @Test
    public void shouldThrowExceptionWhenKeystorePasswordIsWrong() {
        // Given
        String keystorePath = getResourcePath(VALID_KEYSTORE_NAME);
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of(keystorePath));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of(INVALID_KEYSTORE_PASSWORD));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of(VALID_TRUSTSTORE_NAME));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.of(VALID_TRUSTSTORE_PASSWORD));
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
    }

    @Test
    public void shouldThrowExceptionWhenTruststoreIsMissing() {
        // Given
        String keystorePath = getResourcePath(VALID_KEYSTORE_NAME);
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of(keystorePath));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of(VALID_KEYSTORE_PASSWORD));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of(INVALID_TRUSTSTORE_PATH));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.of(VALID_TRUSTSTORE_PASSWORD));
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
    }

    @Test
    public void shouldThrowExceptionWhenTruststorePasswordIsWrong() {
        // Given
        String keystorePath = getResourcePath(VALID_KEYSTORE_NAME);
        String truststorePath = getResourcePath(VALID_TRUSTSTORE_NAME);
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of(keystorePath));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of(VALID_KEYSTORE_PASSWORD));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of(truststorePath));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.of(INVALID_TRUSTSTORE_PASSWORD));
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When, Then
        assertThrows(
                TlsConfigurationException.class, sslContextFactory::create
        );
    }

    @Test
    public void shouldReturnSslContext() throws TlsConfigurationException {
        // Given
        String keystorePath = getResourcePath(VALID_KEYSTORE_NAME);
        String truststorePath = getResourcePath(VALID_TRUSTSTORE_NAME);
        when(envsForTls.getKeystorePath()).thenReturn(Optional.of(keystorePath));
        when(envsForTls.getKeystorePassword()).thenReturn(Optional.of(VALID_KEYSTORE_PASSWORD));
        when(envsForTls.getTruststorePath()).thenReturn(Optional.of(truststorePath));
        when(envsForTls.getTruststorePassword()).thenReturn(Optional.of(VALID_TRUSTSTORE_PASSWORD));
        SslContextFactory sslContextFactory = new SslContextFactory(envsForTls);

        // When
        SSLContext sslContext = sslContextFactory.create();

        // Then
        assertNotNull(sslContext);
    }

    private String getResourcePath(String resource) {
        return getClass().getClassLoader().getResource(resource).getFile();
    }
}

