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

package org.onap.oom.certservice.certification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.onap.oom.certservice.certification.CertificationData.CA_CERT;
import static org.onap.oom.certservice.certification.CertificationData.ENTITY_CERT;
import static org.onap.oom.certservice.certification.CertificationData.EXTRA_CA_CERT;
import static org.onap.oom.certservice.certification.CertificationData.INTERMEDIATE_CERT;
import static org.onap.oom.certservice.certification.TestData.TEST_CSR;
import static org.onap.oom.certservice.certification.TestData.TEST_PK;
import static org.onap.oom.certservice.certification.TestData.TEST_WRONG_CSR;
import static org.onap.oom.certservice.certification.TestData.TEST_WRONG_PEM;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.certification.configuration.Cmpv2ServerProvider;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.exception.Cmpv2ClientAdapterException;
import org.onap.oom.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.oom.certservice.certification.exception.CsrDecryptionException;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel.CertificateUpdateModelBuilder;
import org.onap.oom.certservice.certification.model.CertificationModel;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.certification.model.X509CertificateModel;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;

@ExtendWith(MockitoExtension.class)
class CertificationModelFactoryTest {

    private static final String TEST_CA = "testCA";
    private static final String ENCODED_CSR = getEncodedString(TEST_CSR);
    private static final String ENCODED_PK = getEncodedString(TEST_PK);
    private static final String ENCODED_WRONG_CSR = getEncodedString(TEST_WRONG_CSR);
    private static final String ENCODED_WRONG_PK = getEncodedString(TEST_WRONG_PEM);
    private static final String TEST_CA_NAME = "TestCa";
    private static final String TEST_ENCODED_CSR = "encodedCSR";
    private static final String TEST_ENCODED_PK = "encodedPK";
    private static final String TEST_ENCODED_OLD_PK = "encodedOldPK";
    private static final String TEST_ENCODED_OLD_CERT = "encodedOldCert";
    private static final CertificateUpdateModel TEST_CERTIFICATE_UPDATE_MODEL = new CertificateUpdateModelBuilder()
        .setEncodedCsr(TEST_ENCODED_CSR)
        .setEncodedPrivateKey(TEST_ENCODED_PK)
        .setEncodedOldCert(TEST_ENCODED_OLD_CERT)
        .setEncodedOldPrivateKey(TEST_ENCODED_OLD_PK)
        .setCaName(TEST_CA_NAME)
        .build();

    private CertificationModelFactory certificationModelFactory;

    @Mock
    private Cmpv2ServerProvider cmpv2ServerProvider;
    @Mock
    private CsrModelFactory csrModelFactory;
    @Mock
    private CertificationProvider certificationProvider;
    @Mock
    private X509CertificateModelFactory x509CertificateModelFactory;
    @Mock
    private UpdateRequestTypeDetector updateRequestTypeDetector;

    private static String getEncodedString(String testCsr) {
        return Base64.getEncoder().encodeToString(testCsr.getBytes());
    }

    @BeforeEach
    void setUp() {
        certificationModelFactory =
            new CertificationModelFactory(csrModelFactory, cmpv2ServerProvider, certificationProvider,
                x509CertificateModelFactory, updateRequestTypeDetector);
    }

    @Test
    void shouldCreateProperCertificationModelWhenGivenProperCsrModelAndCaName()
        throws CmpClientException, DecryptionException, Cmpv2ClientAdapterException {

        // Given
        CsrModel csrModel = mockCsrFactoryModelCreation();
        Cmpv2Server testServer = mockCmpv2ProviderServerSelection();
        mockCertificateProviderCertificateSigning(csrModel, testServer);

        // When
        CertificationModel certificationModel =
            certificationModelFactory.createCertificationModel(ENCODED_CSR, ENCODED_PK, TEST_CA);

        // Then
        assertEquals(2, certificationModel.getCertificateChain().size());
        assertThat(certificationModel.getCertificateChain()).contains(INTERMEDIATE_CERT, ENTITY_CERT);
        assertEquals(2, certificationModel.getTrustedCertificates().size());
        assertThat(certificationModel.getTrustedCertificates()).contains(CA_CERT, EXTRA_CA_CERT);
    }

