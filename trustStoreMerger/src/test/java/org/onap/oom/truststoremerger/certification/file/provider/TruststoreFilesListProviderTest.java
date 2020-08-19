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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.certification.file.JksTruststore;
import org.onap.oom.truststoremerger.certification.file.P12Truststore;
import org.onap.oom.truststoremerger.certification.file.PemTruststore;
import org.onap.oom.truststoremerger.certification.file.TruststoreFile;
import org.onap.oom.truststoremerger.certification.file.TruststoreFileWithPassword;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TruststoreFilesListProviderTest {

    private static final String TRUSTSTORE_JKS_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASS_PATH = "src/test/resources/truststore-jks.pass";
    private static final String TRUSTSTORE_JKS_PASS = "EOyuFbuYDyq_EhpboM72RHua";
    private static final String TRUSTSTORE_P12_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASS_PATH = "src/test/resources/truststore-p12.pass";
    private static final String TRUSTSTORE_P12_PASS = "88y9v5D8H3SG6bZWRVHDfOAo";
    private static final String TRUSTSTORE_PEM_PATH = "src/test/resources/truststore.pem";
    private static final String EMPTY_PASS_PATH = "";

    private TruststoreFilesListProvider truststoreFilesListProvider;

    @BeforeEach
    void setUp() {
        TruststoreFileFactory truststoreFileFactory = new TruststoreFileFactory(new FileManager(), new PasswordReader());
        truststoreFilesListProvider = new TruststoreFilesListProvider(truststoreFileFactory);
    }

    @Test
    void shouldReturnTruststoreFilesList()
        throws TruststoreFileFactoryException, PasswordReaderException, LoadTruststoreException, KeystoreInstanceException {
        List<String> truststorePaths = Arrays.asList(TRUSTSTORE_JKS_PATH, TRUSTSTORE_P12_PATH, TRUSTSTORE_PEM_PATH);
        List<String> truststorePasswordPaths = Arrays.asList(TRUSTSTORE_JKS_PASS_PATH, TRUSTSTORE_P12_PASS_PATH, EMPTY_PASS_PATH);
        List<TruststoreFile> truststoreFilesList = truststoreFilesListProvider.getTruststoreFilesList(truststorePaths, truststorePasswordPaths);
        assertThat(truststoreFilesList.size()).isEqualTo(3);
        assertCorrectJksTruststore(truststoreFilesList.get(0), TRUSTSTORE_JKS_PATH, TRUSTSTORE_JKS_PASS);
        assertCorrectP12Truststore(truststoreFilesList.get(1), TRUSTSTORE_P12_PATH, TRUSTSTORE_P12_PASS);
        assertCorrectPemTruststore(truststoreFilesList.get(2), TRUSTSTORE_PEM_PATH);
    }

    private void assertCorrectJksTruststore(TruststoreFile truststoreFile, String truststorePath, String truststorePass) {
        assertCorrectTypeAndTruststorePath(truststoreFile, truststorePath, JksTruststore.class);
        assertContainsCorrectPassword(truststoreFile, truststorePass);
    }

    private void assertCorrectP12Truststore(TruststoreFile truststoreFile, String truststorePath, String truststorePass) {
        assertCorrectTypeAndTruststorePath(truststoreFile, truststorePath, P12Truststore.class);
        assertContainsCorrectPassword(truststoreFile, truststorePass);
    }

    private void assertCorrectPemTruststore(TruststoreFile truststoreFile, String truststorePath) {
        assertCorrectTypeAndTruststorePath(truststoreFile, truststorePath, PemTruststore.class);
    }

    private void assertCorrectTypeAndTruststorePath(TruststoreFile truststoreFile, String truststorePath, Class<?> truststoreType) {
        assertThat(truststoreFile).isInstanceOf(truststoreType);
        assertThat(truststoreFile.getTruststoreFile()).isEqualTo(new File(truststorePath));
    }

    private void assertContainsCorrectPassword(TruststoreFile truststoreFile, String truststorePass) {
        TruststoreFileWithPassword truststoreFileWithPassword = (TruststoreFileWithPassword) truststoreFile;
        assertThat(truststoreFileWithPassword.getPassword()).isEqualTo(truststorePass);
    }
}
