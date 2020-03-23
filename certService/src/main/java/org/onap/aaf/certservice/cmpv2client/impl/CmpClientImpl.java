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

package org.onap.aaf.certservice.cmpv2client.impl;

import java.security.KeyPair;
import java.security.PublicKey;

import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseHelper.checkIfCmpResponseContainsError;
import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseHelper.getCertfromByteArray;
import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore;
import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseValidationHelper.checkImplicitConfirm;
import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifyPasswordBasedProtection;
import static org.onap.aaf.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifySignature;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.onap.aaf.certservice.certification.configuration.model.CaMode;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.aaf.certservice.cmpv2client.api.CmpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the CmpClient Interface conforming to RFC4210 (Certificate Management Protocol
 * (CMP)) and RFC4211 (Certificate Request Message Format (CRMF)) standards.
 */
public class CmpClientImpl implements CmpClient {

    private static final Logger LOG = LoggerFactory.getLogger(CmpClientImpl.class);
    private final CloseableHttpClient httpClient;

    private static final String DEFAULT_CA_NAME = "Certification Authority";

    public CmpClientImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<List<X509Certificate>> createCertificate(
            CsrModel csrModel,
            Cmpv2Server server,
            Date notBefore,
            Date notAfter)
            throws CmpClientException {

        validate(csrModel, server, httpClient, notBefore, notAfter);
        KeyPair keyPair = new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey());

        final CreateCertRequest certRequest =
                CmpMessageBuilder.of(CreateCertRequest::new)
                        .with(CreateCertRequest::setIssuerDn, server.getIssuerDN())
                        .with(CreateCertRequest::setSubjectDn, csrModel.getSubjectData())
                        .with(CreateCertRequest::setSansList, csrModel.getSans())
                        .with(CreateCertRequest::setSubjectKeyPair, keyPair)
                        .with(CreateCertRequest::setNotBefore, notBefore)
                        .with(CreateCertRequest::setNotAfter, notAfter)
                        .with(CreateCertRequest::setInitAuthPassword, server.getAuthentication().getIak())
                        .with(CreateCertRequest::setSenderKid, server.getAuthentication().getRv())
                        .build();

