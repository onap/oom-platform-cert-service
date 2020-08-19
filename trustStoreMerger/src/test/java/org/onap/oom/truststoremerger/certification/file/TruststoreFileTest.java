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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.certification.file.exception.CreateBackupException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TruststoreFileTest {

    private static final String SAMPLE_PATH = "sample/path";
    private static final String PEM_FILE_PATH = "src/test/resources/truststore.pem";
    private static final String PEM_BACKUP_FILE_PATH = "src/test/resources/truststore.pem.bak";
    private static final String BACKUP_EXTENSION = ".bak";

    @Mock
    private File mockedFile;

    @Test
    void createBackupShouldCreateFileWithExtension() throws CreateBackupException {
        //given
        File pemFile = new File(PEM_FILE_PATH);
        TruststoreFile truststoreFile = new PemTruststore(pemFile);
        //when
        truststoreFile.createBackup();

        //then
        File backupFile = new File(PEM_BACKUP_FILE_PATH);
        assertThat(backupFile.getName().endsWith(BACKUP_EXTENSION)).isTrue();
        assertThat(backupFile.isFile()).isTrue();
    }

    @Test
    void createBackupShouldThrowExceptionWhenCannotCreateBackup() {

        //given
        when(mockedFile.getPath()).thenReturn(SAMPLE_PATH);
        mockedFile.setReadable(false);
        TruststoreFile truststoreFile = new PemTruststore(mockedFile);

        //when //then
        assertThatExceptionOfType(CreateBackupException.class).isThrownBy(truststoreFile::createBackup);
    }


    @AfterAll
    static void removeBackupFile() throws IOException {
        Files.deleteIfExists(Paths.get(PEM_BACKUP_FILE_PATH));
    }
}
