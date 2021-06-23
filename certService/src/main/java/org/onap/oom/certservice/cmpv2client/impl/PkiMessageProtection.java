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

package org.onap.oom.certservice.cmpv2client.impl;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;

import static org.onap.oom.certservice.cmpv2client.impl.CmpUtil.generateProtectedBytes;

/**
 * Representation of PKIMessage protection. Complies with RFC4210 (Certificate Management Protocol
 * (CMP)) and RFC4211 (Certificate Request Message Format (CRMF)) standards.
 */
public interface PkiMessageProtection {

    Logger LOG = LoggerFactory.getLogger(PkiMessageProtection.class);

    /**
     * Returns Algorithm Identifier for protection of PKIMessage.
     *
     * @return Algorithm Identifier.
     */
    AlgorithmIdentifier getAlgorithmIdentifier();

    /**
     * Takes encoded bytes of PKIMessage (PKIHeader and PKIBody) and generates protection bytes.
     *
     * @return bytes representing protection.
     */
    byte[] generateProtectionBytes(byte[] protectedBytes) throws GeneralSecurityException;

    /**
     * Takes PKIHeader and PKIBody as parameters and generates protection bytes.
     *
     * @return bytes representing protection wrapped into DERBitString object.
     */
    default DERBitString generatePkiMessageProtection(PKIHeader pkiHeader, PKIBody pkiBody) throws CmpClientException {
        try {
            byte[] protectedBytes = generateProtectedBytes(pkiHeader, pkiBody);
            byte[] protectionBytes = generateProtectionBytes(protectedBytes);
            return new DERBitString(protectionBytes);
        } catch (GeneralSecurityException ex) {
            CmpClientException cmpClientException =
                    new CmpClientException(
                            "Exception occurred while generating protection for PKIMessage", ex);
            LOG.error("Exception occurred while generating the protection for PKIMessage");
            throw cmpClientException;
        }
    }

}
