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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileManagerTest {

    private FileManager fileManager = new FileManager();

    @ParameterizedTest
    @CsvSource(value = {
        "opt/app/truststore.jks:.jks",
        "opt/app/truststore.p12:.p12",
        "opt/app/truststore.pem:.pem",
        "opt/app/truststore:''",
    }, delimiter = ':')
    void shouldReturnCorrectExtension(String filePath, String expectedExtension) {
        String extension = fileManager.getExtension(new File(filePath));
        assertThat(extension).isEqualTo(expectedExtension);
    }

}
