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

package org.onap.oom.certservice.certification;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PemObjectFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PemObjectFactory.class);

    public Optional<PemObject> createPemObject(String pem) {

        try (StringReader stringReader = new StringReader(pem);
             PemReader pemReader = new PemReader(stringReader)) {
            return Optional.ofNullable(pemReader.readPemObject());
        } catch (DecoderException | IOException e) {
            LOGGER.error("Exception occurred during creation of PEM:", e);
            return Optional.empty();
        }
    }

}
