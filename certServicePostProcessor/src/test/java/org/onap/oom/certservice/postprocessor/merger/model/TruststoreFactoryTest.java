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


package org.onap.oom.certservice.postprocessor.merger.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.postprocessor.merger.exception.KeystoreInstanceException;
import org.onap.oom.certservice.postprocessor.merger.exception.LoadTruststoreException;
import org.onap.oom.certservice.postprocessor.merger.exception.PasswordReaderException;
import org.onap.oom.certservice.postprocessor.merger.exception.TruststoreFileFactoryException;

@ExtendWith(MockitoExtension.class)
class TruststoreFactoryTest {

    private static final String TRUSTSTORE_JKS_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASS_PATH = "src/test/resources/truststore-jks.pass";
    private static final String TRUSTSTORE_P12_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASS_PATH = "src/test/resources/truststore-p12.pass";
    private static final String TRUSTSTORE_PEM_PATH = "src/test/resources/truststore.pem";
    private static final String EMPTY_PASS_PATH = "";
    private static final String TRUSTSTORE_UNKNOWN_EXTENSION_PATH = "src/test/resources/truststore-jks.unknown";
    private static final String NON_EXISTING_TRUSTSTORE_PATH = "src/test/resources/non-existing-truststore.jks";

    @Test
    void shouldReturnCorrectJksTruststoreForJksFile()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        //given, when
        Truststore truststore = TruststoreFactory
            .create(TRUSTSTORE_JKS_PATH, TRUSTSTORE_JKS_PASS_PATH);

        //then
        assertThat(truststore).isInstanceOf(Truststore.class);
    }

    @Test
    void shouldReturnCorrectP12TruststoreForP12File()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        //given, when
        Truststore truststore = TruststoreFactory
            .create(TRUSTSTORE_P12_PATH, TRUSTSTORE_P12_PASS_PATH);

        //then
        assertThat(truststore).isInstanceOf(Truststore.class);
    }

    @Test
    void shouldReturnCorrectPemTruststoreForPemFile()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        //given, when
        Truststore truststore = TruststoreFactory
            .create(TRUSTSTORE_PEM_PATH,
                EMPTY_PASS_PATH);

        //then
        assertThat(truststore).isInstanceOf(Truststore.class);
    }

    @Test
    void shouldThrowExceptionForInvalidP12PassPath() {
        assertThatExceptionOfType(PasswordReaderException.class).isThrownBy(
            () -> TruststoreFactory.create(TRUSTSTORE_P12_PATH, EMPTY_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForInvalidJksPassPath() {
        assertThatExceptionOfType(PasswordReaderException.class).isThrownBy(
            () -> TruststoreFactory.create(TRUSTSTORE_JKS_PATH, EMPTY_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForUnknownTruststoreExtension() {
        assertThatExceptionOfType(TruststoreFileFactoryException.class).isThrownBy(
            () -> TruststoreFactory
                .create(TRUSTSTORE_UNKNOWN_EXTENSION_PATH, TRUSTSTORE_JKS_PASS_PATH)
        );
    }

    @Test
    void shouldThrowExceptionForNonExistingTruststoreFile() {
        assertThatExceptionOfType(TruststoreFileFactoryException.class).isThrownBy(
            () -> TruststoreFactory.create(NON_EXISTING_TRUSTSTORE_PATH, TRUSTSTORE_JKS_PASS_PATH)
        );
    }

}
