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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;
import org.onap.oom.truststoremerger.certification.entry.PemCertificateWrapper;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;

class PemTruststoreTest {

    public static final String X_509_CERTIFICATE = "X.509";
    public static final int EXPECTED_ONE = 1;
    public static final int FIRST_ELEMENT = 0;

    @Test
    void pemTruststoreShouldReadCertificatesFromFile() throws ExitableException {

        //given
        PemTruststore pemTruststore = CertificatesTestFileManager.getSamplePemTruststoreFile();

        //when
        List<CertificateWrapper> certificates = pemTruststore.getCertificates();
        Certificate certificate = certificates.get(FIRST_ELEMENT).getCertificate();
        //then

        assertThat(certificates).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void pemTruststoreShouldAddDifferentCertificates() throws IOException, ExitableException {

        //given
        PemTruststore tmpPemTruststoreFile = CertificatesTestFileManager.createEmptyTmpPemTruststoreFile();
        PemTruststore pemTruststoreWithCert = CertificatesTestFileManager.getSamplePemTruststoreFile();

        //when
        tmpPemTruststoreFile.addCertificate(pemTruststoreWithCert.getCertificates());
        List<CertificateWrapper> addedCertificates = tmpPemTruststoreFile.getCertificates();
        Certificate certificate = addedCertificates.get(FIRST_ELEMENT).getCertificate();

        //then
        assertThat(addedCertificates).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);

    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() throws IOException, ExitableException {
        PemTruststore tmpPemTruststoreFile = CertificatesTestFileManager.createEmptyTmpPemTruststoreFile();
        List<CertificateWrapper> pemTruststoreCertificates =
            CertificatesTestFileManager.getSamplePemTruststoreFile().getCertificates();

        tmpPemTruststoreFile.getTruststoreFile().setWritable(false);

        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(() -> tmpPemTruststoreFile.addCertificate(pemTruststoreCertificates));

    }

    @Test
    void shouldThrowExceptionWhenFileNotContainsCertificate() throws IOException {
        PemTruststore tmpPemTruststoreFile = CertificatesTestFileManager.createEmptyTmpPemTruststoreFile();

        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(tmpPemTruststoreFile::getCertificates);
    }

    @Test
    void shouldThrowExceptionWhenCannotConvertCertificateToPem() throws Exception {
        //given
        File file = mock(File.class);
        PemTruststore pemTruststore = spy(new PemTruststore(file));
        Certificate certificate = mock(Certificate.class);

        when(certificate.getEncoded()).thenThrow(new CertificateEncodingException());

        List<CertificateWrapper> certificateWrappers = new ArrayList<>();
        certificateWrappers.add(new PemCertificateWrapper(certificate));

        //when //then
        assertThatExceptionOfType(TruststoreDataOperationException.class)
            .isThrownBy(() -> pemTruststore.addCertificate(certificateWrappers));
    }

    @AfterAll
    static void removeTemporaryFiles() throws IOException {
        CertificatesTestFileManager.removeTemporaryFiles();
    }

}
