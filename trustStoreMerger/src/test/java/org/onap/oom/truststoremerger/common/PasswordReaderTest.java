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

import org.junit.jupiter.api.Test;

import java.io.File;
import org.onap.oom.truststoremerger.merger.exception.PasswordReaderException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PasswordReaderTest {

    @Test
    void shouldReturnCorrectPasswordFromFile() throws PasswordReaderException {
        String fileData = PasswordReader.readPassword(new File("src/test/resources/truststore-jks.pass"));
        assertThat(fileData).isEqualTo("EOyuFbuYDyq_EhpboM72RHua");
    }

    @Test
    void shouldThrowExceptionForNonExistingFile() {
        assertThatExceptionOfType(PasswordReaderException.class)
                .isThrownBy(() -> PasswordReader.readPassword(new File("src/test/resources/non-esisting-file.pass")));
    }
}
