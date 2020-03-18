/*
 * ============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.certification;



import java.io.IOException;
import java.io.StringWriter;
import java.security.PrivateKey;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.aaf.certservice.client.certification.exception.PkEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateKeyToPemEncoder {

    public static final String PEM_OBJECT_TYPE = "RSA PRIVATE KEY";
    private final Logger LOGGER = LoggerFactory.getLogger(PrivateKeyToPemEncoder.class);

    public String encodePrivateKeyToPem(PrivateKey pk) throws PkEncodingException {
        LOGGER.info("Encoding PrivateKey to PEM");
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(new PemObject(PEM_OBJECT_TYPE, pk.getEncoded()));
        } catch (IOException e) {
            LOGGER.error("Encode of private key to PEM failed. Exception message: {}", e.getMessage());
            throw new PkEncodingException(e);
        }
        return stringWriter.toString();
    }
}
