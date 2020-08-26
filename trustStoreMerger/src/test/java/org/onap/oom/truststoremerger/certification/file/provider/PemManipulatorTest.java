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

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.TestCertificateProvider;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;

class PemManipulatorTest {

    private PemManipulator pemManipulator = new PemManipulator();

    @Test
    void getNotEmptyCertificateListShouldThrowExceptionWhenFileNotContainsCertificate() {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemTruststoreFile().getTruststoreFile();

        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(() -> pemManipulator.getNotEmptyCertificateList(emptyPemFile));
    }

    @Test
    void transformToStringInPemFormatShouldCorrectlyTransform() throws ExitableException, IOException {
        //given
        List<Certificate> certificateList = getSampleCertificatesList();

        //when
        String certificateTransformed = pemManipulator.transformToStringInPemFormat(certificateList);
        String expected = TestCertificateProvider.getExpectedPemCertificateAsString();

        //then
        assertThat(certificateTransformed).isEqualTo(expected);
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnTrueIfFileNotContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemTruststoreFile().getTruststoreFile();

        //when//then
        assertThat(pemManipulator.isFileWithoutPemCertificate(emptyPemFile)).isTrue();
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnFalseIfFileContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File notEmptyPemFile = TestCertificateProvider.getSamplePemTruststoreFile().getTruststoreFile();

        //when//then
        assertThat(pemManipulator.isFileWithoutPemCertificate(notEmptyPemFile)).isFalse();
    }

    private List<Certificate> getSampleCertificatesList() throws ExitableException {
        return TestCertificateProvider.getSamplePemTruststoreFile()
            .getCertificates()
            .stream()
            .map(CertificateWithAlias::getCertificate)
            .collect(Collectors.toList());
    }

}
