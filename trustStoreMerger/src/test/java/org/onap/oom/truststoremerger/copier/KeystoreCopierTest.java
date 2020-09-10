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

package org.onap.oom.truststoremerger.copier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onap.oom.truststoremerger.api.ExitStatus;
import org.onap.oom.truststoremerger.common.FileTools;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.copier.exception.KeystoreFileCopyException;
import org.onap.oom.truststoremerger.copier.exception.KeystoreNotExistException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KeystoreCopierTest {

    @TempDir
    File dir;

    KeystoreCopier copier = new KeystoreCopier(new FileTools());

    @Test
    void shouldCopyFileAndCreateBackup() throws IOException {
        File source = createFile("source.p12", "source content");
        File destination = createFile("destination.p12", "destination content");
        File backup = declareFile("destination.p12.bak");

        copier.copyKeystores(createConfiguration(source, destination));

        assertThat(readFile(source)).isEqualTo(readFile(destination));
        assertThat(backup.exists()).isTrue();
        assertThat(readFile(backup)).isEqualTo("destination content");
    }

    @Test
    void shouldCopyFileWithoutCreatingBackup() throws IOException {
        File source = createFile("source.p12", "source content");
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");

        copier.copyKeystores(createConfiguration(source, destination));

        assertThat(destination.exists()).isTrue();
        assertThat(readFile(source)).isEqualTo(readFile(destination));
        assertThat(backup.exists()).isFalse();
    }

    @Test
    void shouldThrowKeystoreNotExistException() throws IOException {
        File source = declareFile("source.p12");
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");

        KeystoreNotExistException exception = assertThrows(KeystoreNotExistException.class, () -> {
            copier.copyKeystores(createConfiguration(source, destination));
        });
        assertThat(exception.applicationExitStatus()).isEqualTo(ExitStatus.KEYSTORE_NOT_EXIST_EXCEPTION);

        assertThat(source.exists()).isFalse();
        assertThat(destination.exists()).isFalse();
        assertThat(backup.exists()).isFalse();
    }

    @Test
    void shouldThrowKeystoreFileCopyException() throws IOException {
        File source = createFile("source.p12", "source content");
        source.setReadable(false);
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");

        KeystoreFileCopyException exception = assertThrows(KeystoreFileCopyException.class, () -> {
            copier.copyKeystores(createConfiguration(source, destination));
        });
        assertThat(exception.applicationExitStatus()).isEqualTo(ExitStatus.KEYSTORE_FILE_COPY_EXCEPTION);

        assertThat(source.exists()).isTrue();
        assertThat(destination.exists()).isFalse();
        assertThat(backup.exists()).isFalse();
    }

    private AppConfiguration createConfiguration(File source, File destination) {
        return new AppConfiguration(Collections.emptyList(), Collections.emptyList(),
            Collections.singletonList(source.getAbsolutePath()),
            Collections.singletonList(destination.getAbsolutePath()));
    }

    private String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    private File declareFile(String name) throws IOException {
        return new File(dir.getAbsolutePath() + File.pathSeparator + name);
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
