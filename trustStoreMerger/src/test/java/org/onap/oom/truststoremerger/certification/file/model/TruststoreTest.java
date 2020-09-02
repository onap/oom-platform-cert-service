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

package org.onap.oom.truststoremerger.certification.file.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.api.CertificateConstants.X_509_CERTIFICATE;
import static org.onap.oom.truststoremerger.certification.file.provider.TestCertificateProvider.PEM_BACKUP_FILE_PATH;
import static org.onap.oom.truststoremerger.certification.file.provider.TestCertificateProvider.PEM_FILE_PATH;

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
import org.onap.oom.truststoremerger.certification.file.exception.CreateBackupException;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.provider.PemCertificateManipulator;
import org.onap.oom.truststoremerger.certification.file.provider.TestCertificateProvider;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAliasFactory;

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
        Truststore truststore = new Truststore(pemFile, new PemCertificateManipulator(pemFile));
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
        List<CertificateWithAlias> certificateFromP12 = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();
        List<CertificateWithAlias> certificateFromPem = TestCertificateProvider.getSamplePemTruststoreFile()
            .getCertificates();

        //when
        jksTruststore.addCertificate(certificateFromP12);
        jksTruststore.addCertificate(certificateFromPem);
        jksTruststore.saveFile();

        //then
        assertThat(jksTruststore.getCertificates()).hasSize(EXPECTED_THREE);
    }

    @Test
    void p12TruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        Truststore p12Truststore = TestCertificateProvider.createTmpP12TruststoreFile();
        List<CertificateWithAlias> certificateFromJks = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWithAlias> certificateFromPem = TestCertificateProvider.getSamplePemTruststoreFile()
            .getCertificates();

        //when
        p12Truststore.addCertificate(certificateFromJks);
        p12Truststore.addCertificate(certificateFromPem);
        p12Truststore.saveFile();

        //then
        assertThat(p12Truststore.getCertificates()).hasSize(EXPECTED_THREE);
    }

    @Test
    void pemTruststoreShouldAddDifferentCertificates() throws IOException, ExitableException {
        //given
        Truststore pemTruststore = TestCertificateProvider.createTmpPemTruststoreFile();
        List<CertificateWithAlias> certificateFromJks = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWithAlias> certificateFromP12 = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();

        //when
        pemTruststore.addCertificate(certificateFromJks);
        pemTruststore.addCertificate(certificateFromP12);
        pemTruststore.saveFile();

        //then
        List<CertificateWithAlias> addedCertificates = pemTruststore.getCertificates();
        Certificate certificate = addedCertificates.get(FIRST_ELEMENT).getCertificate();

        assertThat(pemTruststore.getCertificates()).hasSize(EXPECTED_THREE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void shouldThrowExceptionWhenFileNotContainsCertificate() throws IOException {
        //given
        Truststore tmpPemTruststoreFile = TestCertificateProvider.createEmptyTmpPemTruststoreFile();
        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(tmpPemTruststoreFile::getCertificates);
    }

    @Test
    void shouldThrowExceptionWhenCannotConvertCertificateToPem() throws Exception {
        //given
        Truststore pemTruststore = TestCertificateProvider.createTmpPemTruststoreFile();
        Certificate certificate = mock(Certificate.class);

        when(certificate.getEncoded()).thenThrow(new CertificateEncodingException());

        List<CertificateWithAlias> certificateFromPem = new ArrayList<>();
        certificateFromPem.add(factory.createPemCertificate(certificate));
        pemTruststore.addCertificate(certificateFromPem);

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
            Arguments.of(TestCertificateProvider.getSampleJksTruststoreFile()),
            Arguments.of(TestCertificateProvider.getSampleP12Truststore()),
            Arguments.of(TestCertificateProvider.getSamplePemTruststoreFile())
        );
    }

}
