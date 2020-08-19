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

package org.onap.oom.truststoremerger.certification.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAliasFactory;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;

class PemTruststoreTest {

    public static final String X_509_CERTIFICATE = "X.509";
    public static final int EXPECTED_ONE = 1;
    public static final int EXPECTED_THREE = 3;
    public static final int FIRST_ELEMENT = 0;

    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();

    @Test
    void pemTruststoreShouldReadCertificatesFromFile() throws ExitableException {

        //given
        PemTruststore pemTruststore = TestCertificateProvider.getSamplePemTruststoreFile();

        //when
        List<CertificateWithAlias> certificates = pemTruststore.getCertificates();
        Certificate certificate = certificates.get(FIRST_ELEMENT).getCertificate();
        //then

        assertThat(certificates).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void pemTruststoreShouldAddDifferentCertificates() throws IOException, ExitableException {

        //given
        PemTruststore tmpPemTruststoreFile = TestCertificateProvider.createTmpPemTruststoreFile();
        List<CertificateWithAlias> jksTruststoreCertificates = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias().getCertificates();
        List<CertificateWithAlias> p12TruststoreCertificates = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();

        //when
        tmpPemTruststoreFile.addCertificate(jksTruststoreCertificates);
        tmpPemTruststoreFile.addCertificate(p12TruststoreCertificates);
        tmpPemTruststoreFile.saveFile();

        PemTruststore tmpPemTruststoreSaved = TestCertificateProvider.getTmpPemTruststoreFile();
        List<CertificateWithAlias> addedCertificates = tmpPemTruststoreSaved.getCertificates();
        Certificate certificate = addedCertificates.get(FIRST_ELEMENT).getCertificate();

        //then
        assertThat(addedCertificates).hasSize(EXPECTED_THREE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);

    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() throws IOException, ExitableException {
        //given
        PemTruststore tmpPemTruststoreFile = TestCertificateProvider.createTmpPemTruststoreFile();
        List<CertificateWithAlias> pemTruststoreCertificates =
            TestCertificateProvider.getSamplePemTruststoreFile().getCertificates();
        //when
        tmpPemTruststoreFile.addCertificate(pemTruststoreCertificates);
        tmpPemTruststoreFile.getTruststoreFile().setWritable(false);
        //then
        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(tmpPemTruststoreFile::saveFile);

    }

    @Test
    void shouldThrowExceptionWhenFileNotContainsCertificate() throws IOException {
        //given
        PemTruststore tmpPemTruststoreFile = TestCertificateProvider.createEmptyTmpPemTruststoreFile();
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(tmpPemTruststoreFile::getCertificates);
    }

    @Test
    void shouldThrowExceptionWhenCannotConvertCertificateToPem() throws Exception {
        //given
        PemTruststore pemTruststore = TestCertificateProvider.createTmpPemTruststoreFile();
        Certificate certificate = mock(Certificate.class);

        when(certificate.getEncoded()).thenThrow(new CertificateEncodingException());

        List<CertificateWithAlias> certificatesWithAliases = new ArrayList<>();
        certificatesWithAliases.add(factory.createPemCertificate(certificate));
        pemTruststore.addCertificate(certificatesWithAliases);

        //when //then
        assertThatExceptionOfType(TruststoreDataOperationException.class)
            .isThrownBy(pemTruststore::saveFile);
    }

    @AfterAll
    static void removeTemporaryFiles() throws IOException {
        TestCertificateProvider.removeTemporaryFiles();
    }

}
