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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.ErrorMsgContent;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpServerException;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmpResponseHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CmpResponseHelper.class);

    private CmpResponseHelper() {
    }

    static void checkIfCmpResponseContainsError(PKIMessage respPkiMessage) {
        LOG.info("Response type: {} ", respPkiMessage.getBody().getType());
        if (respPkiMessage.getBody().getType() == PKIBody.TYPE_ERROR) {
            final ErrorMsgContent errorMsgContent =
                (ErrorMsgContent) respPkiMessage.getBody().getContent();
            String text = errorMsgContent.getPKIStatusInfo().getStatusString().getStringAt(0).getString();
            LOG.error("Error in the PkiMessage response: {} ", text);
            throw new CmpServerException(Optional.ofNullable(text).orElse("N/A"));
        }
    }


    /**
     * Puts together certChain and Trust store and verifies the certChain
     *
     * @param respPkiMessage  PKIMessage that may contain extra certs used for certchain
     * @param certRepMessage  CertRepMessage that should contain rootCA for certchain
     * @param leafCertificate certificate returned from our original Cert Request
     * @return model for certification containing certificate chain and trusted certificates
     * @throws CertificateParsingException thrown if error occurs while parsing certificate
     * @throws IOException                 thrown if IOException occurs while parsing certificate
     * @throws CmpClientException          thrown if error occurs during the verification of the certChain
     */
    static Cmpv2CertificationModel verifyAndReturnCertChainAndTrustSTore(
        PKIMessage respPkiMessage, CertRepMessage certRepMessage, X509Certificate leafCertificate)
        throws CertificateParsingException, IOException, CmpClientException {
        Map<X500Name, X509Certificate> certificates = mapAllCertificates(respPkiMessage, certRepMessage);
        return extractCertificationModel(certificates, leafCertificate);
    }

    private static Map<X500Name, X509Certificate> mapAllCertificates(
        PKIMessage respPkiMessage, CertRepMessage certRepMessage
    )
        throws IOException, CertificateParsingException, CmpClientException {

        Map<X500Name, X509Certificate> certificates = new HashMap<>();

        CMPCertificate[] extraCerts = respPkiMessage.getExtraCerts();
        certificates.putAll(mapCertificates(extraCerts));

        CMPCertificate[] caPubsCerts = certRepMessage.getCaPubs();
        certificates.putAll(mapCertificates(caPubsCerts));

        return certificates;
    }

    private static Map<X500Name, X509Certificate> mapCertificates(
        CMPCertificate[] cmpCertificates)
        throws CertificateParsingException, CmpClientException, IOException {

        Map<X500Name, X509Certificate> certificates = new HashMap<>();
        if (cmpCertificates != null) {
            for (CMPCertificate certificate : cmpCertificates) {
                getCertFromByteArray(certificate.getEncoded(), X509Certificate.class)
                    .ifPresent(x509Certificate ->
                        certificates.put(extractSubjectDn(x509Certificate), x509Certificate)
                    );
            }
        }

        return certificates;
    }

    private static Cmpv2CertificationModel extractCertificationModel(
        Map<X500Name, X509Certificate> certificates, X509Certificate leafCertificate
    )
        throws CmpClientException {
        List<X509Certificate> certificateChain = new ArrayList<>();
        X509Certificate previousCertificateInChain;
        X509Certificate nextCertificateInChain = leafCertificate;
        do {
            certificateChain.add(nextCertificateInChain);
            certificates.remove(extractSubjectDn(nextCertificateInChain));
            previousCertificateInChain = nextCertificateInChain;
            nextCertificateInChain = certificates.get(extractIssuerDn(nextCertificateInChain));
            verify(previousCertificateInChain, nextCertificateInChain, null);
        }
        while (!isSelfSign(nextCertificateInChain));
        List<X509Certificate> trustedCertificates = new ArrayList<>(certificates.values());

        return new Cmpv2CertificationModel(certificateChain, trustedCertificates);
    }

    private static boolean isSelfSign(X509Certificate certificate) {
        return extractIssuerDn(certificate).equals(extractSubjectDn(certificate));
    }

    private static X500Name extractIssuerDn(X509Certificate x509Certificate) {
        return X500Name.getInstance(x509Certificate.getIssuerDN());
    }

    private static X500Name extractSubjectDn(X509Certificate x509Certificate) {
        return X500Name.getInstance(x509Certificate.getSubjectDN());
    }


    /**
     * Check the certificate with CA certificate.
     *
     * @param certificate          X.509 certificate to verify. May not be null.
     * @param caCertChain          Collection of X509Certificates. May not be null, an empty list or a Collection with
     *                             null entries.
     * @param date                 Date to verify at, or null to use current time.
     * @param pkixCertPathCheckers optional PKIXCertPathChecker implementations to use during cert path validation
     * @throws CmpClientException if certificate could not be validated
     */
    private static void verify(
        X509Certificate certificate,
        X509Certificate caCertChain,
        Date date,
        PKIXCertPathChecker... pkixCertPathCheckers)
        throws CmpClientException {
        try {
            verifyCertificates(certificate, caCertChain, date, pkixCertPathCheckers);
        } catch (CertPathValidatorException cpve) {
            CmpClientException cmpClientException =
                new CmpClientException(
                    "Invalid certificate or certificate not issued by specified CA: ", cpve);
            LOG.error("Invalid certificate or certificate not issued by specified CA: ", cpve);
            throw cmpClientException;
        } catch (CertificateException ce) {
            CmpClientException cmpClientException =
                new CmpClientException("Something was wrong with the supplied certificate", ce);
            LOG.error("Something was wrong with the supplied certificate", ce);
            throw cmpClientException;
        } catch (NoSuchProviderException nspe) {
            CmpClientException cmpClientException =
                new CmpClientException("BouncyCastle provider not found.", nspe);
            LOG.error("BouncyCastle provider not found.", nspe);
            throw cmpClientException;
        } catch (NoSuchAlgorithmException nsae) {
            CmpClientException cmpClientException =
                new CmpClientException("Algorithm PKIX was not found.", nsae);
            LOG.error("Algorithm PKIX was not found.", nsae);
            throw cmpClientException;
        } catch (InvalidAlgorithmParameterException iape) {
            CmpClientException cmpClientException =
                new CmpClientException(
                    "Either ca certificate chain was empty,"
                        + " or the certificate was on an inappropriate type for a PKIX path checker.",
                    iape);
            LOG.error(
                "Either ca certificate chain was empty, "
                    + "or the certificate was on an inappropriate type for a PKIX path checker.",
                iape);
            throw cmpClientException;
        }
    }

    private static void verifyCertificates(
        X509Certificate certificate,
        X509Certificate caCertChain,
        Date date,
        PKIXCertPathChecker[] pkixCertPathCheckers)
        throws CertificateException, NoSuchProviderException, InvalidAlgorithmParameterException,
        NoSuchAlgorithmException, CertPathValidatorException {
        if (caCertChain == null) {
            final String noRootCaCertificateMessage = "Server response does not contain proper root CA certificate";
            throw new CertificateException(noRootCaCertificateMessage);
        }
        LOG.debug(
            "Verifying certificate {} as part of cert chain with certificate {}",
            certificate.getSubjectDN().getName(),
            caCertChain.getSubjectDN().getName());
        CertPath cp = getCertPath(certificate);
        PKIXParameters params = getPkixParameters(caCertChain, date, pkixCertPathCheckers);
        CertPathValidator cpv =
            CertPathValidator.getInstance("PKIX", BouncyCastleProvider.PROVIDER_NAME);
        PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Certificate verify result:{} ", result);
        }
    }

    private static PKIXParameters getPkixParameters(
        X509Certificate caCertChain, Date date, PKIXCertPathChecker[] pkixCertPathCheckers)
        throws InvalidAlgorithmParameterException {
        TrustAnchor anchor = new TrustAnchor(caCertChain, null);
        PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
        for (final PKIXCertPathChecker pkixCertPathChecker : pkixCertPathCheckers) {
            params.addCertPathChecker(pkixCertPathChecker);
        }
        params.setRevocationEnabled(false);
        params.setDate(date);
        return params;
    }

    private static CertPath getCertPath(X509Certificate certificate)
        throws CertificateException, NoSuchProviderException {
        ArrayList<X509Certificate> certlist = new ArrayList<>();
        certlist.add(certificate);
        return CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME)
            .generateCertPath(certlist);
    }

    /**
     * Returns a CertificateFactory that can be used to create certificates from byte arrays and such.
     *
     * @param provider Security provider that should be used to create certificates, default BC is null is passed.
     * @return CertificateFactory for creating certificate
     */
    private static CertificateFactory getCertificateFactory(final String provider)
        throws CmpClientException {
        LOG.debug("Creating certificate Factory to generate certificate using provider {}", provider);
        final String prov;
        prov = Objects.requireNonNullElse(provider, BouncyCastleProvider.PROVIDER_NAME);
        try {
            return CertificateFactory.getInstance("X.509", prov);
        } catch (NoSuchProviderException nspe) {
            CmpClientException cmpClientException = new CmpClientException("NoSuchProvider: ", nspe);
            LOG.error("NoSuchProvider: ", nspe);
            throw cmpClientException;
        } catch (CertificateException ce) {
            CmpClientException cmpClientException = new CmpClientException("CertificateException: ", ce);
            LOG.error("CertificateException: ", ce);
            throw cmpClientException;
        }
    }

    /**
     * @param cert       byte array that contains certificate
     * @param returnType the type of Certificate to be returned, for example X509Certificate.class. Certificate.class
     *                   can be used if certificate type is unknown.
     * @throws CertificateParsingException if the byte array does not contain a proper certificate.
     */
    static <T extends Certificate> Optional<X509Certificate> getCertFromByteArray(
        byte[] cert, Class<T> returnType) throws CertificateParsingException, CmpClientException {
        LOG.debug("Retrieving certificate of type {} from byte array.", returnType);
        String prov = BouncyCastleProvider.PROVIDER_NAME;

        if (returnType.equals(X509Certificate.class)) {
            return parseX509Certificate(prov, cert);
        } else {
            LOG.debug("Certificate of type {} was skipped, because type of certificate is not 'X509Certificate'.",
                returnType);
            return Optional.empty();
        }
    }


    /**
     * Parse a X509Certificate from an array of bytes
     *
     * @param provider a provider name
     * @param cert     a byte array containing an encoded certificate
     * @return a decoded X509Certificate
     * @throws CertificateParsingException if the byte array wasn't valid, or contained a certificate other than an X509
     *                                     Certificate.
     */
    private static Optional<X509Certificate> parseX509Certificate(String provider, byte[] cert)
        throws CertificateParsingException, CmpClientException {
        LOG.debug("Parsing X509Certificate from bytes with provider {}", provider);
        final CertificateFactory cf = getCertificateFactory(provider);
        X509Certificate result;
        try {
            result = (X509Certificate) Objects.requireNonNull(cf).generateCertificate(new ByteArrayInputStream(cert));
            return Optional.ofNullable(result);
        } catch (CertificateException ce) {
            throw new CertificateParsingException("Could not parse byte array as X509Certificate ", ce);
        }
    }
}
