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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmpUtil.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int RANDOM_BYTE_LENGTH = 16;
    public static final int RANDOM_SEED = 1000;

    private CmpUtil() {
    }

    /**
     * Validates specified object reference is not null.
     *
     * @param argument T - the type of the reference.
     * @param message  message - detail message to be used in the event that a NullPointerException is
     *                 thrown.
     * @return The Object if not null
     */
    public static <T> T notNull(T argument, String message) {
        return Objects.requireNonNull(argument, message + " must not be null");
    }

    /**
     * Validates String object reference is not null and not empty.
     *
     * @param stringArg String Object that need to be validated.
     * @return boolean
     */
    public static boolean isNullOrEmpty(String stringArg) {
        return (stringArg != null && !stringArg.trim().isEmpty());
    }

    /**
     * Creates a random number than can be used for sendernonce, transactionId and salts.
     *
     * @return bytes containing a random number string representing a nonce
     */
    static byte[] createRandomBytes() {
        LOGGER.info("Generating random array of bytes");
        byte[] randomBytes = new byte[RANDOM_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * Creates a random integer than can be used to represent a transactionId or determine the number
     * iterations in a protection algorithm.
     *
     * @return bytes containing a random number string representing a nonce
     */
    static int createRandomInt(int range) {
        LOGGER.info("Generating random integer");
        return SECURE_RANDOM.nextInt(range) + RANDOM_SEED;
    }

    /**
     * Generates protected bytes of a combined PKIHeader and PKIBody.
     *
     * @param header Header of PKIMessage containing common parameters
     * @param body   Body of PKIMessage containing specific information for message
     * @return bytes representing the PKIHeader and PKIBody thats to be protected
     */
    static byte[] generateProtectedBytes(PKIHeader header, PKIBody body) throws CmpClientException {
        LOGGER.info("Generating array of bytes representing PkiHeader and PkiBody");
        byte[] res;
        ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(header);
        vector.add(body);
        ASN1Encodable protectedPart = new DERSequence(vector);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DEROutputStream out = new DEROutputStream(baos);
            out.writeObject(protectedPart);
            res = baos.toByteArray();
        } catch (IOException ioe) {
            CmpClientException cmpClientException =
                    new CmpClientException("IOException occurred while creating protectedBytes", ioe);
            LOGGER.error("IOException occurred while creating protectedBytes");
            throw cmpClientException;
        }
        return res;
    }

    /**
     * Generates a PKIHeader Builder object.
     *
     * @param subjectDn     distinguished name of Subject
     * @param issuerDn      distinguished name of external CA
     * @param protectionAlg protection Algorithm used to protect PKIMessage
     * @return PKIHeaderBuilder
     */
    static PKIHeader generatePkiHeader(
            X500Name subjectDn, X500Name issuerDn, AlgorithmIdentifier protectionAlg, String senderKid) {
        LOGGER.info("Generating a Pki Header Builder");
        PKIHeaderBuilder pkiHeaderBuilder =
                new PKIHeaderBuilder(
                        PKIHeader.CMP_2000, new GeneralName(subjectDn), new GeneralName(issuerDn));

        pkiHeaderBuilder.setMessageTime(new ASN1GeneralizedTime(new Date()));
        pkiHeaderBuilder.setSenderNonce(new DEROctetString(createRandomBytes()));
        pkiHeaderBuilder.setTransactionID(new DEROctetString(createRandomBytes()));
        pkiHeaderBuilder.setProtectionAlg(protectionAlg);
        pkiHeaderBuilder.setGeneralInfo(new InfoTypeAndValue(CMPObjectIdentifiers.it_implicitConfirm));
        pkiHeaderBuilder.setSenderKID(new DEROctetString(senderKid.getBytes()));

        return pkiHeaderBuilder.build();
    }
}
