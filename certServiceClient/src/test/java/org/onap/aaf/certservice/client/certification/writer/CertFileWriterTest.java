/*============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.certification.writer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.certification.exception.CertFileWriterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CertFileWriterTest {

    private static final String RESOURCES_PATH = "src/test/resources";
    private static final String OUTPUT_PATH = RESOURCES_PATH + "/generatedFiles/";
    private static final String TRUSTSTORE_P12 = "truststore.p12";
    private static final String ERROR_MESSAGE = "java.io.FileNotFoundException: src/test/resources/generatedFiles/thisPathDoesNotExist/truststore.p12 (No such file or directory)";

    private File outputDirectory = new File(OUTPUT_PATH);

    @BeforeEach
    void createDirectory() {
        outputDirectory.mkdir();
    }

    @AfterEach
    void cleanUpFiles() {
        List.of(outputDirectory.listFiles()).forEach(f -> f.delete());
        outputDirectory.delete();
    }

    @Test
    void certFileWriterShouldCreateFilesWithDataInGivenLocation()
            throws IOException, CertFileWriterException {
        // given
        final byte[] data = new byte[]{-128, 1, 2, 3, 127};
        File truststore = new File(OUTPUT_PATH + TRUSTSTORE_P12);
        CertFileWriter certFileWriter = new CertFileWriter(OUTPUT_PATH);

        // when
        certFileWriter.saveData(data, TRUSTSTORE_P12);

        // then
        assertThat(truststore.exists()).isTrue();
        assertThat(Files.readAllBytes(Path.of(OUTPUT_PATH + TRUSTSTORE_P12))).isEqualTo(data);
    }

    @Test
    void certFileWriterShouldThrowCertFileWriterExceptionWhenOutputDirectoryDoesNotExist() {
        // given
        final byte[] data = new byte[]{-128, 1, 2, 3, 0};
        CertFileWriter certFileWriter = new CertFileWriter(OUTPUT_PATH + "thisPathDoesNotExist/");

        // when then
        assertThatThrownBy(() -> certFileWriter.saveData(data, TRUSTSTORE_P12))
                .isInstanceOf(CertFileWriterException.class).hasMessage(ERROR_MESSAGE);
    }
}
