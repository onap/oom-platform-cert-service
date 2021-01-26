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

import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.generateProtectedBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmpMessageHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CmpMessageHelper.class);
    private static final AlgorithmIdentifier OWF_ALGORITHM =
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.14.3.2.26"));
    private static final AlgorithmIdentifier MAC_ALGORITHM =
            new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.6.1.5.5.8.1.2"));
    private static final ASN1ObjectIdentifier PASSWORD_BASED_MAC =
            new ASN1ObjectIdentifier("1.2.840.113533.7.66.13");

    private CmpMessageHelper() {
    }

    /**
     * Creates an Optional Validity, which is used to specify how long the returned cert should be
     * valid for.
     *
     * @param notBefore Date specifying certificate is not valid before this date.
     * @param notAfter  Date specifying certificate is not valid after this date.
     * @return {@link OptionalValidity} that can be set for certificate on external CA.
     */
    public static OptionalValidity generateOptionalValidity(
            final Date notBefore, final Date notAfter) {
        LOG.info("Generating Optional Validity from Date objects");
        ASN1EncodableVector optionalValidityV = new ASN1EncodableVector();
        if (notBefore != null) {
            Time nb = new Time(notBefore);
            optionalValidityV.add(new DERTaggedObject(true, 0, nb));
        }
        if (notAfter != null) {
            Time na = new Time(notAfter);
            optionalValidityV.add(new DERTaggedObject(true, 1, na));
        }
        return OptionalValidity.getInstance(new DERSequence(optionalValidityV));
    }

    /**
     * Create Extensions from Subject Alternative Names.
     *
     * @return {@link Extensions}.
     */
    public static Extensions generateExtension(final GeneralName[] sansArray)
            throws CmpClientException {
        LOG.info("Generating Extensions from Subject Alternative Names");
        final ExtensionsGenerator extGenerator = new ExtensionsGenerator();
        // KeyUsage
        try {
            extGenerator.addExtension(Extension.keyUsage, false, getKeyUsage());
            extGenerator.addExtension(Extension.extendedKeyUsage, false, getExtendedKeyUsage());
            extGenerator.addExtension(
                    Extension.subjectAlternativeName, false, new GeneralNames(sansArray));
        } catch (IOException ioe) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Exception occurred while creating extensions for PKIMessage", ioe);
            LOG.error("Exception occurred while creating extensions for PKIMessage");
            throw cmpClientException;
        }
        return extGenerator.generate();
    }

    /**
     * Method generates Proof-of-Possession (POP) of Private Key. To allow a CA/RA to properly
     * validity binding between an End Entity and a Key Pair, the PKI Operations specified here make
     * it possible for an End Entity to prove that it has possession of the Private Key corresponding
     * to the Public Key for which a Certificate is requested.
     *
     * @param certRequest Certificate request that requires proof of possession
     * @param keypair     keypair associated with the subject sending the certificate request
     * @return {@link ProofOfPossession}.
     * @throws CmpClientException A general-purpose Cmp client exception.
     */
    public static ProofOfPossession generateProofOfPossession(
            final CertRequest certRequest, final KeyPair keypair) throws CmpClientException {
        ProofOfPossession proofOfPossession;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
            derOutputStream.writeObject(certRequest);

            byte[] popoProtectionBytes = byteArrayOutputStream.toByteArray();
            final String sigalg = PKCSObjectIdentifiers.sha256WithRSAEncryption.getId();
            final Signature signature = Signature.getInstance(sigalg, BouncyCastleProvider.PROVIDER_NAME);
            signature.initSign(keypair.getPrivate());
            signature.update(popoProtectionBytes);
            DERBitString bs = new DERBitString(signature.sign());

            proofOfPossession =
                    new ProofOfPossession(
                            new POPOSigningKey(
                                    null, new AlgorithmIdentifier(new ASN1ObjectIdentifier(sigalg)), bs));
        } catch (IOException
                | NoSuchProviderException
                | NoSuchAlgorithmException
                | InvalidKeyException
                | SignatureException ex) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Exception occurred while creating proof of possession for PKIMessage", ex);
            LOG.error("Exception occurred while creating proof of possession for PKIMessage");
            throw cmpClientException;
        }
        return proofOfPossession;
    }

    /**
     * Generic code to create Algorithm Identifier for protection of PKIMessage.
     *
     * @return Algorithm Identifier
     */
    public static AlgorithmIdentifier protectionAlgoIdentifier(int iterations, byte[] salt) {
        ASN1Integer iteration = new ASN1Integer(iterations);
        DEROctetString derSalt = new DEROctetString(salt);

        PBMParameter pp = new PBMParameter(derSalt, OWF_ALGORITHM, iteration, MAC_ALGORITHM);
        return new AlgorithmIdentifier(PASSWORD_BASED_MAC, pp);
    }

    /**
     * Adds protection to the PKIMessage via a specified protection algorithm.
     *
     * @param password  password used to authenticate PkiMessage with external CA
     * @param pkiHeader Header of PKIMessage containing generic details for any PKIMessage
     * @param pkiBody   Body of PKIMessage containing specific details for certificate request
     * @return Protected Pki Message
     * @throws CmpClientException Wraps several exceptions into one general-purpose exception.
     */
    public static PKIMessage protectPkiMessage(
            PKIHeader pkiHeader, PKIBody pkiBody, String password, int iterations, byte[] salt)
            throws CmpClientException {

        byte[] raSecret = password.getBytes();
        byte[] basekey = new byte[raSecret.length + salt.length];
        System.arraycopy(raSecret, 0, basekey, 0, raSecret.length);
        System.arraycopy(salt, 0, basekey, raSecret.length, salt.length);
        byte[] out;
        try {
            MessageDigest dig =
                    MessageDigest.getInstance(
                            OWF_ALGORITHM.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
            for (int i = 0; i < iterations; i++) {
                basekey = dig.digest(basekey);
                dig.reset();
            }
            byte[] protectedBytes = generateProtectedBytes(pkiHeader, pkiBody);
            Mac mac =
                    Mac.getInstance(MAC_ALGORITHM.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
            SecretKey key = new SecretKeySpec(basekey, MAC_ALGORITHM.getAlgorithm().getId());
            mac.init(key);
            mac.reset();
            mac.update(protectedBytes, 0, protectedBytes.length);
            out = mac.doFinal();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException ex) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Exception occurred while generating proof of possession for PKIMessage", ex);
            LOG.error("Exception occured while generating the proof of possession for PKIMessage");
            throw cmpClientException;
        }
        DERBitString bs = new DERBitString(out);

        return new PKIMessage(pkiHeader, pkiBody, bs);
    }

    private static KeyUsage getKeyUsage() {
        return new KeyUsage(
            KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.nonRepudiation);
    }

    private static ExtendedKeyUsage getExtendedKeyUsage() {
        return new ExtendedKeyUsage(
            new KeyPurposeId[]{KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth});
    }
}
