/*
 * ============LICENSE_START=======================================================
 * PROJECT
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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
 * ============LICENSE_END=========================================================
 */

package org.onap.aaf.certservice.certification;

import java.io.IOException;
import java.util.Base64;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.aaf.certservice.certification.exceptions.CsrDecryptionException;
import org.onap.aaf.certservice.certification.exceptions.PemDecryptionException;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class CsrModelFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrModelFactory.class);
    private final PemObjectFactory pemObjectFactory = new PemObjectFactory();

    public CsrModel createCsrModel(StringBase64 csr, StringBase64 privateKey)
            throws CsrDecryptionException, PemDecryptionException {
        LOGGER.debug("Decoded CSR: \n{}", csr);

        try {
            PemObject pemObject = pemObjectFactory.createPemObject(csr.asString());
            PKCS10CertificationRequest decodedCsr = new PKCS10CertificationRequest(
                    pemObject.getContent()
            );
            PemObject decodedPrivateKey = pemObjectFactory.createPemObject(privateKey.asString());
            return new CsrModel(decodedCsr, decodedPrivateKey);
        } catch (IOException e) {
            throw new CsrDecryptionException("Incorrect CSR, decryption failed", e);
        }
    }

    public static class StringBase64 {
        private final String value;
        private final Base64.Decoder decoder = Base64.getDecoder();

        public StringBase64(String value) {
            this.value = value;
        }

        public String asString() {
            return new String(decoder.decode(value));
        }
    }
}


