/*
 * ============LICENSE_START=======================================================
 * Cert Service
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

package org.onap.aaf.certservice.certification.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aaf.certservice.certification.configuration.model.CaMode;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.api.CmpClient;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Cmpv2ClientAdapterTest {

    @Mock
    private CmpClient cmpClient;
    @Mock
    private CsrModel csrModel;
    @Mock
    private Cmpv2Server server;
    @Mock
    private RsaContentSignerBuilder rsaContentSignerBuilder;
    @Mock
    private X509CertificateBuilder x509CertificateBuilder;
    @Mock
    private PKCS10CertificationRequest csr;
    @Mock
    private PrivateKey privateKey;
    @Mock
    private X509v3CertificateBuilder x509V3CertificateBuilder;
    @Mock
    private ContentSigner contentSigner;
    @Mock
    private X509CertificateHolder holder;
    @Mock
    private Certificate asn1Certificate;
    @Mock
    private X509Certificate certificate;
    @Mock
    private CertificateFactoryProvider certificateFactoryProvider;

    @InjectMocks
    private Cmpv2ClientAdapter adapter;

    private static final CaMode CA_MODEL = CaMode.CLIENT;
    private static final String TEST_MSG = "Test";

    @Test
    void adapterShouldRethrowClientExceptionOnFailure()
            throws CmpClientException, IOException, OperatorCreationException, CertificateException,
            NoSuchProviderException {
        // Given
        stubInternalProperties();

        // When
        Mockito.when(cmpClient.createCertificate(Mockito.any(), Mockito.any()))
                .thenThrow(new CmpClientException(TEST_MSG));

        // Then
        Assertions.assertThrows(CmpClientException.class, () -> adapter.callCmpClient(csrModel, server));
    }

    @Test
    void shouldConvertToCertificationModel()
            throws OperatorCreationException, CertificateException, NoSuchProviderException, IOException,
            CmpClientException {
        // Given
        stubInternalProperties();

        // When
        Mockito.when(cmpClient.createCertificate(Mockito.any(), Mockito.any()))
                .thenReturn(createCorrectClientResponse());
        CertificationModel certificationModel = adapter.callCmpClient(csrModel, server);

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

        Assertions.assertEquals(certificateModel, expectedCertificate);
        Assertions.assertEquals(trustedCertificateModel, expectedTrustedCertificate);
    }

    private List<List<X509Certificate>> createCorrectClientResponse()
            throws CertificateException, NoSuchProviderException {
        InputStream certificateChain = getClass().getClassLoader().getResourceAsStream("certificateChain.first");
        InputStream trustedCertificate = getClass().getClassLoader().getResourceAsStream("trustedCertificates.first");
        X509Certificate x509Certificate = new CertificateFactoryProvider().generateCertificate(certificateChain);
        X509Certificate x509TrustedCertificate =
                new CertificateFactoryProvider().generateCertificate(trustedCertificate);
        return Arrays.asList(Collections.singletonList(x509Certificate),
                Collections.singletonList(x509TrustedCertificate));
    }

    private String removeLineEndings(String string) {
        return string.replace("\n", "").replace("\r", "");
    }

    private void stubInternalProperties()
            throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException {
        Mockito.when(server.getCaMode()).thenReturn(CA_MODEL);
        Mockito.when(csrModel.getCsr()).thenReturn(csr);
        Mockito.when(csrModel.getPrivateKey()).thenReturn(privateKey);
        Mockito.when(x509CertificateBuilder.build(csr)).thenReturn(x509V3CertificateBuilder);
        Mockito.when(rsaContentSignerBuilder.build(csr, privateKey)).thenReturn(contentSigner);
        Mockito.when(x509V3CertificateBuilder.build(contentSigner)).thenReturn(holder);
        Mockito.when(holder.toASN1Structure()).thenReturn(asn1Certificate);
        Mockito.when(certificateFactoryProvider.generateCertificate(Mockito.any())).thenReturn(certificate);
        Mockito.when(holder.toASN1Structure().getEncoded()).thenReturn("".getBytes());
    }

}
