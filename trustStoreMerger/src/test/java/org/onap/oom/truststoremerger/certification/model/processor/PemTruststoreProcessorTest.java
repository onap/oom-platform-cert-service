/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.truststoremerger.certification.model.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.common.TestCertificateProvider;
import org.onap.oom.truststoremerger.certification.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.model.Truststore;
import org.onap.oom.truststoremerger.certification.model.processor.PemTruststoreProcessor;
import org.onap.oom.truststoremerger.certification.model.entry.CertificateWithAlias;

class PemTruststoreProcessorTest {

    private static final int EXPECTED_ONE = 1;

    @Test
    void getNotEmptyCertificateListShouldThrowExceptionWhenFileNotContainsCertificate() {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemFile();
        PemTruststoreProcessor pemCertificateController = new PemTruststoreProcessor(emptyPemFile);
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(pemCertificateController::getNotEmptyCertificateList);
    }

    @Test
    void transformToStringInPemFormatShouldCorrectlyTransform() throws ExitableException, IOException {
        //given
        Truststore pemTruststore = TestCertificateProvider.getSamplePemTruststoreFile();
        List<CertificateWithAlias> wrappedCertificates = pemTruststore.getCertificates();
        List<Certificate> certificateList = unWrapCertificate(wrappedCertificates);
        File notEmptyPemFile = TestCertificateProvider.getNotEmptyPemFile();
        PemTruststoreProcessor pemCertificateController = new PemTruststoreProcessor(notEmptyPemFile);

        //when
        String certificateTransformed = pemCertificateController.transformToStringInPemFormat(certificateList);

        //then
        String expected = TestCertificateProvider.getExpectedPemCertificateAsString();
        assertThat(certificateTransformed).isEqualTo(expected);
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnTrueIfFileNotContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemFile();
        PemTruststoreProcessor pemCertificateController = new PemTruststoreProcessor(emptyPemFile);
        //when//then
        assertThat(pemCertificateController.isFileWithoutPemCertificate()).isTrue();
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnFalseIfFileContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File notEmptyPemFile = TestCertificateProvider.getNotEmptyPemFile();
        PemTruststoreProcessor pemCertificateController = new PemTruststoreProcessor(notEmptyPemFile);

        //when//then
        assertThat(pemCertificateController.isFileWithoutPemCertificate()).isFalse();
    }

    @Test
    void privateKeyIsSkippedWhileReadingCertificates() throws ExitableException {
        //given
        File pemTruststoreFile = TestCertificateProvider.getPemWithPrivateKeyFile();
        PemTruststoreProcessor pemCertificateController = new PemTruststoreProcessor(pemTruststoreFile);

        //when
        List<CertificateWithAlias> certificate = pemCertificateController.getNotEmptyCertificateList();

        //then
        assertThat(certificate).hasSize(EXPECTED_ONE);
    }

    private List<Certificate> unWrapCertificate(List<CertificateWithAlias> certificateWithAliases) {
        return certificateWithAliases
            .stream()
            .map(CertificateWithAlias::getCertificate)
            .collect(Collectors.toList());
    }

}