        final PKIMessage pkiMessage = certRequest.generateCertReq();
        Cmpv2HttpClient cmpv2HttpClient = new Cmpv2HttpClient(httpClient);
        return retrieveCertificates(csrModel, server, pkiMessage, cmpv2HttpClient);
    }

    @Override
    public List<List<X509Certificate>> createCertificate(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException {
        return createCertificate(csrModel, server, null, null);
    }

    private void checkCmpResponse(
            final PKIMessage respPkiMessage, final PublicKey publicKey, final String initAuthPassword)
            throws CmpClientException {
        final PKIHeader header = respPkiMessage.getHeader();
        final AlgorithmIdentifier protectionAlgo = header.getProtectionAlg();
        verifySignatureWithPublicKey(respPkiMessage, publicKey);
        verifyProtectionWithProtectionAlgo(respPkiMessage, initAuthPassword, header, protectionAlgo);
    }

    private void verifySignatureWithPublicKey(PKIMessage respPkiMessage, PublicKey publicKey)
            throws CmpClientException {
        if (Objects.nonNull(publicKey)) {
            LOG.debug("Verifying signature of the response.");
            verifySignature(respPkiMessage, publicKey);
        } else {
            LOG.error("Public Key is not available, therefore cannot verify signature");
            throw new CmpClientException(
                    "Public Key is not available, therefore cannot verify signature");
        }
    }

    private void verifyProtectionWithProtectionAlgo(
            PKIMessage respPkiMessage,
            String initAuthPassword,
            PKIHeader header,
            AlgorithmIdentifier protectionAlgo)
            throws CmpClientException {
        if (Objects.nonNull(protectionAlgo)) {
            LOG.debug("Verifying PasswordBased Protection of the Response.");
            verifyPasswordBasedProtection(respPkiMessage, initAuthPassword, protectionAlgo);
            checkImplicitConfirm(header);
        } else {
            LOG.error(
                    "Protection Algorithm is not available when expecting PBE protected response containing protection algorithm");
            throw new CmpClientException(
                    "Protection Algorithm is not available when expecting PBE protected response containing protection algorithm");
        }
    }

    private List<List<X509Certificate>> checkCmpCertRepMessage(final PKIMessage respPkiMessage)
            throws CmpClientException {
        final PKIBody pkiBody = respPkiMessage.getBody();
        if (Objects.nonNull(pkiBody) && pkiBody.getContent() instanceof CertRepMessage) {
            final CertRepMessage certRepMessage = (CertRepMessage) pkiBody.getContent();
            if (Objects.nonNull(certRepMessage)) {
                final CertResponse certResponse =
                        getCertificateResponseContainingNewCertificate(certRepMessage);
                try {
                    return verifyReturnCertChainAndTrustStore(respPkiMessage, certRepMessage, certResponse);
                } catch (IOException | CertificateParsingException ex) {
                    CmpClientException cmpClientException =
                            new CmpClientException(
                                    "Exception occurred while retrieving Certificates from response", ex);
                    LOG.error("Exception occurred while retrieving Certificates from response", ex);
                    throw cmpClientException;
                }
            } else {
                return new ArrayList<>(Collections.emptyList());
            }
        }
        return new ArrayList<>(Collections.emptyList());
    }

    private List<List<X509Certificate>> verifyReturnCertChainAndTrustStore(
            PKIMessage respPkiMessage, CertRepMessage certRepMessage, CertResponse certResponse)
            throws CertificateParsingException, CmpClientException, IOException {
        LOG.info("Verifying certificates returned as part of CertResponse.");
        final CMPCertificate cmpCertificate =
                certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
        final Optional<X509Certificate> leafCertificate =
                getCertfromByteArray(cmpCertificate.getEncoded(), X509Certificate.class);
        if (leafCertificate.isPresent()) {
            return verifyAndReturnCertChainAndTrustSTore(
                    respPkiMessage, certRepMessage, leafCertificate.get());
        }
        return Collections.emptyList();
    }

    private CertResponse getCertificateResponseContainingNewCertificate(
            CertRepMessage certRepMessage) {
        return certRepMessage.getResponse()[0];
    }

    /**
     * Validate inputs for Certificate Creation.
     *
     * @param csrModel        Certificate Signing Request model. Must not be {@code null}.
     * @param server          CMPv2 Server. Must not be {@code null}.
     * @throws IllegalArgumentException if Before Date is set after the After Date.
     */
    private static void validate(
            final CsrModel csrModel,
            final Cmpv2Server server,
            final CloseableHttpClient httpClient,
            final Date notBefore,
            final Date notAfter) {


        String caName = CmpUtil.isNullOrEmpty(server.getCaName()) ? server.getCaName() : DEFAULT_CA_NAME;
        String caProfile = server.getCaMode() != null ? String.valueOf(server.getCaMode()) : String.valueOf(CaMode.RA);
        LOG.info(
                "Validate before creating Certificate Request for CA :{} in Mode {} ", caName, caProfile);

        CmpUtil.notNull(csrModel, "CsrModel Instance");
        CmpUtil.notNull(csrModel.getSubjectData(), "Subject DN");
        CmpUtil.notNull(csrModel.getPrivateKey(), "Subject private key");
        CmpUtil.notNull(csrModel.getPublicKey(), "Subject public key");
        CmpUtil.notNull(server.getIssuerDN(), "Issuer DN");
        CmpUtil.notNull(server.getUrl(), "External CA URL");
        CmpUtil.notNull(server.getAuthentication().getIak(), "IAK/RV Password");
        CmpUtil.notNull(httpClient, "Closeable Http Client");

        if (notBefore != null && notAfter != null && notBefore.compareTo(notAfter) > 0) {
            throw new IllegalArgumentException("Before Date is set after the After Date");
        }
    }

    private List<List<X509Certificate>> retrieveCertificates(
            CsrModel csrModel, Cmpv2Server server, PKIMessage pkiMessage, Cmpv2HttpClient cmpv2HttpClient)
            throws CmpClientException {
        final byte[] respBytes = cmpv2HttpClient.postRequest(pkiMessage, server.getUrl(), server.getCaName());
        try {
            final PKIMessage respPkiMessage = PKIMessage.getInstance(respBytes);
            LOG.info("Received response from Server");
            checkIfCmpResponseContainsError(respPkiMessage);
            checkCmpResponse(respPkiMessage, csrModel.getPublicKey(), server.getAuthentication().getIak());
            return checkCmpCertRepMessage(respPkiMessage);
        } catch (IllegalArgumentException iae) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Error encountered while processing response from CA server ", iae);
            LOG.error("Error encountered while processing response from CA server ", iae);
            throw cmpClientException;
        }
    }
}
