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

package org.onap.aaf.certservice.client.certification.conversion;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.certification.exception.PemToPKCS12ConverterException;

class PKCS12FilesCreatorTest {

    private static final String RESOURCES_PATH = "src/test/resources";
    private static final String OUTPUT_PATH = RESOURCES_PATH + "/generatedFiles/";
    private static final String KEYSTORE_PATH = OUTPUT_PATH + "keystore.jks";
    private static final String KEYSTORE_PASS_PATH = OUTPUT_PATH + "keystore.pass";
    private static final String TRUSTSTORE_PATH = OUTPUT_PATH + "truststore.jks";
    private static final String TRUSTSTORE_PASS_PATH = OUTPUT_PATH + "truststore.pass";
    private static final String ERROR_MESSAGE = "java.io.FileNotFoundException: src/test/resources/generatedFiles/thisPathDoesNotExist/keystore.jks (No such file or directory)";

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
    void saveKeystoreDataShouldCreateFilesWithDataInGivenLocation() throws PemToPKCS12ConverterException, IOException {
        // given
        final byte[] data = new byte[]{-128, 1, 127};
        final String password = "onap123";
        File keystore = new File(KEYSTORE_PATH);
        File keystorePass = new File(KEYSTORE_PASS_PATH);
        PKCS12FilesCreator filesCreator = new PKCS12FilesCreator(OUTPUT_PATH);

        // when
        filesCreator.saveKeystoreData(data, password);

        // then
        assertTrue(keystore.exists());
        assertTrue(keystorePass.exists());
        assertArrayEquals(data, Files.readAllBytes(Path.of(KEYSTORE_PATH)));
        assertEquals(password, Files.readString(Path.of(KEYSTORE_PASS_PATH), StandardCharsets.UTF_8));
    }

    @Test
    void saveTruststoreDataShouldCreateFilesWithDataInGivenLocation()
        throws PemToPKCS12ConverterException, IOException {
        // given
        final byte[] data = new byte[]{-128, 1, 2, 3, 127};
        final String password = "nokia321";
        File truststore = new File(TRUSTSTORE_PATH);
        File truststorePass = new File(TRUSTSTORE_PASS_PATH);
        PKCS12FilesCreator filesCreator = new PKCS12FilesCreator(OUTPUT_PATH);

        // when
        filesCreator.saveTruststoreData(data, password);

        // then
        assertTrue(truststore.exists());
        assertTrue(truststorePass.exists());
        assertArrayEquals(data, Files.readAllBytes(Path.of(TRUSTSTORE_PATH)));
        assertEquals(password, Files.readString(Path.of(TRUSTSTORE_PASS_PATH), StandardCharsets.UTF_8));
    }

    @Test
    void saveKeystoreDataShouldThrowPemToPKCS12ConverterExceptionWhenOutputDirectoryDoesNotExist() {
        // given
        final byte[] data = new byte[]{-128, 1, 2, 3, 0};
        final String password = "123aikon";
        PKCS12FilesCreator filesCreator = new PKCS12FilesCreator(OUTPUT_PATH + "thisPathDoesNotExist/");

        // when then
        assertThatThrownBy(() -> filesCreator.saveKeystoreData(data, password))
            .isInstanceOf(PemToPKCS12ConverterException.class).hasMessage(ERROR_MESSAGE);
    }
}