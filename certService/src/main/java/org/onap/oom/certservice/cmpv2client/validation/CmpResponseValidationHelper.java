/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.oom.certservice.cmpv2client.validation;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.impl.CmpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmpResponseValidationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CmpResponseValidationHelper.class);

    private CmpResponseValidationHelper() {
    }

    /**
     * Verifies the signature of the response message using our public key
     *
     * @param respPkiMessage PKIMessage we wish to verify signature for
     * @param pk             public key used to verify signature.
     * @throws CmpClientException
     */
    static void verifySignature(PKIMessage respPkiMessage, PublicKey pk)
            throws CmpClientException {
        final byte[] protBytes = getProtectedBytes(respPkiMessage);
        final DERBitString derBitString = respPkiMessage.getProtection();
        try {
            final Signature signature =
                    Signature.getInstance(
                            PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(),
                            BouncyCastleProvider.PROVIDER_NAME);
            signature.initVerify(pk);
            signature.update(protBytes);
            signature.verify(derBitString.getBytes());
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidKeyException
                | SignatureException e) {
            CmpClientException clientException =
                    new CmpClientException("Signature Verification failed", e);
            LOG.error("Signature Verification failed", e);
            throw clientException;
        }
    }

    /**
     * verify the password based protection within the response message
     *
     * @param respPkiMessage   PKIMessage we want to verify password based protection for
     * @param initAuthPassword password used to decrypt protection
     * @param protectionAlgo   protection algorithm we can use to decrypt protection
     * @throws CmpClientException
     */
    static void verifyPasswordBasedProtection(
            PKIMessage respPkiMessage, String initAuthPassword, AlgorithmIdentifier protectionAlgo)
            throws CmpClientException {
        final byte[] protectedBytes = getProtectedBytes(respPkiMessage);
        final PBMParameter pbmParamSeq = PBMParameter.getInstance(protectionAlgo.getParameters());
        if (Objects.nonNull(pbmParamSeq)) {
            try {
                byte[] basekey = getBaseKeyFromPbmParameters(pbmParamSeq, initAuthPassword);
                final Mac mac = getMac(protectedBytes, pbmParamSeq, basekey);
                final byte[] outBytes = mac.doFinal();
                final byte[] protectionBytes = respPkiMessage.getProtection().getBytes();
                if (!Arrays.equals(outBytes, protectionBytes)) {
                    LOG.error("protectionBytes don't match passwordBasedProtection, authentication failed");
                    throw new CmpClientException(
                            "protectionBytes don't match passwordBasedProtection, authentication failed");
                }
            } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException ex) {
                CmpClientException cmpClientException =
                        new CmpClientException("Error while validating CMP response ", ex);
                LOG.error("Error while validating CMP response ", ex);
                throw cmpClientException;
            }
        }
    }

    static void checkImplicitConfirm(PKIHeader header) {
        InfoTypeAndValue[] infos = header.getGeneralInfo();
        if (Objects.nonNull(infos)) {
            if (CMPObjectIdentifiers.it_implicitConfirm.equals(getImplicitConfirm(infos))) {
                LOG.info("Implicit Confirm on certificate from server.");
            } else {
                LOG.debug("No Implicit confirm in Response");
            }
        } else {
            LOG.debug("No general Info in header of response, cannot verify implicit confirm");
        }
    }

    /**
     * Create a base key to use for verifying the PasswordBasedMac on a PKIMessage
     *
     * @param pbmParamSeq      parameters recieved in PKIMessage used with password
     * @param initAuthPassword password used to decrypt the basekey
     * @return bytes representing the basekey
     * @throws CmpClientException thrown if algorithem exceptions occur for the message digest
     */
    private static byte[] getBaseKeyFromPbmParameters(
            PBMParameter pbmParamSeq, String initAuthPassword) throws CmpClientException {
        final int iterationCount = pbmParamSeq.getIterationCount().getPositiveValue().intValue();
        LOG.info("Iteration count is: {}", iterationCount);
        final AlgorithmIdentifier owfAlg = pbmParamSeq.getOwf();
        LOG.info("One Way Function type is: {}", owfAlg.getAlgorithm().getId());
        final byte[] salt = pbmParamSeq.getSalt().getOctets();
        final byte[] raSecret = initAuthPassword != null ? initAuthPassword.getBytes() : new byte[0];
        byte[] basekey = new byte[raSecret.length + salt.length];
        System.arraycopy(raSecret, 0, basekey, 0, raSecret.length);
        System.arraycopy(salt, 0, basekey, raSecret.length, salt.length);
        try {
            final MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            owfAlg.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
            for (int i = 0; i < iterationCount; i++) {
                basekey = messageDigest.digest(basekey);
                messageDigest.reset();
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error("ProtectionBytes don't match passwordBasedProtection, authentication failed");
            throw new CmpClientException(
                    "ProtectionBytes don't match passwordBasedProtection, authentication failed", ex);
        }
        return basekey;
    }

    private static ASN1ObjectIdentifier getImplicitConfirm(InfoTypeAndValue[] info) {
        return info[0].getInfoType();
    }

    /**
     * Get cryptographical Mac we can use to decrypt our PKIMessage
     *
     * @param protectedBytes Protected bytes representing the PKIMessage
     * @param pbmParamSeq    Parameters used to decrypt PKIMessage, including mac algorithm used
     * @param basekey        Key used alongside mac Oid to create secret key for decrypting PKIMessage
     * @return Mac that's ready to return decrypted bytes
     * @throws NoSuchAlgorithmException Possibly thrown trying to get mac instance
     * @throws NoSuchProviderException  Possibly thrown trying to get mac instance
     * @throws InvalidKeyException      Possibly thrown trying to initialize mac using secretkey
     */
    private static Mac getMac(byte[] protectedBytes, PBMParameter pbmParamSeq, byte[] basekey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        final AlgorithmIdentifier macAlg = pbmParamSeq.getMac();
        LOG.info("Mac type is: {}", macAlg.getAlgorithm().getId());
        final String macOid = macAlg.getAlgorithm().getId();
        final Mac mac = Mac.getInstance(macOid, BouncyCastleProvider.PROVIDER_NAME);
        final SecretKey key = new SecretKeySpec(basekey, macOid);
        mac.init(key);
        mac.reset();
        mac.update(protectedBytes, 0, protectedBytes.length);
        return mac;
    }

    /**
     * Converts the header and the body of a PKIMessage to an ASN1Encodable and returns the as a byte
     * array
     *
     * @param msg PKIMessage to get protected bytes from
     * @return the PKIMessage's header and body in byte array
     */
    private static byte[] getProtectedBytes(PKIMessage msg) throws CmpClientException {
        return CmpUtil.generateProtectedBytes(msg.getHeader(), msg.getBody());
    }
}
