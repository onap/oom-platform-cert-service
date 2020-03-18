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

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.io.pem.PemObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Pkcs10CertificationRequestFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pkcs10CertificationRequestFactory.class);

    public Optional<PKCS10CertificationRequest> createPkcs10CertificationRequest(PemObject pemObject) {
        try {
            LOGGER.debug("Creating certification request from pem object");
            return Optional.of(new PKCS10CertificationRequest(pemObject.getContent()));
        } catch (DecoderException | IOException e) {
            LOGGER.error("Exception occurred during creation of certification request:", e);
            return Optional.empty();
        }
    }
}
