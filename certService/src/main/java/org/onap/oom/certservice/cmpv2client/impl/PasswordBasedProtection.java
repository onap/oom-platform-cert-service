/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.createRandomBytes;
import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.createRandomInt;

/**
 * Implementation of password-based PKIMessage protection
 */
public class PasswordBasedProtection implements PkiMessageProtection {

    private static final int ITERATIONS = createRandomInt(1000);
    private static final byte[] SALT = createRandomBytes();
    private static final AlgorithmIdentifier OWF_ALGORITHM =
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.14.3.2.26"));
    private static final AlgorithmIdentifier MAC_ALGORITHM =
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.6.1.5.5.8.1.2"));
    private static final ASN1ObjectIdentifier PASSWORD_BASED_MAC =
            new ASN1ObjectIdentifier("1.2.840.113533.7.66.13");

    private final String initAuthPassword;

    PasswordBasedProtection(String initAuthPassword) {
        this.initAuthPassword = initAuthPassword;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        ASN1Integer iteration = new ASN1Integer(ITERATIONS);
        DEROctetString derSalt = new DEROctetString(SALT);

        PBMParameter pp = new PBMParameter(derSalt, OWF_ALGORITHM, iteration, MAC_ALGORITHM);
        return new AlgorithmIdentifier(PASSWORD_BASED_MAC, pp);
    }

    @Override
    public byte[] generateProtectionBytes(byte[] protectedBytes) throws GeneralSecurityException {
        byte[] baseKey = generateBaseKey();
        return generateMacBytes(baseKey, protectedBytes);
    }

    private byte[] generateBaseKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] raSecret = initAuthPassword.getBytes();
        byte[] baseKey = new byte[raSecret.length + SALT.length];
        System.arraycopy(raSecret, 0, baseKey, 0, raSecret.length);
        System.arraycopy(SALT, 0, baseKey, raSecret.length, SALT.length);
        MessageDigest dig =
                MessageDigest.getInstance(
                        OWF_ALGORITHM.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
        for (int i = 0; i < ITERATIONS; i++) {
            baseKey = dig.digest(baseKey);
            dig.reset();
        }
        return baseKey;
    }

    private byte[] generateMacBytes(byte[] baseKey, byte[] protectedBytes) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(MAC_ALGORITHM.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
        SecretKey key = new SecretKeySpec(baseKey, MAC_ALGORITHM.getAlgorithm().getId());
        mac.init(key);
        mac.reset();
        mac.update(protectedBytes, 0, protectedBytes.length);
        return mac.doFinal();
    }

}
