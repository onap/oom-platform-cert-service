/*============LICENSE_START=======================================================
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

package org.onap.aaf.certservice.client.certification.conversion;

import java.security.PrivateKey;
import java.util.List;
import org.onap.aaf.certservice.client.certification.exception.PemToPKCS12ConverterException;

public class KeystoreTruststoreCreator {

    private static final String CERTIFICATE_ALIAS = "certificate";
    private static final String TRUSTED_CERTIFICATE_ALIAS = "trusted-certificate-";
    private static final int PASSWORD_LENGTH = 24;
    private final RandomPasswordGenerator generator;
    private final PemToPKCS12Converter converter;
    private final PKCS12FilesCreator creator;

    public KeystoreTruststoreCreator(PKCS12FilesCreator creator, RandomPasswordGenerator generator,
        PemToPKCS12Converter converter) {
        this.generator = generator;
        this.converter = converter;
        this.creator = creator;
    }

    public void createKeystore(List<String> data, PrivateKey privateKey)
        throws PemToPKCS12ConverterException {
        Password password = generator.generate(PASSWORD_LENGTH);
        creator.saveKeystoreData(converter.convertKeystore(data, password, CERTIFICATE_ALIAS, privateKey),
            password.getPassword());
    }

    public void createTruststore(List<String> data)
        throws PemToPKCS12ConverterException {
        Password password = generator.generate(PASSWORD_LENGTH);
        creator.saveTruststoreData(converter.convertTruststore(data, password, TRUSTED_CERTIFICATE_ALIAS),
            password.getPassword());
    }
}
