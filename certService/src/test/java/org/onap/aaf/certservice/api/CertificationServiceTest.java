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

package org.onap.aaf.certservice.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aaf.certservice.certification.CertificationModelFactory;
import org.onap.aaf.certservice.certification.CsrModelFactory;
import org.onap.aaf.certservice.certification.CsrModelFactory.StringBase64;
import org.onap.aaf.certservice.certification.exceptions.CsrDecryptionException;
import org.onap.aaf.certservice.certification.exceptions.DecryptionException;
import org.onap.aaf.certservice.certification.exceptions.PemDecryptionException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CertificationServiceTest {

    private CertificationService certificationService;

    @Mock
    private CsrModelFactory csrModelFactory;

    @Mock
    private CertificationModelFactory certificationModelFactory;

    @BeforeEach
    void serUp() {
        MockitoAnnotations.initMocks(this);
        certificationService = new CertificationService(csrModelFactory, certificationModelFactory);
    }

    @Test
    void shouldReturnDataAboutCsrBaseOnEncodedParameters() throws DecryptionException {
        // given
        final String testStringCsr = "testData";
        final String testCaName = "TestCa";
        CsrModel mockedCsrModel = mock(CsrModel.class);
        CertificationModel testCertificationModel = new CertificationModel(
                Arrays.asList("ENTITY_CERT", "INTERMEDIATE_CERT"),
                Arrays.asList("CA_CERT", "EXTRA_CA_CERT")
        );
        when(mockedCsrModel.toString()).thenReturn(testStringCsr);
        when(csrModelFactory.createCsrModel(any(StringBase64.class), any(StringBase64.class)))
                .thenReturn(mockedCsrModel);
        when(certificationModelFactory.createCertificationModel(mockedCsrModel, testCaName))
                .thenReturn(testCertificationModel);

        // when
        ResponseEntity<String> testResponse =
                certificationService.signCertificate(testCaName, "encryptedCSR", "encryptedPK");

        CertificationModel responseCertificationModel = new Gson().fromJson(testResponse.getBody(), CertificationModel.class);

        // then
        assertEquals(HttpStatus.OK, testResponse.getStatusCode());
        assertThat(responseCertificationModel
        ).isEqualToComparingFieldByField(testCertificationModel);

    }

    @Test
    void shouldReturnBadRequestWhenCreatingCsrModelFails() throws DecryptionException {
        // given
        when(csrModelFactory.createCsrModel(any(StringBase64.class), any(StringBase64.class)))
                .thenThrow(new CsrDecryptionException("CSR creation fail",new IOException()));

        // when
        ResponseEntity<String> testResponse =
                certificationService.signCertificate("TestCa", "encryptedCSR", "encryptedPK");

        String expectedMessage = "Wrong certificate signing request (CSR) format";

        // then
        assertEquals(HttpStatus.BAD_REQUEST, testResponse.getStatusCode());
        assertTrue(
                testResponse.toString().contains(expectedMessage)
        );

    }

    @Test
    void shouldReturnBadRequestWhenCreatingPemModelFails() throws DecryptionException {
        // given
        when(csrModelFactory.createCsrModel(any(StringBase64.class), any(StringBase64.class)))
                .thenThrow(new PemDecryptionException("PEM creation fail",new IOException()));

        // when
        ResponseEntity<String> testResponse =
                certificationService.signCertificate("TestCa", "encryptedCSR", "encryptedPK");

        String expectedMessage = "Wrong key (PK) format";

        // then
        assertEquals(HttpStatus.BAD_REQUEST, testResponse.getStatusCode());
        assertTrue(
                testResponse.toString().contains(expectedMessage)
        );

    }

}
