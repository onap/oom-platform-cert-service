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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.certification.file.JavaTruststore;
import org.onap.oom.truststoremerger.certification.file.PemTruststore;
import org.onap.oom.truststoremerger.certification.file.TruststoreFile;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;

import java.io.File;
import org.onap.oom.truststoremerger.certification.file.exception.PasswordReaderException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreFileFactoryException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class TruststoreFileFactoryTest {

    private static final String TRUSTSTORE_JKS_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASS_PATH = "src/test/resources/truststore-jks.pass";
    private static final String TRUSTSTORE_JKS_PASS = "EOyuFbuYDyq_EhpboM72RHua";
    private static final String TRUSTSTORE_P12_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASS_PATH = "src/test/resources/truststore-p12.pass";
    private static final String TRUSTSTORE_P12_PASS = "88y9v5D8H3SG6bZWRVHDfOAo";
    private static final String TRUSTSTORE_PEM_PATH = "src/test/resources/truststore.pem";
    private static final String EMPTY_PASS_PATH = "";
    private static final String TRUSTSTORE_UNKNOWN_EXTENSION_PATH = "src/test/resources/truststore-jks.unknown";
    private static final String NON_EXISTING_TRUSTSTORE_PATH = "src/test/resources/non-existing-truststore.jks";

    private TruststoreFileFactory truststoreFileFactory;

    @BeforeEach
    void setUp() {
        truststoreFileFactory = new TruststoreFileFactory(new FileManager(), new PasswordReader());
    }

    @Test
    void shouldReturnCorrectJksTruststoreForJksFile()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        TruststoreFile truststore = truststoreFileFactory
                .create(TRUSTSTORE_JKS_PATH, TRUSTSTORE_JKS_PASS_PATH);
        assertThat(truststore).isInstanceOf(JavaTruststore.class);
        JavaTruststore jksTruststore = (JavaTruststore) truststore;
        assertThat(jksTruststore.getPassword()).isEqualTo(TRUSTSTORE_JKS_PASS);
        assertThat(jksTruststore.getTruststoreFile()).isEqualTo(new File(TRUSTSTORE_JKS_PATH));
    }

    @Test
    void shouldReturnCorrectP12TruststoreForP12File()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        TruststoreFile truststore = truststoreFileFactory
                .create(TRUSTSTORE_P12_PATH,
                        TRUSTSTORE_P12_PASS_PATH);
        assertThat(truststore).isInstanceOf(JavaTruststore.class);
        JavaTruststore jksTruststore = (JavaTruststore) truststore;
        assertThat(jksTruststore.getPassword()).isEqualTo(TRUSTSTORE_P12_PASS);
    }

    @Test
    void shouldReturnCorrectPemTruststoreForPemFile()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        TruststoreFile truststore = truststoreFileFactory
                .create(TRUSTSTORE_PEM_PATH,
                        EMPTY_PASS_PATH);
        assertThat(truststore).isInstanceOf(PemTruststore.class);
    }

    @Test
    void shouldThrowExceptionForInvalidP12PassPath() {
        assertThatExceptionOfType(PasswordReaderException.class).isThrownBy(
                () -> truststoreFileFactory.create(TRUSTSTORE_P12_PATH, EMPTY_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForInvalidJksPassPath() {
        assertThatExceptionOfType(PasswordReaderException.class).isThrownBy(
                () -> truststoreFileFactory.create(TRUSTSTORE_JKS_PATH, EMPTY_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForUnknownTruststoreExtension() {
        assertThatExceptionOfType(TruststoreFileFactoryException.class).isThrownBy(
                () -> truststoreFileFactory.create(TRUSTSTORE_UNKNOWN_EXTENSION_PATH, TRUSTSTORE_JKS_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForNonExistingTruststoreFile() {
        assertThatExceptionOfType(TruststoreFileFactoryException.class).isThrownBy(
                () -> truststoreFileFactory.create(NON_EXISTING_TRUSTSTORE_PATH, TRUSTSTORE_JKS_PASS_PATH)
        );
    }

}
