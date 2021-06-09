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

package org.onap.oom.certservice.postprocessor.copier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onap.oom.certservice.postprocessor.common.FileTools;
import org.onap.oom.certservice.postprocessor.configuration.model.AppConfiguration;
import org.onap.oom.certservice.postprocessor.copier.exception.KeystoreFileCopyException;
import org.onap.oom.certservice.postprocessor.copier.exception.KeystoreNotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class KeystoreCopierTest {

    private static final String SOURCE_CONTENT = "source content";
    private static final String DESTINATION_CONTENT = "destination content";

    @TempDir
    File dir;

    private KeystoreCopier copier = new KeystoreCopier(new FileTools());

    @Test
    void shouldDoNothingForEmptySourceFileList() {
        AppConfiguration configuration = createEmptyConfiguration();

        copier.copyKeystores(configuration);

        assertThat(dir).isEmptyDirectory();
    }


    @Test
    void shouldCopyFileAndCreateBackup() throws IOException {
        File source = createFile("source.p12", SOURCE_CONTENT);
        File destination = createFile("destination.p12", DESTINATION_CONTENT);
        File backup = declareFile("destination.p12.bak");
        AppConfiguration configuration = createConfiguration(source, destination);

        copier.copyKeystores(configuration);

        assertThat(readFile(destination)).isEqualTo(readFile(source));
        assertThat(backup).exists();
        assertThat(readFile(backup)).isEqualTo(DESTINATION_CONTENT);
    }

    @Test
    void shouldCopyFileWithoutCreatingBackup() throws IOException {
        File source = createFile("source.p12", SOURCE_CONTENT);
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");
        AppConfiguration configuration = createConfiguration(source, destination);

        copier.copyKeystores(configuration);

        assertThat(destination).exists();
        assertThat(readFile(destination)).isEqualTo(readFile(source));
        assertThat(backup).doesNotExist();
    }

    @Test
    void shouldThrowKeystoreNotExistException() throws IOException {
        File source = declareFile("source.p12");
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");
        AppConfiguration configuration = createConfiguration(source, destination);

        assertThatExceptionOfType(KeystoreNotExistException.class).isThrownBy( () ->
            copier.copyKeystores(configuration)
        );

        assertThat(source).doesNotExist();
        assertThat(destination).doesNotExist();
        assertThat(backup).doesNotExist();
    }

    @Test
    void shouldThrowKeystoreFileCopyException() throws IOException {
        File source = createFile("source.p12", SOURCE_CONTENT);
        source.setReadable(false);
        File destination = declareFile("destination.p12");
        File backup = declareFile("destination.p12.bak");
        AppConfiguration configuration = createConfiguration(source, destination);

        assertThatExceptionOfType(KeystoreFileCopyException.class).isThrownBy( () ->
            copier.copyKeystores(configuration)
        );

        assertThat(source).exists();
        assertThat(destination).doesNotExist();
        assertThat(backup).doesNotExist();
    }

    private AppConfiguration createConfiguration(File source, File destination) {
        return new AppConfiguration(Collections.emptyList(), Collections.emptyList(),
            Collections.singletonList(source.getAbsolutePath()),
            Collections.singletonList(destination.getAbsolutePath()));
    }

    private AppConfiguration createEmptyConfiguration() {
        return new AppConfiguration(Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());
    }

    private String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    private File declareFile(String name) {
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
