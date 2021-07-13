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
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.cmp.GeneralPKIMessage;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Date;

final class PkiTestUtils {

    private static final String CN_TEST_SUBJECT = "CN=test1Subject";
    private static final String CN_TEST_ISSUER = "CN=test2Issuer";
    private static final int TEST_CERT_REQUEST_ID = 1432;
    private static final int PVNO = 0;
    private static final String BC_PROVIDER = "BC";
    private static final String RSA = "RSA";

    private PkiTestUtils() {
    }

    static PKIBody getTestPkiBody(AlgorithmIdentifier signingAlgorithm) {
        CertTemplateBuilder certTemplateBuilder =
                new CertTemplateBuilder()
                        .setIssuer(new X500Name(CN_TEST_ISSUER))
                        .setSubject(new X500Name(CN_TEST_SUBJECT))
                        .setSigningAlg(signingAlgorithm);

        CertRequest certRequest = new CertRequest(TEST_CERT_REQUEST_ID, certTemplateBuilder.build(), null);
        CertReqMsg certReqMsg = new CertReqMsg(certRequest, null, null);

        CertReqMessages certReqMessages = new CertReqMessages(certReqMsg);
        return new PKIBody(0, certReqMessages);
    }

    static PKIHeader getTestPkiHeader(AlgorithmIdentifier protectionAlgorithm) {
        PKIHeaderBuilder pkiHeader = new PKIHeaderBuilder(
                PVNO,
                new GeneralName(new X500Name(CN_TEST_SUBJECT)),
                new GeneralName(new X500Name(CN_TEST_ISSUER)));
        pkiHeader.setProtectionAlg(protectionAlgorithm);
        pkiHeader.setMessageTime(new DERGeneralizedTime(new Date()));
        return pkiHeader.build();
    }

    static ProtectedPKIMessage getProtectedPkiMessage(PKIHeader pkiHeader, PKIBody pkiBody, DERBitString messageProtection) {
        PKIMessage pkiMessage = new PKIMessage(pkiHeader, pkiBody, messageProtection);
        GeneralPKIMessage generalPkiMessage = new GeneralPKIMessage(pkiMessage);
        return new ProtectedPKIMessage(generalPkiMessage);
    }

    static KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA, BC_PROVIDER);
        return keyPairGenerator.generateKeyPair();
    }

    static ContentVerifierProvider getContentVerifierProvider(PublicKey publicKey) throws OperatorCreationException {
        return new JcaContentVerifierProviderBuilder()
                .setProvider(BC_PROVIDER)
                .build(publicKey);
    }
}
