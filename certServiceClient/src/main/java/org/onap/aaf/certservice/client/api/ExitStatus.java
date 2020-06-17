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

package org.onap.aaf.certservice.client.api;

public enum ExitStatus {

    SUCCESS(0, "Success"),
    CLIENT_CONFIGURATION_EXCEPTION(1, "Invalid client configuration"),
    CSR_CONFIGURATION_EXCEPTION(2, "Invalid CSR configuration"),
    KEY_PAIR_GENERATION_EXCEPTION(3, "Fail in key pair generation"),
    CSR_GENERATION_EXCEPTION(4, "Fail in CSR generation"),
    CERT_SERVICE_API_CONNECTION_EXCEPTION(5, "CertService HTTP unsuccessful response"),
    HTTP_CLIENT_EXCEPTION(6, "Internal HTTP Client connection problem"),
    PEM_CONVERSION_EXCEPTION(7, "Fail in PEM conversion"),
    PK_TO_PEM_ENCODING_EXCEPTION(8, "Fail in Private Key to PEM Encoding"),
    TLS_CONFIGURATION_EXCEPTION(9, "Invalid TLS configuration"),
    FILE_CREATION_EXCEPTION(10, "File could not be created");

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
