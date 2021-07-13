/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nokia.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.certservice.certification.conversion;

import org.bouncycastle.util.io.pem.PemObject;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class StringBase64ToPrivateKeyConverter {

    private final PemObjectFactory pemObjectFactory = new PemObjectFactory();

    public PrivateKey convert(StringBase64 privateKey) throws KeyDecryptionException {
        PemObject decodedPrivateKey = createDecodedPrivateKey(privateKey);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey.getContent());
            return factory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyDecryptionException("Converting Private Key failed", e.getCause());
        }
    }

    private PemObject createDecodedPrivateKey(StringBase64 privateKey) throws KeyDecryptionException {
        return privateKey.asString()
                .flatMap(pemObjectFactory::createPemObject)
                .orElseThrow(
                        () -> new KeyDecryptionException("Incorrect Key, decryption failed")
                );
    }

}
