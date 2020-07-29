/*============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.certification.writer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.oom.certservice.client.certification.exception.CertFileWriterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CertFileWriterTest {

    private static final String RESOURCES_PATH = "src/test/resources/";
    private static final String OUTPUT_PATH = RESOURCES_PATH + "generatedFiles/";
    private static final String NOT_EXISTING_OUTPUT_PATH = OUTPUT_PATH + "directoryDoesNotExist/";
    private static final String TRUSTSTORE_P12 = "truststore.p12";
    private File outputDirectory = new File(OUTPUT_PATH);

    @AfterEach
    void cleanUpFiles() {
        deleteDirectoryRecursive(outputDirectory);
    }

    @ParameterizedTest
    @ValueSource(strings = {OUTPUT_PATH, NOT_EXISTING_OUTPUT_PATH})
    void certFileWriterShouldCreateFilesWithDataInGivenLocation(String outputPath)
            throws IOException, CertFileWriterException {
        // given
        File truststore = new File(outputPath + TRUSTSTORE_P12);
        CertFileWriter certFileWriter = CertFileWriter.createWithDir(outputPath);
        final byte[] data = new byte[]{-128, 1, 2, 3, 127};

        // when
        certFileWriter.saveData(data, TRUSTSTORE_P12);

        // then
        assertThat(truststore.exists()).isTrue();
        assertThat(Files.readAllBytes(Path.of(outputPath + TRUSTSTORE_P12))).isEqualTo(data);
    }

    private void deleteDirectoryRecursive(File dirForDeletion) {
        List.of(dirForDeletion.listFiles()).forEach(file -> {
            if (file.isDirectory()) {
                deleteDirectoryRecursive(file);
            }
            file.delete();
        });
        dirForDeletion.delete();
    }

}
