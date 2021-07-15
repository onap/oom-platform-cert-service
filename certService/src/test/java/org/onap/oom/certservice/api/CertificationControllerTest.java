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

package org.onap.oom.certservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel;
import org.onap.oom.certservice.certification.CertificationResponseModelFactory;
import org.onap.oom.certservice.certification.exception.Cmpv2ServerNotFoundException;
import org.onap.oom.certservice.certification.exception.CsrDecryptionException;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel.CertificateUpdateModelBuilder;
import org.onap.oom.certservice.certification.model.CertificationResponseModel;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CertificationControllerTest {

    private static final String TEST_CA_NAME = "TestCa";
    private static final String TEST_ENCODED_CSR = "encodedCSR";
    private static final String TEST_ENCODED_PK = "encodedPK";
    private static final String TEST_WRONG_ENCODED_CSR = "wrongEncodedCSR";
    private static final String TEST_WRONG_ENCODED_PK = "wrongEncodedPK";
    private static final String TEST_WRONG_CA_NAME = "wrongTestCa";
    private static final String TEST_ENCODED_OLD_PK = "encodedOldPK";
    private static final String TEST_ENCODED_OLD_CERT = "encodedOldCert";
    private static final CertificateUpdateModel TEST_CERTIFICATE_UPDATE_MODEL = new CertificateUpdateModelBuilder()
        .setEncodedCsr(TEST_ENCODED_CSR)
        .setEncodedPrivateKey(TEST_ENCODED_PK)
        .setEncodedOldCert(TEST_ENCODED_OLD_CERT)
        .setEncodedOldPrivateKey(TEST_ENCODED_OLD_PK)
        .setCaName(TEST_CA_NAME)
        .build();

    private CertificationController certificationController;

    @Mock
    private CertificationResponseModelFactory certificationResponseModelFactory;

    @BeforeEach
    void serUp() {
        certificationController = new CertificationController(certificationResponseModelFactory);
    }

    @Test
    void shouldReturnDataAboutCsrBaseOnEncodedParameters()
            throws DecryptionException, CmpClientException {
        // Given
        CertificationResponseModel testCertificationResponseModel = new CertificationResponseModel(
                Arrays.asList("ENTITY_CERT", "INTERMEDIATE_CERT"),
                Arrays.asList("CA_CERT", "EXTRA_CA_CERT")
        );
        when(certificationResponseModelFactory
            .provideCertificationModelFromInitialRequest(TEST_ENCODED_CSR, TEST_ENCODED_PK, TEST_CA_NAME))
                .thenReturn(testCertificationResponseModel);

        // When
        ResponseEntity<CertificationResponseModel> responseCertificationModel =
                certificationController.signCertificate(TEST_CA_NAME, TEST_ENCODED_CSR, TEST_ENCODED_PK);

        // Then
        assertEquals(HttpStatus.OK, responseCertificationModel.getStatusCode());
        assertThat(responseCertificationModel.getBody()
        ).isEqualToComparingFieldByField(testCertificationResponseModel);

    }

    @Test
    void shouldThrowCsrDecryptionExceptionWhenCreatingCsrModelFails()
            throws DecryptionException, CmpClientException {
        // Given
        String expectedMessage = "Incorrect CSR, decryption failed";
        when(certificationResponseModelFactory
            .provideCertificationModelFromInitialRequest(TEST_WRONG_ENCODED_CSR, TEST_ENCODED_PK, TEST_CA_NAME))
                .thenThrow(new CsrDecryptionException(expectedMessage));

        // When
        Exception exception = assertThrows(
                CsrDecryptionException.class, () ->
                        certificationController.signCertificate(TEST_CA_NAME, TEST_WRONG_ENCODED_CSR, TEST_ENCODED_PK)
        );

        String actualMessage = exception.getMessage();

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowPemDecryptionExceptionWhenCreatingPemModelFails()
            throws DecryptionException, CmpClientException {
        // Given
        String expectedMessage = "Incorrect PEM, decryption failed";
        when(certificationResponseModelFactory
            .provideCertificationModelFromInitialRequest(TEST_ENCODED_CSR, TEST_WRONG_ENCODED_PK, TEST_CA_NAME))
                .thenThrow(new KeyDecryptionException(expectedMessage));

        // When
        Exception exception = assertThrows(
                KeyDecryptionException.class, () ->
                        certificationController.signCertificate(TEST_CA_NAME, TEST_ENCODED_CSR, TEST_WRONG_ENCODED_PK)
        );

        String actualMessage = exception.getMessage();

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowCmpv2ServerNotFoundWhenGivenWrongCaName()
            throws DecryptionException, CmpClientException {
        // Given
        String expectedMessage = "No server found for given CA name";
        when(certificationResponseModelFactory
            .provideCertificationModelFromInitialRequest(TEST_ENCODED_CSR, TEST_ENCODED_PK, TEST_WRONG_CA_NAME))
                .thenThrow(new Cmpv2ServerNotFoundException(expectedMessage));

        // When
        Exception exception = assertThrows(
                Cmpv2ServerNotFoundException.class, () ->
                        certificationController.signCertificate(TEST_WRONG_CA_NAME, TEST_ENCODED_CSR, TEST_ENCODED_PK)
        );

        String actualMessage = exception.getMessage();

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldUpdateEndpointReturnDataAboutCsrBaseOnEncodedParameters()
        throws DecryptionException, CmpClientException {
        // Given
        CertificationResponseModel testCertificationResponseModel = new CertificationResponseModel(
                Arrays.asList("ENTITY_CERT", "INTERMEDIATE_CERT"),
                Arrays.asList("CA_CERT", "EXTRA_CA_CERT")
        );
        when(certificationResponseModelFactory.provideCertificationModelFromUpdateRequest(TEST_CERTIFICATE_UPDATE_MODEL)).thenReturn(
            testCertificationResponseModel);

        // When
        ResponseEntity<CertificationResponseModel> responseCertificationModel =
                certificationController.updateCertificate(TEST_CA_NAME, TEST_ENCODED_CSR,
                        TEST_ENCODED_PK, TEST_ENCODED_OLD_CERT, TEST_ENCODED_OLD_PK);

        // Then
        assertEquals(HttpStatus.OK, responseCertificationModel.getStatusCode());
        assertThat(responseCertificationModel.getBody()).isEqualToComparingFieldByField(testCertificationResponseModel);
    }

    @Test
    void shouldThrowCertificateDecryptionExceptionWhenCreatingPemModelFails()
        throws DecryptionException, CmpClientException {
        // Given
        String expectedMessage = "Incorrect certificate, decryption failed";
        when(certificationResponseModelFactory.provideCertificationModelFromUpdateRequest(TEST_CERTIFICATE_UPDATE_MODEL))
            .thenThrow(new CertificateDecryptionException(expectedMessage));

        // When
        Exception exception = assertThrows(
            CertificateDecryptionException.class, () ->
                certificationController.updateCertificate(TEST_CA_NAME, TEST_ENCODED_CSR,
                    TEST_ENCODED_PK, TEST_ENCODED_OLD_CERT, TEST_ENCODED_OLD_PK)
        );

        String actualMessage = exception.getMessage();

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

}
