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

package org.onap.oom.certservice.postprocessor.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileToolsTest {

    public static final String BAK_EXTENSION = ".bak";

    @TempDir
    File dir;

    @Test
    void shouldCreateBackupProvidedFile() throws Exception {
        //given
        File fileToBackup = createFile("truststore.pem", "arbitrary content");
        String backupFilePath = fileToBackup.getPath() + BAK_EXTENSION;
        //when
        new FileTools().createBackup(fileToBackup);
        //then
        assertThat(fileToBackup.equals(new File(backupFilePath)));
    }

    @Test
    void shouldCopyFile() throws IOException {
        //given
        File sourceFile = createFile("source.p12", "any content");
        File destinationFile = new File(dir.getAbsolutePath() + "destination.p12");
        //when
        new FileTools().copy(sourceFile, destinationFile);
        //then
        assertThat(sourceFile.equals(destinationFile));
    }


    private File createFile(String name, String content) throws IOException {
        File file = new File(dir.getAbsolutePath() + File.pathSeparator + name);
        if (file.createNewFile()) {
            FileUtils.write(file, content, Charset.defaultCharset());
        } else {
            throw new IllegalStateException("File could not be created: " + file.getAbsolutePath());
        }
        return file;
    }

}
