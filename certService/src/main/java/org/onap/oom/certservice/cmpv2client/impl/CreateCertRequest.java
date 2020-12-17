/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.createRandomBytes;
import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.createRandomInt;
import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.generatePkiHeader;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;

/**
 * Implementation of the CmpClient Interface conforming to RFC4210 (Certificate Management Protocol
 * (CMP)) and RFC4211 (Certificate Request Message Format (CRMF)) standards.
 */
class CreateCertRequest {

    private X500Name issuerDn;
    private X500Name subjectDn;
    private List<String> sansList;
    private KeyPair subjectKeyPair;
    private Date notBefore;
    private Date notAfter;
    private String initAuthPassword;
    private String senderKid;

    private static final int ITERATIONS = createRandomInt(1000);
    private static final byte[] SALT = createRandomBytes();
    private final int certReqId = createRandomInt(Integer.MAX_VALUE);
    private final AlgorithmIdentifier signingAlgorithm = new DefaultSignatureAlgorithmIdentifierFinder()
            .find("SHA256withRSA");

    public void setIssuerDn(X500Name issuerDn) {
        this.issuerDn = issuerDn;
    }

    public void setSubjectDn(X500Name subjectDn) {
        this.subjectDn = subjectDn;
    }

    public void setSansList(List<String> sansList) {
        this.sansList = sansList;
    }

    public void setSubjectKeyPair(KeyPair subjectKeyPair) {
        this.subjectKeyPair = subjectKeyPair;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public void setInitAuthPassword(String initAuthPassword) {
        this.initAuthPassword = initAuthPassword;
    }

    public void setSenderKid(String senderKid) {
        this.senderKid = senderKid;
    }

    /**
     * Method to create {@link PKIMessage} from {@link CertRequest},{@link ProofOfPossession}, {@link
     * CertReqMsg}, {@link CertReqMessages}, {@link PKIHeader} and {@link PKIBody}.
     *
     * @return {@link PKIMessage}
     */
    public PKIMessage generateCertReq() throws CmpClientException {
        final CertTemplateBuilder certTemplateBuilder =
                new CertTemplateBuilder()
                        .setIssuer(issuerDn)
                        .setSubject(subjectDn)
                        .setExtensions(CmpMessageHelper.generateExtension(sansList))
                        .setValidity(CmpMessageHelper.generateOptionalValidity(notBefore, notAfter))
                        .setVersion(2)
                        .setSerialNumber(new ASN1Integer(0L))
                        .setSigningAlg(signingAlgorithm)
                        .setPublicKey(
                                SubjectPublicKeyInfo.getInstance(subjectKeyPair.getPublic().getEncoded()));

        final CertRequest certRequest = new CertRequest(certReqId, certTemplateBuilder.build(), null);
        final ProofOfPossession proofOfPossession =
                CmpMessageHelper.generateProofOfPossession(certRequest, subjectKeyPair);

        final CertReqMsg certReqMsg = new CertReqMsg(certRequest, proofOfPossession, null);
        final CertReqMessages certReqMessages = new CertReqMessages(certReqMsg);

        final PKIHeader pkiHeader =
                generatePkiHeader(
                        subjectDn,
                        issuerDn,
                        CmpMessageHelper.protectionAlgoIdentifier(ITERATIONS, SALT),
                        senderKid);
        final PKIBody pkiBody = new PKIBody(PKIBody.TYPE_INIT_REQ, certReqMessages);

        return CmpMessageHelper.protectPkiMessage(
                pkiHeader, pkiBody, initAuthPassword, ITERATIONS, SALT);
    }
}
