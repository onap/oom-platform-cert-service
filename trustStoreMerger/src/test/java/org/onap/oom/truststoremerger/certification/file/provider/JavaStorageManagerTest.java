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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;

@ExtendWith(MockitoExtension.class)
public class JavaStorageManagerTest {

    private KeyStore keyStore;
    @Mock
    private KeyStoreSpi keyStoreSpi;

    @BeforeEach
    void setUp() {
        keyStore = new KeyStore(keyStoreSpi, null, "") {
        };
    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() throws ExitableException {
        //given
        File jksFile = TestCertificateProvider.getSampleJksFile();
        String jksTruststorePassword = "";
        jksFile.setWritable(false);
        JavaStorageManager javaStorageManager = JavaStorageManager
            .createAndLoadFile(keyStore, jksFile, jksTruststorePassword);

        //when, then
        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(javaStorageManager::saveFile);
    }

}
