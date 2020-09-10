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

package org.onap.oom.truststoremerger.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.merger.exception.CreateBackupException;
import org.onap.oom.truststoremerger.merger.model.TestCertificateProvider;

public class FileToolsTest {

    public static final String BAK_EXTENSION = ".bak";

    @Test
    void shouldCreateBackupProvidedFile() throws CreateBackupException {
        //given
        File fileToBackup = new File(TestCertificateProvider.PEM_FILE_PATH);
        String backupFilePath = fileToBackup.getPath() + BAK_EXTENSION;
        //when
        new FileTools().createBackup(fileToBackup);
        //then
        assertThat(fileToBackup.equals(new File(backupFilePath)));
    }

    @Test
    void shouldCopyFile() throws IOException {
        //given
        File sourceFile = new File(TestCertificateProvider.PEM_FILE_PATH);
        File destinationFile = new File(TestCertificateProvider.PEM_FILE_PATH + ".new");
        //when
        new FileTools().copy(sourceFile, destinationFile);
        //then
        assertThat(sourceFile.equals(destinationFile));
    }


    @AfterEach
    void removeTemporaryFiles() throws IOException {
        TestCertificateProvider.removeTemporaryFiles();
    }

}
