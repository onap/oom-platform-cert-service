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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PasswordReader {
    private static final String COULD_NOT_READ_PASSWORD_FROM_FILE_MSG_TEMPLATE = "Could not read password from file: %s";

    String readPassword(File file) throws PasswordReaderException {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new PasswordReaderException(String.format(COULD_NOT_READ_PASSWORD_FROM_FILE_MSG_TEMPLATE, file));
        }
    }
}
