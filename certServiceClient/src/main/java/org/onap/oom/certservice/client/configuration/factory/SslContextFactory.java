/*============LICENSE_START=======================================================
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

import org.apache.http.ssl.SSLContexts;
import org.onap.oom.certservice.client.configuration.EnvsForTls;
import org.onap.oom.certservice.client.configuration.TlsConfigurationEnvs;
import org.onap.oom.certservice.client.configuration.exception.TlsConfigurationException;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SslContextFactory {

    private static final String JKS = "jks";

    private EnvsForTls envsForTls;

    public SslContextFactory(EnvsForTls envsForTls) {
        this.envsForTls = envsForTls;
    }

    public SSLContext create() throws TlsConfigurationException {
        String keystorePath = envsForTls.getKeystorePath()
                .orElseThrow(() -> new TlsConfigurationException(createEnvMissingMessage(TlsConfigurationEnvs.KEYSTORE_PATH)));
        String keystorePassword = envsForTls.getKeystorePassword()
                .orElseThrow(() -> new TlsConfigurationException(createEnvMissingMessage(TlsConfigurationEnvs.KEYSTORE_PASSWORD)));
        String truststorePath = envsForTls.getTruststorePath()
                .orElseThrow(() -> new TlsConfigurationException(createEnvMissingMessage(TlsConfigurationEnvs.TRUSTSTORE_PATH)));
        String truststorePassword = envsForTls.getTruststorePassword()
                .orElseThrow(() -> new TlsConfigurationException(createEnvMissingMessage(TlsConfigurationEnvs.TRUSTSTORE_PASSWORD)));

        return createSslContext(keystorePath, keystorePassword, truststorePath, truststorePassword);
    }

    private String createEnvMissingMessage(TlsConfigurationEnvs keystorePath) {
        return String.format("%s env is missing.", keystorePath);
    }

    private KeyStore setupKeystore(String keystorePath, String certPassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(JKS);
        FileInputStream identityKeyStoreFile = new FileInputStream(new File(
                keystorePath));
        keyStore.load(identityKeyStoreFile, certPassword.toCharArray());
        return keyStore;
    }

    private SSLContext createSslContext(String keystorePath, String keystorePassword, String truststorePath, String truststorePassword) throws TlsConfigurationException {
        try {
            KeyStore identityKeystore = setupKeystore(keystorePath, keystorePassword);
            KeyStore trustKeystore = setupKeystore(truststorePath, truststorePassword);

            return SSLContexts.custom()
                    .loadKeyMaterial(identityKeystore, keystorePassword.toCharArray())
                    .loadTrustMaterial(trustKeystore, null)
                    .build();
        } catch (Exception e) {
            throw new TlsConfigurationException("TLS configuration exception: " + e);
        }
    }
}
