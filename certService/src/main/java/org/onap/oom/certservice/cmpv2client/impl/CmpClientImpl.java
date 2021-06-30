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
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.checkImplicitConfirm;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifyPasswordBasedProtection;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifySignature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.io.pem.PemReader;
import org.onap.oom.certservice.certification.configuration.model.CaMode;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.cmpv2client.api.CmpClient;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpServerException;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;
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
    private static final String DEFAULT_PROFILE = CaMode.RA.getProfile();

    public CmpClientImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Cmpv2CertificationModel createCertificate(
            CsrModel csrModel,
            Cmpv2Server server,
            Date notBefore,
            Date notAfter)
            throws CmpClientException {

        validate(csrModel, server, httpClient, notBefore, notAfter);
        KeyPair keyPair = new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey());

        final String iak = server.getAuthentication().getIak();
        final PkiMessageProtection pkiMessageProtection = new PasswordBasedProtection(iak);
        final CreateCertRequest certRequest =
                CmpMessageBuilder.of(CreateCertRequest::new)
                        .with(CreateCertRequest::setIssuerDn, server.getIssuerDN())
                        .with(CreateCertRequest::setSubjectDn, csrModel.getSubjectData())
                        .with(CreateCertRequest::setSansArray, csrModel.getSans())
                        .with(CreateCertRequest::setSubjectKeyPair, keyPair)
                        .with(CreateCertRequest::setNotBefore, notBefore)
                        .with(CreateCertRequest::setNotAfter, notAfter)
                        .with(CreateCertRequest::setSenderKid, server.getAuthentication().getRv())
                        .with(CreateCertRequest::setCmpRequestType, PKIBody.TYPE_INIT_REQ)
                        .with(CreateCertRequest::setProtection, pkiMessageProtection)
                        .build();

        final PKIMessage pkiMessage = certRequest.generateCertReq();
        Cmpv2HttpClient cmpv2HttpClient = new Cmpv2HttpClient(httpClient);
        return retrieveCertificates(csrModel, server, pkiMessage, cmpv2HttpClient);
    }

    @Override
    public Cmpv2CertificationModel createCertificate(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException {
        return createCertificate(csrModel, server, null, null);
    }

    @Override
    public Cmpv2CertificationModel updateCertificate(CsrModel csrModel, Cmpv2Server cmpv2Server,
        CertificateUpdateModel certificateUpdateModel) throws CmpClientException {
        validate(csrModel, cmpv2Server, httpClient, null, null);
        KeyPair keyPair = new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey());

        final PkiMessageProtection pkiMessageProtection =
            new SignatureProtection(getPrivateKey(certificateUpdateModel.getEncodedOldPrivateKey()));
        final CreateCertRequest certRequest =
            CmpMessageBuilder.of(CreateCertRequest::new)
                .with(CreateCertRequest::setIssuerDn, cmpv2Server.getIssuerDN())
                .with(CreateCertRequest::setSubjectDn, csrModel.getSubjectData())
                .with(CreateCertRequest::setSansArray, csrModel.getSans())
                .with(CreateCertRequest::setSubjectKeyPair, keyPair)
                .with(CreateCertRequest::setCmpRequestType, PKIBody.TYPE_KEY_UPDATE_REQ)
                .with(CreateCertRequest::setExtraCerts, getCertsFromPem(certificateUpdateModel.getEncodedOldCert()))
                .with(CreateCertRequest::setProtection, pkiMessageProtection)
                .build();

        final PKIMessage pkiMessage = certRequest.generateCertReq();
        Cmpv2HttpClient cmpv2HttpClient = new Cmpv2HttpClient(httpClient);
        return retrieveCertificates(csrModel, cmpv2Server, pkiMessage, cmpv2HttpClient);

//        return null;
    }
    public CMPCertificate[] getCertsFromPem(String encodedCertPem) throws CmpClientException {
//        Base64.decodeBase64(encodedCertPem);
        try {
            Certificate certificate = Certificate.getInstance(new PemReader
                (new InputStreamReader(
                    new ByteArrayInputStream(Base64.decodeBase64(encodedCertPem)))).readPemObject().getContent());
            CMPCertificate cert = new CMPCertificate(certificate);
            return new CMPCertificate[]{cert};
        } catch (IOException e) {
            throw new CmpClientException("Cannot parse old certificate", e);
        }
    }
    private PrivateKey getPrivateKey(String encodedOldPrivateKey) throws CmpClientException {

        try {
            byte[] decodedOldPrivateKey = Base64.decodeBase64(encodedOldPrivateKey);
            String key = new String(decodedOldPrivateKey);
            String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
            byte[] encoded = Base64.decodeBase64(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CmpClientException("Cannot parse old private key ", e);
        }
    }

    private void checkCmpResponse(
            final PKIMessage respPkiMessage, final PublicKey publicKey, final String initAuthPassword)
            throws CmpClientException {
        final PKIHeader header = respPkiMessage.getHeader();
        final AlgorithmIdentifier protectionAlgo = header.getProtectionAlg();
        verifySignatureWithPublicKey(respPkiMessage, publicKey);
//        verifyProtectionWithProtectionAlgo(respPkiMessage, initAuthPassword, header, protectionAlgo);
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

    private Cmpv2CertificationModel checkCmpCertRepMessage(final PKIMessage respPkiMessage)
            throws CmpClientException {
        final PKIBody pkiBody = respPkiMessage.getBody();
        if (Objects.nonNull(pkiBody) && pkiBody.getContent() instanceof CertRepMessage) {
            final CertRepMessage certRepMessage = (CertRepMessage) pkiBody.getContent();
            if (Objects.nonNull(certRepMessage)) {
                try {
                    CertResponse certResponse = getCertificateResponseContainingNewCertificate(certRepMessage);
                    checkServerResponse(certResponse);
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

    private void checkServerResponse(CertResponse certResponse) {
        if (certResponse.getStatus() != null && certResponse.getStatus().getStatus() != null) {
            logServerResponse(certResponse);
            if (certResponse.getStatus().getStatus().intValue() == PkiStatus.REJECTED.getCode()) {
                String serverMessage = certResponse.getStatus().getStatusString().getStringAt(0).getString();
                throw new CmpServerException(Optional.ofNullable(serverMessage).orElse("N/A"));
            }
        }
    }

    private void logServerResponse(CertResponse certResponse) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Response status code: {}", certResponse.getStatus().getStatus());
        }
        if (certResponse.getStatus().getStatusString() != null) {
            String serverMessage = certResponse.getStatus().getStatusString().getStringAt(0).getString();
            LOG.warn("Response status text: {}", serverMessage);
        }
        if (LOG.isWarnEnabled() && certResponse.getStatus().getFailInfo() != null) {
            LOG.warn("Response fail info:   {}", certResponse.getStatus().getFailInfo());
        }
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

    /**
     * Validate inputs for Certificate Creation.
     *
     * @param csrModel Certificate Signing Request model. Must not be {@code null}.
     * @param server   CMPv2 Server. Must not be {@code null}.
     * @throws IllegalArgumentException if Before Date is set after the After Date.
     */
    private static void validate(
            final CsrModel csrModel,
            final Cmpv2Server server,
            final CloseableHttpClient httpClient,
            final Date notBefore,
            final Date notAfter) {

        String caName = CmpUtil.isNullOrEmpty(server.getCaName()) ? server.getCaName() : DEFAULT_CA_NAME;
        String profile = server.getCaMode() != null ? server.getCaMode().getProfile() : DEFAULT_PROFILE;
        LOG.info(
                "Validate before creating Certificate Request for CA :{} in Mode {} ", caName, profile);

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

    private Cmpv2CertificationModel retrieveCertificates(
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
