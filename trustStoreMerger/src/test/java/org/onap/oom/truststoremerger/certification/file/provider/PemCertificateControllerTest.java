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

import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.model.Truststore;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PemCertificateControllerTest {

    @Test
    void getNotEmptyCertificateListShouldThrowExceptionWhenFileNotContainsCertificate() {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemTruststoreFile().getFile();
        PemCertificateController pemCertificateController = new PemCertificateController(emptyPemFile);
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(pemCertificateController::getNotEmptyCertificateList);
    }

    @Test
    void transformToStringInPemFormatShouldCorrectlyTransform() throws ExitableException, IOException {
        //given
        Truststore pemTruststore = TestCertificateProvider.getSamplePemTruststoreFile();
        List<CertificateWithAlias> wrappedCertificates = pemTruststore.getCertificates();
        File notEmptyPemFile = pemTruststore.getFile();
        List<Certificate> certificateList = unWrapCertificate(wrappedCertificates);
        PemCertificateController pemCertificateController = new PemCertificateController(notEmptyPemFile);
        String expected = TestCertificateProvider.getExpectedPemCertificateAsString();

        //when
        String certificateTransformed = pemCertificateController.transformToStringInPemFormat(certificateList);

        //then
        assertThat(certificateTransformed).isEqualTo(expected);
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnTrueIfFileNotContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File emptyPemFile = TestCertificateProvider.getEmptyPemTruststoreFile().getFile();
        PemCertificateController pemCertificateController = new PemCertificateController(emptyPemFile);
        //when//then
        assertThat(pemCertificateController.isFileWithoutPemCertificate()).isTrue();
    }

    @Test
    void fileNotContainsPemCertificateShouldReturnFalseIfFileContainsCertificate()
        throws TruststoreDataOperationException {
        //given
        File notEmptyPemFile = TestCertificateProvider.getSamplePemTruststoreFile().getFile();
        PemCertificateController pemCertificateController = new PemCertificateController(notEmptyPemFile);

        //when//then
        assertThat(pemCertificateController.isFileWithoutPemCertificate()).isFalse();
    }

    private List<Certificate> unWrapCertificate(List<CertificateWithAlias> certificateWithAliases) {
        return certificateWithAliases
            .stream()
            .map(CertificateWithAlias::getCertificate)
            .collect(Collectors.toList());
    }

}
