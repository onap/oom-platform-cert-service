/*
 * ============LICENSE_START=======================================================
 * OOM Certification Service
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

package org.onap.oom.certservice.certification;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.model.CertificationModel;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.cmpv2client.api.CmpClient;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationProviderTest {

    @Mock
    private CsrModel csrModel;
    @Mock
    private Cmpv2Server server;
    @Mock
    private CsrModel testCsrModel;
    @Mock
    private Cmpv2Server testServer;
    @Mock
    private CmpClient cmpClient;

    private CertificationProvider certificationProvider;

    @BeforeEach
    public void init() {
        certificationProvider = new CertificationProvider(cmpClient);
    }

    @Test
    void shouldConvertToCertificationModel()
            throws CertificateException, NoSuchProviderException, IOException, CmpClientException {
        // When
        when(
                cmpClient.createCertificate(any(CsrModel.class), any(Cmpv2Server.class))
        ).thenReturn(createCorrectClientResponse());

        CertificationModel certificationModel = certificationProvider.signCsr(csrModel, server);

        // Then
        InputStream certificate = getClass().getClassLoader().getResourceAsStream("certificateModelChain.first");
        InputStream trustedCertificate =
                getClass().getClassLoader().getResourceAsStream("trustedCertificatesModel.first");
        String certificateModel = removeLineEndings(certificationModel.getCertificateChain().get(0));
        String expectedCertificate =
                removeLineEndings(IOUtils.toString(Objects.requireNonNull(certificate), StandardCharsets.UTF_8));
        String trustedCertificateModel = removeLineEndings(certificationModel.getTrustedCertificates().get(0));
        String expectedTrustedCertificate =
                removeLineEndings(IOUtils.toString(Objects.requireNonNull(trustedCertificate), StandardCharsets.UTF_8));

        assertThat(certificateModel).isEqualTo(expectedCertificate);
        assertThat(trustedCertificateModel).isEqualTo(expectedTrustedCertificate);
    }


    @Test
    void certificationProviderThrowCmpClientWhenCallingClientFails()
            throws CmpClientException {
        // Given
        String expectedErrorMessage = "connecting to CMP client failed";

        when(
                cmpClient.createCertificate(any(CsrModel.class), any(Cmpv2Server.class))
        ).thenThrow(new CmpClientException(expectedErrorMessage));

        // When
        Exception exception = assertThrows(
                CmpClientException.class, () ->
                        certificationProvider.signCsr(testCsrModel, testServer)
        );

        // Then
        assertThat(exception.getMessage()).isEqualTo(expectedErrorMessage);
    }

    private Cmpv2CertificationModel createCorrectClientResponse()
            throws CertificateException, NoSuchProviderException {
        InputStream certificateChain = getClass().getClassLoader().getResourceAsStream("certificateChain.first");
        InputStream trustedCertificate = getClass().getClassLoader().getResourceAsStream("trustedCertificates.first");
        X509Certificate x509Certificate = new CertificateFactoryProvider().generateCertificate(certificateChain);
        X509Certificate x509TrustedCertificate =
                new CertificateFactoryProvider().generateCertificate(trustedCertificate);
        return new Cmpv2CertificationModel(
                Collections.singletonList(x509Certificate),
                Collections.singletonList(x509TrustedCertificate));
    }

    private String removeLineEndings(String string) {
        return string.replace("\n", "").replace("\r", "");
    }
}
