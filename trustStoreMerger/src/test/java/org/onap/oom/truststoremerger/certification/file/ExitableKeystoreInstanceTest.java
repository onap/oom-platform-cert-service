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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.certification.file.CertificatesFileProvider.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;

class ExitableKeystoreInstanceTest {

    private static final String TRUSTSTORE_P12_FILE_PATH = "src/test/resources/truststore-p12.p12";

    @Test
    void getTruststoreCertificatesShouldReturnWrapperedCertificates() {

    }

    @Test
    void addCertificatesShouldAddCertificateToFile() {
    }

    @Test
    void throwException() {
    }

    @Test
    void throwExceptionWhenCannotLoadFile() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        ExitableKeystoreInstance exitableKeystoreInstance = new ExitableKeystoreInstance(keyStore);
        File truststoreFile = getSampleP12File();
//        FileInputStream inputStream = new FileInputStream(truststoreFile);
//        when(keyStore.load(inputStream, CertificatesFileProvider.TRUSTSTORE_P12_PASSWORD.toCharArray()))
//            .thenThrow(new KeyStoreException());
//        doThrow(new KeyStoreException()).when(keyStore.load(inputStream, CertificatesFileProvider.TRUSTSTORE_P12_PASSWORD.toCharArray()))
//        truststoreFile.setReadable(true);
//
//        exitableKeystoreInstance
//            .getTruststoreCertificates(truststoreFile, CertificatesFileProvider.TRUSTSTORE_P12_PASSWORD);

        assertThatExceptionOfType(LoadTruststoreException.class)
            .isThrownBy(() -> exitableKeystoreInstance
                .getTruststoreCertificates(truststoreFile, TRUSTSTORE_P12_PASSWORD));
    }

    @Test
    void throwExceptionWhenCannotSaveFile() throws Exception {
//        KeyStore keyStore = mock(KeyStore.class);
        KeyStore keyStore = spy(KeyStore.getInstance(KEYSTORE_INSTANCE_P12));
        File truststoreFile = getSampleP12File();
        FileInputStream inputStream = new FileInputStream(truststoreFile);
//        when(keyStore.load(inputStream, CertificatesFileProvider.TRUSTSTORE_P12_PASSWORD.toCharArray()))
//            .thenThrow(new KeyStoreException());
        File coppiedFile = getTemporaryP12Truststore().getTruststoreFile();
        FileOutputStream fileOutputStream = new FileOutputStream(coppiedFile);
        keyStore.load(inputStream, TRUSTSTORE_P12_PASSWORD.toCharArray());

        doThrow(new KeyStoreException()).when(keyStore)
            .store(fileOutputStream, TRUSTSTORE_P12_PASSWORD.toCharArray());
        ExitableKeystoreInstance exitableKeystoreInstance = new ExitableKeystoreInstance(keyStore);


        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(() -> exitableKeystoreInstance
                .addCertificates(new ArrayList<>(),coppiedFile,TRUSTSTORE_P12_PASSWORD));
    }

    @Test
    void throwExceptionWhenAliasConflictDetected() {
    }

    @Test
    void throwExceptionWhenCannotAddCertificate() {
    }

    @Test
    void throwExceptionWhenCannotInvestigateAliasConflict() {
    }

    @Test
    void throwExceptionWhenFileNotContainsTruststoreEntry() {
    }

    @Test
    void throwExceptionWhenCannotGetCertificateByAlias() {
    }

    @Test
    void throwExceptionWhenCannotReadTruststoreAliases() {
    }


}
