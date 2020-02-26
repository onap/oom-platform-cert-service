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

package org.onap.aaf.certservice.client.certification;

public final class EncryptionAlgorithmConstants {

    private EncryptionAlgorithmConstants() {}

    public static final String RSA_ENCRYPTION_ALGORITHM = "RSA";
    public static final String SIGN_ALGORITHM = "SHA1withRSA";
    public static final int KEY_SIZE = 2048;

    public static final String COMMON_NAME = "CN";
    public static final String ORGANIZATION = "O";
    public static final String ORGANIZATION_UNIT = "OU";
    public static final String LOCATION = "L";
    public static final String STATE = "ST";
    public static final String COUNTRY = "C";

}
