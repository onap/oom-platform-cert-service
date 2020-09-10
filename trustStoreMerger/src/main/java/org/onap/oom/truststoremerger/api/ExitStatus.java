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

package org.onap.oom.truststoremerger.api;

public enum ExitStatus {

    SUCCESS(0, "Success"),
    CERTIFICATES_PATHS_VALIDATION_EXCEPTION(1, "Invalid paths in environment variables"),
    CONFIGURATION_EXCEPTION(2, "Invalid merger configuration"),
    TRUSTSTORE_FILE_FACTORY_EXCEPTION(3, "Invalid truststore file-password pair"),
    PASSWORD_READER_EXCEPTION(4, "Cannot read password from file"),
    CREATE_BACKUP_EXCEPTION(5, "Cannot create backup file"),
    KEYSTORE_INSTANCE_EXCEPTION(6, "Cannot initialize keystore instance"),
    TRUSTSTORE_LOAD_FILE_EXCEPTION(7, "Cannot load truststore file"),
    TRUSTSTORE_DATA_OPERATION_EXCEPTION(8, "Cannot operate on truststore data"),
    MISSING_TRUSTSTORE_EXCEPTION(9, "Missing truststore certificates in provided file"),
    ALIAS_CONFLICT_EXCEPTION(10, "Alias conflict detected"),
    WRITE_TRUSTSTORE_FILE_EXCEPTION(11, "Cannot save truststore file"),
    KEYSTORE_FILE_COPY_EXCEPTION(12, "Cannot copy keystore file"),
    KEYSTORE_NOT_EXIST_EXCEPTION(13, "Keystore file does not exist"),
    UNEXPECTED_EXCEPTION(99, "Application exited abnormally");


    private final int value;
    private final String message;

    ExitStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getExitCodeValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
