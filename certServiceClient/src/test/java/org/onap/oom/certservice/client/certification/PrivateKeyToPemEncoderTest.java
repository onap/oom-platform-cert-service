/*============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.certification;


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.certification.exception.PkEncodingException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateKeyToPemEncoderTest {

    private static final String ENCRYPTION_ALGORITHM = "RSA";
    private static final String RESOURCES_DIR = "src/test/resources/";
    private static final String PRIVATE_KEY_PEM_PATH = RESOURCES_DIR + "rsaPrivateKeyPem";

    @Test
    void shouldReturnProperlyEncodedPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, PkEncodingException, IOException {
        //given
        String expectedPem = Files.readString(Paths.get(PRIVATE_KEY_PEM_PATH));
        PrivateKeyToPemEncoder testedPkEncoder = new PrivateKeyToPemEncoder();
        //when
        PrivateKey privateKey = extractPrivateKeyFromPem(expectedPem);
        String resultPkInPem = testedPkEncoder.encodePrivateKeyToPem(privateKey);
        //then
        assertThat(resultPkInPem).isEqualTo(expectedPem);
    }

    private PrivateKey extractPrivateKeyFromPem(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        PemReader pemReader = new PemReader(new StringReader(pem));
        PemObject pemObject = pemReader.readPemObject();
        pemReader.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        return kf.generatePrivate(spec);
    }
}
