/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 * ================================================================================
 * Modification copyright 2021 Nokia
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

import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseHelper.checkIfCmpResponseContainsError;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseHelper.getCertFromByteArray;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.certification.model.OldCertificateModel;
import org.onap.oom.certservice.cmpv2client.api.CmpClient;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;
import org.onap.oom.certservice.cmpv2client.validation.CmpCertificationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the CmpClient Interface conforming to RFC4210 (Certificate Management Protocol
 * (CMP)) and RFC4211 (Certificate Request Message Format (CRMF)) standards.
 */
public class CmpClientImpl implements CmpClient {

    private static final Logger LOG = LoggerFactory.getLogger(CmpClientImpl.class);
    private final CloseableHttpClient httpClient;
    private final CmpCertificationValidator validator;

    public CmpClientImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.validator = new CmpCertificationValidator();
    }

    @Override
    public Cmpv2CertificationModel executeInitializationRequest(
            CsrModel csrModel,
            Cmpv2Server server,
            Date notBefore,
            Date notAfter)
            throws CmpClientException {

        validator.validate(csrModel, server, httpClient, notBefore, notAfter);
        final CreateCertRequest certRequest = getIakRvRequest(csrModel, server, notBefore, notAfter, PKIBody.TYPE_INIT_REQ);
        return executeCmpRequest(csrModel, server, certRequest);
    }

    @Override
    public Cmpv2CertificationModel executeInitializationRequest(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException {
        return executeInitializationRequest(csrModel, server, null, null);
    }

    @Override
    public Cmpv2CertificationModel executeKeyUpdateRequest(CsrModel csrModel, Cmpv2Server cmpv2Server,
        OldCertificateModel oldCertificateModel) throws CmpClientException {
        validator.validate(csrModel, cmpv2Server, httpClient, null, null);

        final PkiMessageProtection pkiMessageProtection = getSignatureProtection(oldCertificateModel);
        final CreateCertRequest certRequest =
            getCmpMessageBuilderWithCommonRequestValues(csrModel, cmpv2Server)
                .with(CreateCertRequest::setCmpRequestType, PKIBody.TYPE_KEY_UPDATE_REQ)
                .with(CreateCertRequest::setExtraCerts, getCMPCertificateFromPem(oldCertificateModel.getOldCertificate()))
                .with(CreateCertRequest::setProtection, pkiMessageProtection)
                .build();

        return executeCmpRequest(csrModel, cmpv2Server, certRequest);
    }

    @Override
    public Cmpv2CertificationModel executeCertificationRequest(CsrModel csrModel, Cmpv2Server cmpv2Server) throws CmpClientException {

        validator.validate(csrModel, cmpv2Server, httpClient, null, null);
        final CreateCertRequest certRequest = getIakRvRequest(csrModel, cmpv2Server, null, null, PKIBody.TYPE_CERT_REQ);
        return executeCmpRequest(csrModel, cmpv2Server, certRequest);
    }

    private CreateCertRequest getIakRvRequest(
        CsrModel csrModel,
        Cmpv2Server server,
        Date notBefore,
        Date notAfter,
        int requestType) {

        final String iak = server.getAuthentication().getIak();
        final PkiMessageProtection pkiMessageProtection = new PasswordBasedProtection(iak);
        return getCmpMessageBuilderWithCommonRequestValues(csrModel, server)
            .with(CreateCertRequest::setNotBefore, notBefore)
            .with(CreateCertRequest::setNotAfter, notAfter)
            .with(CreateCertRequest::setSenderKid, server.getAuthentication().getRv())
            .with(CreateCertRequest::setCmpRequestType, requestType)
            .with(CreateCertRequest::setProtection, pkiMessageProtection)
            .build();
    }

    private Cmpv2CertificationModel executeCmpRequest(CsrModel csrModel, Cmpv2Server cmpv2Server,
        CreateCertRequest certRequest) throws CmpClientException {
        final PKIMessage pkiMessage = certRequest.generateCertReq();
        Cmpv2HttpClient cmpv2HttpClient = new Cmpv2HttpClient(httpClient);
        return retrieveCertificates(csrModel, cmpv2Server, pkiMessage, cmpv2HttpClient);
    }

    private CmpMessageBuilder<CreateCertRequest> getCmpMessageBuilderWithCommonRequestValues(CsrModel csrModel,
        Cmpv2Server cmpv2Server) {
        KeyPair keyPair = new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey());
        return CmpMessageBuilder.of(CreateCertRequest::new)
            .with(CreateCertRequest::setIssuerDn, cmpv2Server.getIssuerDN())
            .with(CreateCertRequest::setSubjectDn, csrModel.getSubjectData())
            .with(CreateCertRequest::setSansArray, csrModel.getSans())
            .with(CreateCertRequest::setSubjectKeyPair, keyPair);
    }

    private SignatureProtection getSignatureProtection(OldCertificateModel oldCertificateModel) {
            return new SignatureProtection(oldCertificateModel.getOldPrivateKey());
    }

    private CMPCertificate[] getCMPCertificateFromPem(JcaX509CertificateHolder oldCertificate) {
            CMPCertificate cert = new CMPCertificate(oldCertificate.toASN1Structure());
            return new CMPCertificate[]{cert};
    }

    private Cmpv2CertificationModel retrieveCertificates(
            CsrModel csrModel, Cmpv2Server server, PKIMessage pkiMessage, Cmpv2HttpClient cmpv2HttpClient)
            throws CmpClientException {
        final byte[] respBytes = cmpv2HttpClient.postRequest(pkiMessage, server.getUrl(), server.getCaName());
        try {
            final PKIMessage respPkiMessage = PKIMessage.getInstance(respBytes);
            LOG.info("Received response from Server");
            checkIfCmpResponseContainsError(respPkiMessage);
            validator.checkCmpResponse(respPkiMessage, csrModel.getPublicKey(), server.getAuthentication().getIak());
            return checkCmpCertRepMessage(respPkiMessage);
        } catch (IllegalArgumentException iae) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Error encountered while processing response from CA server ", iae);
            LOG.error("Error encountered while processing response from CA server ", iae);
            throw cmpClientException;
        }
    }

    private Cmpv2CertificationModel checkCmpCertRepMessage(final PKIMessage respPkiMessage)
        throws CmpClientException {
        final PKIBody pkiBody = respPkiMessage.getBody();
        if (Objects.nonNull(pkiBody) && pkiBody.getContent() instanceof CertRepMessage) {
            final CertRepMessage certRepMessage = (CertRepMessage) pkiBody.getContent();
            if (Objects.nonNull(certRepMessage)) {
                try {
                    CertResponse certResponse = getCertificateResponseContainingNewCertificate(certRepMessage);
                    validator.checkServerResponse(certResponse);
                    return verifyReturnCertChainAndTrustStore(respPkiMessage, certRepMessage, certResponse);
                } catch (IOException | CertificateParsingException ex) {
                    CmpClientException cmpClientException =
                        new CmpClientException(
                            "Exception occurred while retrieving Certificates from response", ex);
                    LOG.error("Exception occurred while retrieving Certificates from response", ex);
                    throw cmpClientException;
                }
            } else {
                return new Cmpv2CertificationModel(Collections.emptyList(), Collections.emptyList());
            }
        }
        return new Cmpv2CertificationModel(Collections.emptyList(), Collections.emptyList());
    }

    private Cmpv2CertificationModel verifyReturnCertChainAndTrustStore(
        PKIMessage respPkiMessage, CertRepMessage certRepMessage, CertResponse certResponse)
        throws CertificateParsingException, CmpClientException, IOException {
        LOG.info("Verifying certificates returned as part of CertResponse.");
        final CMPCertificate cmpCertificate =
            certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
        final Optional<X509Certificate> leafCertificate =
            getCertFromByteArray(cmpCertificate.getEncoded(), X509Certificate.class);
        if (leafCertificate.isPresent()) {
            return verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate.get());
        }
        return new Cmpv2CertificationModel(Collections.emptyList(), Collections.emptyList());
    }

    private CertResponse getCertificateResponseContainingNewCertificate(
        CertRepMessage certRepMessage) {
        return certRepMessage.getResponse()[0];
    }
}
