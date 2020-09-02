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
import org.onap.oom.truststoremerger.certification.file.TruststoreFileFactory;
import org.onap.oom.truststoremerger.certification.file.TruststoreFilesListProvider;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.PasswordReaderException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreFileFactoryException;
import org.onap.oom.truststoremerger.certification.file.model.Truststore;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TruststoreFilesListProviderTest {

    private static final String TRUSTSTORE_JKS_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASS_PATH = "src/test/resources/truststore-jks.pass";
    private static final String TRUSTSTORE_P12_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASS_PATH = "src/test/resources/truststore-p12.pass";
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
        List<Truststore> truststoreFilesList = truststoreFilesListProvider.getTruststoreFilesList(truststorePaths, truststorePasswordPaths);
        assertThat(truststoreFilesList.size()).isEqualTo(3);
        assertCorrectJksTruststore(truststoreFilesList.get(0));
        assertCorrectP12Truststore(truststoreFilesList.get(1));
        assertCorrectPemTruststore(truststoreFilesList.get(2));
    }

    private void assertCorrectJksTruststore(Truststore truststore) {
        assertCorrectTypeAndTruststorePath(truststore, TRUSTSTORE_JKS_PATH);
    }

    private void assertCorrectP12Truststore(Truststore truststore) {
        assertCorrectTypeAndTruststorePath(truststore, TRUSTSTORE_P12_PATH);
    }

    private void assertCorrectPemTruststore(Truststore truststore) {
        assertCorrectTypeAndTruststorePath(truststore, TRUSTSTORE_PEM_PATH);
    }

    private void assertCorrectTypeAndTruststorePath(Truststore truststore, String truststorePath) {
        assertThat(truststore).isInstanceOf(Truststore.class);
        assertThat(truststore.getFile()).isEqualTo(new File(truststorePath));
    }

}
