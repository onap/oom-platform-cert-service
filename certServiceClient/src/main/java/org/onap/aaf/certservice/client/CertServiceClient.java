/*============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client;

import org.onap.aaf.certservice.client.api.ExitableException;
import org.onap.aaf.certservice.client.certification.CsrFactory;
import org.onap.aaf.certservice.client.certification.KeyPairFactory;
import org.onap.aaf.certservice.client.certification.conversion.KeystoreTruststoreCreator;
import org.onap.aaf.certservice.client.certification.conversion.KeystoreTruststoreCreatorFactory;

import java.security.KeyPair;
import org.onap.aaf.certservice.client.configuration.EnvsForClient;
import org.onap.aaf.certservice.client.configuration.EnvsForCsr;
import org.onap.aaf.certservice.client.configuration.factory.ClientConfigurationFactory;
import org.onap.aaf.certservice.client.configuration.factory.CsrConfigurationFactory;
import org.onap.aaf.certservice.client.configuration.model.ClientConfiguration;
import org.onap.aaf.certservice.client.configuration.model.CsrConfiguration;
import org.onap.aaf.certservice.client.httpclient.CloseableHttpClientProvider;
import org.onap.aaf.certservice.client.httpclient.HttpClient;
import org.onap.aaf.certservice.client.httpclient.model.CertServiceResponse;

import static org.onap.aaf.certservice.client.api.ExitCode.SUCCESS_EXIT_CODE;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.KEY_SIZE;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.RSA_ENCRYPTION_ALGORITHM;
import static org.onap.aaf.certservice.client.common.Base64Coder.encode;

public class CertServiceClient {

    private AppExitHandler appExitHandler;

    public CertServiceClient(AppExitHandler appExitHandler) {
        this.appExitHandler = appExitHandler;
    }

    public void run() {
        KeyPairFactory keyPairFactory = new KeyPairFactory(RSA_ENCRYPTION_ALGORITHM, KEY_SIZE);
        try {
            ClientConfiguration clientConfiguration = new ClientConfigurationFactory(new EnvsForClient()).create();
            CsrConfiguration csrConfiguration = new CsrConfigurationFactory(new EnvsForCsr()).create();
            KeyPair keyPair = keyPairFactory.create();
            CsrFactory csrFactory = new CsrFactory(csrConfiguration);
            String csr = csrFactory.createEncodedCsr(keyPair);

            CloseableHttpClientProvider provider = new CloseableHttpClientProvider(
                clientConfiguration.getRequestTimeout());
            HttpClient httpClient = new HttpClient(provider, clientConfiguration.getUrlToCertService());

            CertServiceResponse certServiceData =
                httpClient.retrieveCertServiceData(
                    clientConfiguration.getCaName(),
                    csr,
                    encode(keyPair.getPrivate().toString()));

            KeystoreTruststoreCreator filesCreator = new KeystoreTruststoreCreatorFactory(
                clientConfiguration.getCertsOutputPath()).create();
            filesCreator.createKeystore(certServiceData.getCertificateChain(), keyPair.getPrivate());
            filesCreator.createTruststore(certServiceData.getTrustedCertificates());
        } catch (ExitableException e) {
            appExitHandler.exit(e.applicationExitCode());
        }
        appExitHandler.exit(SUCCESS_EXIT_CODE.getValue());
    }
}
