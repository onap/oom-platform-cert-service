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

package org.onap.oom.truststoremerger.certification.file.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.certification.file.model.Truststore;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;

class PemCertificateManipulatorTest {

    private static final int EXPECTED_ONE = 1;

    @Test
    void getCertificatesShouldThrowExceptionWhenFileNotContainsCertificate() {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemFile();
        PemCertificateManipulator pemCertificateManipulator = new PemCertificateManipulator(emptyPemFile);
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(pemCertificateManipulator::getCertificates);
    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() {
        //given
        KeyStoreSpi keyStoreSpi = mock(KeyStoreSpi.class);
        KeyStore keyStore = new KeyStore(keyStoreSpi, null, "") {
        };
        File pemFile = TestCertificateProvider.getEmptyPemFile();
        pemFile.setWritable(false);
        PemCertificateManipulator pemManipulator = new PemCertificateManipulator(pemFile);

        //when. then
        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(pemManipulator::saveFile);
    }

    @Test
    void transformToStringInPemFormatShouldCorrectlyTransform() throws ExitableException, IOException {
        //given
        Truststore pemTruststore = TestCertificateProvider.getSamplePemTruststoreFile();
        List<CertificateWithAlias> wrappedCertificates = pemTruststore.getCertificates();
        List<Certificate> certificateList = unWrapCertificate(wrappedCertificates);
        File notEmptyPemFile = TestCertificateProvider.getNotEmptyPemFile();
        PemCertificateManipulator pemCertificateManipulator = new PemCertificateManipulator(notEmptyPemFile);

        //when
        String certificateTransformed = pemCertificateManipulator.transformToStringInPemFormat(certificateList);

        //then
        String expected = TestCertificateProvider.getExpectedPemCertificateAsString();
        assertThat(certificateTransformed).isEqualTo(expected);
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnTrueIfFileNotContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemFile();
        PemCertificateManipulator pemCertificateManipulator = new PemCertificateManipulator(emptyPemFile);
        //when//then
        assertThat(pemCertificateManipulator.isFileWithoutPemCertificate()).isTrue();
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnFalseIfFileContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File notEmptyPemFile = TestCertificateProvider.getNotEmptyPemFile();
        PemCertificateManipulator pemCertificateManipulator = new PemCertificateManipulator(notEmptyPemFile);

        //when//then
        assertThat(pemCertificateManipulator.isFileWithoutPemCertificate()).isFalse();
    }

    @Test
    void privateKeyIsSkippedWhileReadingCertificates() throws ExitableException {
        //given
        File pemTruststoreFile = TestCertificateProvider.getPemWithPrivateKeyFile();
        PemCertificateManipulator pemCertificateManipulator = new PemCertificateManipulator(pemTruststoreFile);

        //when
        List<CertificateWithAlias> certificate = pemCertificateManipulator.getCertificates();

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
