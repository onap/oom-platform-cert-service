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


package org.onap.oom.truststoremerger.certification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.certification.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.PasswordReaderException;
import org.onap.oom.truststoremerger.certification.exception.TruststoreFileFactoryException;
import org.onap.oom.truststoremerger.certification.model.Truststore;
import org.onap.oom.truststoremerger.certification.model.TruststoreFactory;

@ExtendWith(MockitoExtension.class)
class TruststoreFilesListProviderTest {

    private static final String TRUSTSTORE_JKS_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASS_PATH = "src/test/resources/truststore-jks.pass";
    private static final String TRUSTSTORE_P12_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASS_PATH = "src/test/resources/truststore-p12.pass";
    private static final String TRUSTSTORE_PEM_PATH = "src/test/resources/truststore.pem";
    private static final String EMPTY_PASS_PATH = "";

    private TruststoreFilesListProvider truststoreFilesListProvider;

    @Mock
    TruststoreFactory truststoreFactory;

    @BeforeEach
    void setUp()
        throws LoadTruststoreException, PasswordReaderException, TruststoreFileFactoryException, KeystoreInstanceException {
        truststoreFilesListProvider = new TruststoreFilesListProvider(truststoreFactory);
        when(truststoreFactory.create(Mockito.any(), Mockito.any())).thenReturn(new Truststore(null));
    }

    @Test
    void shouldReturnTruststoreFilesList()
        throws TruststoreFileFactoryException, PasswordReaderException, LoadTruststoreException, KeystoreInstanceException {
        //given
        List<String> truststorePaths = Arrays.asList(TRUSTSTORE_JKS_PATH, TRUSTSTORE_P12_PATH, TRUSTSTORE_PEM_PATH);
        List<String> truststorePasswordPaths = Arrays
            .asList(TRUSTSTORE_JKS_PASS_PATH, TRUSTSTORE_P12_PASS_PATH, EMPTY_PASS_PATH);

        //when
        List<Truststore> truststoreFilesList = truststoreFilesListProvider
            .getTruststoreFilesList(truststorePaths, truststorePasswordPaths);

        //then
        assertThat(truststoreFilesList.size()).isEqualTo(3);
        verify(truststoreFactory).create(TRUSTSTORE_JKS_PATH, TRUSTSTORE_JKS_PASS_PATH);
        verify(truststoreFactory).create(TRUSTSTORE_P12_PATH, TRUSTSTORE_P12_PASS_PATH);
        verify(truststoreFactory).create(TRUSTSTORE_PEM_PATH, EMPTY_PASS_PATH);
    }

}
