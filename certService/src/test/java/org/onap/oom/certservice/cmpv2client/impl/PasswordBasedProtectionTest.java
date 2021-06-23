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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.cert.crmf.jcajce.JcePKMACValuesCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.oom.certservice.cmpv2client.impl.PkiTestUtils.getProtectedPkiMessage;
import static org.onap.oom.certservice.cmpv2client.impl.PkiTestUtils.getTestPkiHeader;

class PasswordBasedProtectionTest {

    private static final ASN1ObjectIdentifier PASSWORD_BASED_MAC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.13");
    private static final AlgorithmIdentifier SHA_1_ALGORITHM = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.14.3.2.26"));
    private static final AlgorithmIdentifier H_MAC_SHA_1_ALGORITHM = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.6.1.5.5.8.1.2"));
    private static final int MIN_ITERATION_COUNT = 1000;
    private static final int MAX_ITERATION_COUNT = 2000;
    private static final int SALT_LENGTH = 16;

    @BeforeAll
    static void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterAll
    static void clean() {
        Security.removeProvider("BC");
    }

    @Test
    void shouldReturnPasswordBasedMacAlgorithmWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        assertEquals(PASSWORD_BASED_MAC, algorithmIdentifier.getAlgorithm());
    }

    @Test
    void shouldSetPasswordBasedParametersWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        assertTrue(algorithmIdentifier.getParameters() instanceof PBMParameter);
    }

    @Test
    void shouldSetSha1ForOwfWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        PBMParameter pbmParameters = (PBMParameter) algorithmIdentifier.getParameters();
        assertEquals(SHA_1_ALGORITHM, pbmParameters.getOwf());
    }

    @Test
    void shouldSetHMacSha1ForMacWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        PBMParameter pbmParameters = (PBMParameter) algorithmIdentifier.getParameters();
        assertEquals(H_MAC_SHA_1_ALGORITHM, pbmParameters.getMac());
        pbmParameters.getIterationCount();
        pbmParameters.getSalt();
    }

    @Test
    void shouldSetSaltWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        PBMParameter pbmParameters = (PBMParameter) algorithmIdentifier.getParameters();
        assertTrue(pbmParameters.getSalt() instanceof DEROctetString);
        DEROctetString salt = (DEROctetString) pbmParameters.getSalt();
        assertEquals(SALT_LENGTH, salt.getOctets().length);
    }

    @Test
    void shouldSetIterationCountWhenGetAlgorithmMethodCalled() {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = protection.getAlgorithmIdentifier();
        //Then
        PBMParameter pbmParameters = (PBMParameter) algorithmIdentifier.getParameters();
        assertNotNull(pbmParameters.getIterationCount());
        long iterationCount = pbmParameters.getIterationCount().getValue().longValue();
        assertTrue(MIN_ITERATION_COUNT <= iterationCount && iterationCount < MAX_ITERATION_COUNT,
                "Iteration count not in range");
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "123"})
    void shouldReturnProtectionByPasswordWhenGenerateProtectionMethodCalled(String initAuthPassword)
            throws CmpClientException, CMPException {
        //Given
        PasswordBasedProtection protection = new PasswordBasedProtection(initAuthPassword);
        PKIHeader pkiHeader = getTestPkiHeader(protection.getAlgorithmIdentifier());
        PKIBody pkiBody = PkiTestUtils.getTestPkiBody(SHA_1_ALGORITHM);
        //When
        DERBitString messageProtection = protection.generatePkiMessageProtection(pkiHeader, pkiBody);
        //Then
        ProtectedPKIMessage protectedPkiMessage = getProtectedPkiMessage(pkiHeader, pkiBody, messageProtection);
        PKMACBuilder pkMacBuilder = new PKMACBuilder(new JcePKMACValuesCalculator());
        assertTrue(protectedPkiMessage.verify(pkMacBuilder, initAuthPassword.toCharArray()));
    }

}
