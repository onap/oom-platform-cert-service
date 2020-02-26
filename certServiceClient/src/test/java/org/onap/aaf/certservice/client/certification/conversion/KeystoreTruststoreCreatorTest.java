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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.PrivateKey;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.certification.exception.PemToPKCS12ConverterException;

class KeystoreTruststoreCreatorTest {

    private PKCS12FilesCreator filesCreator = mock(PKCS12FilesCreator.class);
    private RandomPasswordGenerator passwordGenerator = mock(RandomPasswordGenerator.class);
    private PemToPKCS12Converter converter = mock(PemToPKCS12Converter.class);
    private PrivateKey privateKey = mock(PrivateKey.class);

    @Test
    void createKeystoreShouldCallRequiredMethods() throws PemToPKCS12ConverterException {
        // given
        final Password password = new Password("d9D_u8LooYaXH4G48DtN#vw0");
        final List<String> certificates = List.of("a", "b");
        final int passwordLength = 24;
        final String alias = "certificate";
        final byte[] keystoreBytes = "this is a keystore test".getBytes();
        KeystoreTruststoreCreator creator = new KeystoreTruststoreCreator(filesCreator, passwordGenerator, converter);

        // when
        when(passwordGenerator.generate(passwordLength)).thenReturn(password);
        when(converter.convertKeystore(certificates, password, alias, privateKey)).thenReturn(keystoreBytes);
        creator.createKeystore(certificates, privateKey);

        // then
        verify(passwordGenerator, times(1)).generate(passwordLength);
        verify(converter, times(1)).convertKeystore(certificates, password, alias, privateKey);
        verify(filesCreator, times(1)).saveKeystoreData(keystoreBytes, password.getPassword());
    }

    @Test
    void createTruststoreShouldCallRequiredMethods() throws PemToPKCS12ConverterException {
        // given
        final Password password = new Password("d9D_u8LooYaXH4G48DtN#vw0");
        final List<String> certificates = List.of("a", "b");
        final int passwordLength = 24;
        final String alias = "trusted-certificate-";
        final byte[] truststoreBytes = "this is a truststore test".getBytes();
        KeystoreTruststoreCreator creator = new KeystoreTruststoreCreator(filesCreator, passwordGenerator, converter);

        // when
        when(passwordGenerator.generate(passwordLength)).thenReturn(password);
        when(converter.convertTruststore(certificates, password, alias)).thenReturn(truststoreBytes);
        creator.createTruststore(certificates);

        // then
        verify(passwordGenerator, times(1)).generate(passwordLength);
        verify(converter, times(1)).convertTruststore(certificates, password, alias);
        verify(filesCreator, times(1)).saveTruststoreData(truststoreBytes, password.getPassword());
    }
}