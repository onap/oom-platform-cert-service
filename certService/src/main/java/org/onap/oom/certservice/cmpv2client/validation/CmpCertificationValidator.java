/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

package org.onap.oom.certservice.cmpv2client.validation;

import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.checkImplicitConfirm;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifyPasswordBasedProtection;
import static org.onap.oom.certservice.cmpv2client.impl.CmpResponseValidationHelper.verifySignature;

import java.security.PublicKey;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.onap.oom.certservice.certification.configuration.model.CaMode;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpServerException;
import org.onap.oom.certservice.cmpv2client.impl.CmpUtil;
import org.onap.oom.certservice.cmpv2client.impl.PkiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmpCertificationValidator {
    private static final String DEFAULT_CA_NAME = "Certification Authority";
    private static final String DEFAULT_PROFILE = CaMode.RA.getProfile();
    private static final ASN1ObjectIdentifier PASSWORD_BASED_MAC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.13");
    private static final Logger LOG = LoggerFactory.getLogger(CmpCertificationValidator.class);

    public static void validate(
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

    public void checkCmpResponse(final PKIMessage respPkiMessage, final PublicKey publicKey, final String initAuthPassword)
        throws CmpClientException {
        final PKIHeader header = respPkiMessage.getHeader();
        final AlgorithmIdentifier protectionAlgo = header.getProtectionAlg();
        verifySignatureWithPublicKey(respPkiMessage, publicKey);
        if (isPasswordBasedMacAlgorithm(protectionAlgo)) {
            LOG.info("CMP response is protected by Password Base Mac Algorithm. Attempt to verify protection");
            verifyPasswordBasedMacProtection(respPkiMessage, initAuthPassword, header, protectionAlgo);
        }
    }

    public void checkServerResponse(CertResponse certResponse) {
        if (certResponse.getStatus() != null && certResponse.getStatus().getStatus() != null) {
            logServerResponse(certResponse);
            if (certResponse.getStatus().getStatus().intValue() == PkiStatus.REJECTED.getCode()) {
                String serverMessage = certResponse.getStatus().getStatusString().getStringAt(0).getString();
                throw new CmpServerException(Optional.ofNullable(serverMessage).orElse("N/A"));
            }
        }
    }

    private boolean isPasswordBasedMacAlgorithm(AlgorithmIdentifier protectionAlgo) throws CmpClientException {
        if (Objects.isNull(protectionAlgo)) {
            LOG.error("CMP response does not contain Protection Algorithm field");
            throw new CmpClientException("CMP response does not contain Protection Algorithm field");
        }
        return PASSWORD_BASED_MAC.equals(protectionAlgo.getAlgorithm());
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

    private void verifyPasswordBasedMacProtection(PKIMessage respPkiMessage, String initAuthPassword,
        PKIHeader header, AlgorithmIdentifier protectionAlgo)
        throws CmpClientException {
        LOG.debug("Verifying PasswordBased Protection of the Response.");
        verifyPasswordBasedProtection(respPkiMessage, initAuthPassword, protectionAlgo);
        checkImplicitConfirm(header);
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
}