    @Test
    void shouldThrowDecryptionExceptionWhenGivenWrongEncodedCsr()
        throws DecryptionException {
        // Given
        String expectedMessage = "Incorrect CSR, decryption failed";
        when(
            csrModelFactory.createCsrModel(
                new StringBase64(ENCODED_WRONG_CSR),
                new StringBase64(ENCODED_WRONG_PK)
            )
        ).thenThrow(
            new CsrDecryptionException(expectedMessage)
        );

        // When
        Exception exception = assertThrows(
            DecryptionException.class, () ->
                certificationModelFactory.createCertificationModel(ENCODED_WRONG_CSR, ENCODED_WRONG_PK, TEST_CA)
        );

        // Then
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void shouldThrowCmpv2ServerNotFoundExceptionWhenGivenWrongCaName()
        throws DecryptionException {
        // Given
        String expectedMessage = "CA not found";
        mockCsrFactoryModelCreation();
        when(
            cmpv2ServerProvider.getCmpv2Server(TEST_CA)
        ).thenThrow(
            new Cmpv2ServerNotFoundException(expectedMessage)
        );

        // When
        Exception exception = assertThrows(
            Cmpv2ServerNotFoundException.class, () ->
                certificationModelFactory.createCertificationModel(ENCODED_CSR, ENCODED_PK, TEST_CA)
        );

        // Then
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void shouldThrowCmpClientExceptionWhenSigningCsrFailed()
        throws DecryptionException, CmpClientException, Cmpv2ClientAdapterException {
        // Given
        String expectedMessage = "failed to sign certificate";
        CsrModel csrModel = mockCsrFactoryModelCreation();
        Cmpv2Server testServer = mockCmpv2ProviderServerSelection();
        when(
            certificationProvider.signCsr(csrModel, testServer)
        ).thenThrow(
            new CmpClientException(expectedMessage)
        );

        // When
        Exception exception = assertThrows(
            CmpClientException.class, () ->
                certificationModelFactory.createCertificationModel(ENCODED_CSR, ENCODED_PK, TEST_CA)
        );

        // Then
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void shouldPerformKurWhenCsrAndOldCertDataMatch() throws CertificateDecryptionException, DecryptionException {
        //given
        mockCsrFactoryModelCreation();
        mockCertificateFactoryModelCreation();
        when(updateRequestTypeDetector.isKur(any(), any())).thenReturn(true);
        //when, then
        Exception exception = assertThrows(
            UnsupportedOperationException.class, () ->
                certificationModelFactory.createCertificationModel(TEST_CERTIFICATE_UPDATE_MODEL)
        );
        assertEquals(exception.getMessage(), "TODO: implement KUR in separate MR");
    }

    @Test
    void shouldPerformCrWhenCsrAndOldCertDataMatch() throws CertificateDecryptionException, DecryptionException {
        //given
        mockCsrFactoryModelCreation();
        mockCertificateFactoryModelCreation();
        when(updateRequestTypeDetector.isKur(any(), any())).thenReturn(false);
        //when, then
        Exception exception = assertThrows(
            UnsupportedOperationException.class, () ->
                certificationModelFactory.createCertificationModel(TEST_CERTIFICATE_UPDATE_MODEL)
        );
        assertEquals(exception.getMessage(), "TODO: implement CR in separate MR");
    }

    @Test
    void shouldThrowCertificateDecryptionExceptionWhenOldCertificateInvalid()
        throws CertificateDecryptionException {
        //given
        when(x509CertificateModelFactory.createCertificateModel(any()))
            .thenThrow(new CertificateDecryptionException("Incorrect certificate, decryption failed"));
        //when, then
        assertThrows(
            CertificateDecryptionException.class, () ->
                certificationModelFactory.createCertificationModel(TEST_CERTIFICATE_UPDATE_MODEL)
        );
    }

    private void mockCertificateProviderCertificateSigning(CsrModel csrModel, Cmpv2Server testServer)
        throws CmpClientException, Cmpv2ClientAdapterException {
        CertificationModel expectedCertificationModel = getCertificationModel();
        when(
            certificationProvider.signCsr(csrModel, testServer)
        ).thenReturn(expectedCertificationModel);
    }

    private Cmpv2Server mockCmpv2ProviderServerSelection() {
        Cmpv2Server testServer = getCmpv2Server();
        when(
            cmpv2ServerProvider.getCmpv2Server(TEST_CA)
        ).thenReturn(testServer);
        return testServer;
    }

    private CsrModel mockCsrFactoryModelCreation()
        throws DecryptionException {
        CsrModel csrModel = getCsrModel();
        when(csrModelFactory.createCsrModel(any(), any())).thenReturn(csrModel);
        return csrModel;
    }

    private X509CertificateModel mockCertificateFactoryModelCreation()
        throws CertificateDecryptionException {
        final X509CertificateModel certificateModel = mock(X509CertificateModel.class);
        when(x509CertificateModelFactory.createCertificateModel(any())).thenReturn(certificateModel);
        return certificateModel;
    }

    private Cmpv2Server getCmpv2Server() {
        return new Cmpv2Server();
    }

    private CsrModel getCsrModel() {
        return mock(CsrModel.class);
    }

    private CertificationModel getCertificationModel() {
        List<String> testTrustedCertificates = Arrays.asList(CA_CERT, EXTRA_CA_CERT);
        List<String> testCertificationChain = Arrays.asList(INTERMEDIATE_CERT, ENTITY_CERT);
        return new CertificationModel(testCertificationChain, testTrustedCertificates);
    }


}
