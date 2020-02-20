/*
 * ============LICENSE_START=======================================================
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

package org.onap.aaf.certservice.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class CerServiceRequestTestData {

    private static final String RESOURCE_PATH = "src/test/resources/";

    // Request parameters
    public static final String CA_NAME = "TestCA";
    public static final String CSR = getCsrValue();
    public static final String PK = getPkValue();

    // Correct response data
    public static final String CORRECT_RESPONSE = getCorrectResponse();
    public static final String EXPECTED_FIRST_ELEMENT_OF_CERTIFICATE_CHAIN =
            getExpectedFirstElementOfCertificateChain();
    public static final String EXPECTED_FIRST_ELEMENT_OF_TRUSTED_CERTIFICATES =
            getExpectedFirstElementOfTrustedCertificates();

    // Error response data
    public static final String MISSING_PK_RESPONSE = getMissingPkResponse();

    private CerServiceRequestTestData() {
    }

    private static String getMissingPkResponse() {
        String fileName = "missingPkResponse";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String getExpectedFirstElementOfTrustedCertificates() {

        String fileName = "expectedFirstElementOfTrustedCertificates";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String getExpectedFirstElementOfCertificateChain() {
        String fileName = "expectedFirstElementOfCertificateChain";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String getCorrectResponse() {
        String fileName = "correctResponse";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String getPkValue() {
        String fileName = "testPk";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String getCsrValue() {
        String fileName = "testCsr";
        return readFromFile(RESOURCE_PATH + fileName);
    }

    private static String readFromFile(String path) {
        try {
            return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "File not found";
        }
    }
}
