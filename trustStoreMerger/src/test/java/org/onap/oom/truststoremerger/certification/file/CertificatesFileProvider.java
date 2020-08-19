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

package org.onap.oom.truststoremerger.certification.file;

import java.io.File;

public class CertificatesFileProvider {

    public static final String TRUSTSTORE_P12_FILE_PATH = "src/test/resources/truststore-p12.p12";
    public static final String TRUSTSTORE_P12_PASSWORD = "88y9v5D8H3SG6bZWRVHDfOAo";
    public static final String KEYSTORE_INSTANCE_P12 = "PKCS12";
    private static final String TRUSTSTORE_P12_TEMPORARY_FILE_PATH = "src/test/resources/truststore-new-p12.p12";

    public static P12Truststore getSampleP12Truststore(){
        return new P12Truststore(getSampleP12File(), TRUSTSTORE_P12_PASSWORD, KEYSTORE_INSTANCE_P12);
    }

    public static P12Truststore getTemporaryP12Truststore(){
        return new P12Truststore(new File(TRUSTSTORE_P12_TEMPORARY_FILE_PATH), TRUSTSTORE_P12_PASSWORD, KEYSTORE_INSTANCE_P12);
    }

    public static File getSampleP12File(){
        return new File(TRUSTSTORE_P12_FILE_PATH);
    }
}
