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

package org.onap.oom.certservice.cmpv2client.impl;


import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * Implementation of signature PKIMessage protection
 */
public class SignatureProtection extends PkiMessageProtection {

    private static final AlgorithmIdentifier SHA256_RSA_ALGORITHM = new DefaultSignatureAlgorithmIdentifierFinder()
            .find("SHA256withRSA");

    private final PrivateKey oldPrivateKey;

    SignatureProtection(PrivateKey privateKey) {
        this.oldPrivateKey = privateKey;
    }

    @Override
    AlgorithmIdentifier getAlgorithmIdentifier() {
        return SHA256_RSA_ALGORITHM;
    }

    @Override
    byte[] generateProtectionBytes(byte[] protectedBytes) throws GeneralSecurityException {
        Signature signature =
                Signature.getInstance(
                        PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(),
                        BouncyCastleProvider.PROVIDER_NAME);
        signature.initSign(oldPrivateKey);
        signature.update(protectedBytes, 0, protectedBytes.length);
        return signature.sign();
    }

}
