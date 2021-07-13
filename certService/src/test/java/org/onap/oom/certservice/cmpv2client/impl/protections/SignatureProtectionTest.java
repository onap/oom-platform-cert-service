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

package org.onap.oom.certservice.cmpv2client.impl.protections;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.oom.certservice.cmpv2client.impl.protections.PkiTestUtils.getProtectedPkiMessage;
import static org.onap.oom.certservice.cmpv2client.impl.protections.PkiTestUtils.getTestPkiBody;
import static org.onap.oom.certservice.cmpv2client.impl.protections.PkiTestUtils.getTestPkiHeader;

class SignatureProtectionTest {

    private static final String SHA256_RSA_OID = "1.2.840.113549.1.1.11";
    private static final AlgorithmIdentifier SHA256_RSA_ALGORITHM = new DefaultSignatureAlgorithmIdentifierFinder()
            .find("SHA256withRSA");
    private static final String BC_PROVIDER = "BC";

    @BeforeAll
    static void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @AfterAll
    static void clean() {
        Security.removeProvider(BC_PROVIDER);
    }

    @Test
    void shouldReturnExpectedAlgorithmWhenGetAlgorithmMethodCalled() {
        //Given
        SignatureProtection signatureProtection = new SignatureProtection(null);
        //When
        AlgorithmIdentifier algorithmIdentifier = signatureProtection.getAlgorithmIdentifier();
        //Then
        assertNotNull(algorithmIdentifier);
        assertNotNull(algorithmIdentifier.getAlgorithm());
        assertEquals(SHA256_RSA_OID, algorithmIdentifier.getAlgorithm().toString());
    }

    @Test
    void shouldReturnProtectionByPkWhenGenerateProtectionMethodCalled()
            throws GeneralSecurityException, CmpClientException, OperatorCreationException, CMPException {
        //Given
        KeyPair keyPair = PkiTestUtils.getKeyPair();
        SignatureProtection signatureProtection = new SignatureProtection(keyPair.getPrivate());
        PKIHeader pkiHeader = getTestPkiHeader(SHA256_RSA_ALGORITHM);
        PKIBody pkiBody = getTestPkiBody(SHA256_RSA_ALGORITHM);
        //When
        DERBitString protection = signatureProtection.generatePkiMessageProtection(pkiHeader, pkiBody);
        //Then
        ProtectedPKIMessage protectedPkiMessage = getProtectedPkiMessage(pkiHeader, pkiBody, protection);
        ContentVerifierProvider verifierProvider = PkiTestUtils.getContentVerifierProvider(keyPair.getPublic());
        assertTrue(protectedPkiMessage.verify(verifierProvider));
    }

}
