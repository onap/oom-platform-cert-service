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

package org.onap.oom.truststoremerger.certification.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.api.CertificateConstants.X_509_CERTIFICATE;
import static org.onap.oom.truststoremerger.certification.common.TestCertificateProvider.PEM_BACKUP_FILE_PATH;
import static org.onap.oom.truststoremerger.certification.common.TestCertificateProvider.PEM_FILE_PATH;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.common.TestCertificateProvider;
import org.onap.oom.truststoremerger.certification.exception.CreateBackupException;
import org.onap.oom.truststoremerger.certification.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.certification.model.certificate.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.model.certificate.CertificateWithAliasFactory;

class TruststoreTest {

    private static final String BACKUP_EXTENSION = ".bak";

    private static final int EXPECTED_ONE = 1;
    public static final int EXPECTED_THREE = 3;
    public static final int FIRST_ELEMENT = 0;

    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();

    @Test
    void createBackupShouldCreateFileWithExtension() throws CreateBackupException {
        //given
        File pemFile = new File(PEM_FILE_PATH);
        Truststore truststore = new Truststore(new PemTruststore(pemFile));
        //when
        truststore.createBackup();

        //then
        File backupFile = new File(PEM_BACKUP_FILE_PATH);
        assertThat(backupFile.getName().endsWith(BACKUP_EXTENSION)).isTrue();
        assertThat(backupFile.isFile()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("truststoreProvider")
    void truststoreShouldReadCertificatesFromFile(Truststore truststore) throws ExitableException {
        //when
        List<CertificateWithAlias> certificates = truststore.getCertificates();
        Certificate certificate = certificates.get(FIRST_ELEMENT).getCertificate();

        //then
        assertThat(certificates).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void jksTruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        Truststore jksTruststore = TestCertificateProvider.createTmpJksTruststoreFileWithUniqAlias();
        List<CertificateWithAlias> p12certificates = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();
        List<CertificateWithAlias> pemCertificates = TestCertificateProvider.getSamplePemTruststore()
            .getCertificates();

        //when
        jksTruststore.addCertificate(p12certificates);
        jksTruststore.addCertificate(pemCertificates);
        jksTruststore.saveFile();

        //then
        assertThat(jksTruststore.getCertificates()).hasSize(EXPECTED_THREE);
    }

    @Test
    void p12TruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        Truststore p12Truststore = TestCertificateProvider.createTmpP12Truststore();
        List<CertificateWithAlias> jksCertificates = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWithAlias> pemCertificates = TestCertificateProvider.getSamplePemTruststore()
            .getCertificates();

        //when
        p12Truststore.addCertificate(jksCertificates);
        p12Truststore.addCertificate(pemCertificates);
        p12Truststore.saveFile();

        //then
        assertThat(p12Truststore.getCertificates()).hasSize(EXPECTED_THREE);
    }

    @Test
    void pemTruststoreShouldAddDifferentCertificates() throws IOException, ExitableException {
        //given
        Truststore pemTruststore = TestCertificateProvider.createTmpPemTruststore();
        List<CertificateWithAlias> jksCertificates = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWithAlias> p12TruststoreCertificates = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();

        //when
        pemTruststore.addCertificate(jksCertificates);
        pemTruststore.addCertificate(p12TruststoreCertificates);
        pemTruststore.saveFile();

        //then
        List<CertificateWithAlias> addedCertificates = pemTruststore.getCertificates();
        Certificate certificate = addedCertificates.get(FIRST_ELEMENT).getCertificate();

        assertThat(pemTruststore.getCertificates()).hasSize(EXPECTED_THREE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() throws IOException, ExitableException {
        //given
        Truststore tmpPemTruststoreFile = TestCertificateProvider.createTmpPemTruststore();
        List<CertificateWithAlias> pemTruststoreCertificates =
            TestCertificateProvider.getSamplePemTruststore().getCertificates();
        //when
        tmpPemTruststoreFile.addCertificate(pemTruststoreCertificates);
        tmpPemTruststoreFile.getStoreFile().setWritable(false);
        //then
        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(tmpPemTruststoreFile::saveFile);

    }

    @Test
    void shouldThrowExceptionWhenFileNotContainsCertificate() throws IOException {
        //given
        Truststore tmpPemTruststoreFile = TestCertificateProvider.createEmptyTmpPemTruststore();
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(tmpPemTruststoreFile::getCertificates);
    }

    @Test
    void shouldThrowExceptionWhenCannotConvertCertificateToPem() throws Exception {
        //given
        Truststore pemTruststore = TestCertificateProvider.createTmpPemTruststore();
        Certificate certificate = mock(Certificate.class);

        when(certificate.getEncoded()).thenThrow(new CertificateEncodingException());

        List<CertificateWithAlias> certificatesWithAliases = new ArrayList<>();
        certificatesWithAliases.add(factory.createPemCertificate(certificate));
        pemTruststore.addCertificate(certificatesWithAliases);

        //when //then
        assertThatExceptionOfType(TruststoreDataOperationException.class)
            .isThrownBy(pemTruststore::saveFile);
    }

    @AfterEach
    void removeTemporaryFiles() throws IOException {
        TestCertificateProvider.removeTemporaryFiles();
    }

    private static Stream<Arguments> truststoreProvider() throws LoadTruststoreException, KeystoreInstanceException {
        return Stream.of(
            Arguments.of(TestCertificateProvider.getSampleJksTruststore()),
            Arguments.of(TestCertificateProvider.getSampleP12Truststore()),
            Arguments.of(TestCertificateProvider.getSamplePemTruststore())
        );
    }

}
