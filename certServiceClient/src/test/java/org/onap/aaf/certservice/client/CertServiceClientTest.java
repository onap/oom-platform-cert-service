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
package org.onap.aaf.certservice.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.aaf.certservice.client.certification.KeyPairFactory;

import java.security.KeyPair;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.KEY_SIZE;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.RSA_ENCRYPTION_ALGORITHM;

@ExtendWith(MockitoExtension.class)
class CertServiceClientTest {
    private static final int DUMMY_EXIT_CODE = 888;
    @Spy
    AppExitHandler appExitHandler = new AppExitHandler();

    @Test
    public void shouldExitWithDefinedExitCode_onGenerateKeyPairCallWhereExitableExceptionIsThrown() {
        //  given
        KeyPairFactory keyPairFactory = mock(KeyPairFactory.class);
        when(keyPairFactory.create()).thenThrow(new DummyExitableException());
        doNothing().when(appExitHandler).exit(DUMMY_EXIT_CODE);
        CertServiceClient certServiceClient = new CertServiceClient(appExitHandler);
        //  when
        Optional<KeyPair> keyPair = certServiceClient.generateKeyPair(keyPairFactory);
        //  then
        verify(appExitHandler).exit(DUMMY_EXIT_CODE);
        assertThat(keyPair).isEmpty();
    }

    @Test
    public void shouldReturnKeyPair_onGenerateKeyPairCall() {
        //  given
        KeyPairFactory keyPairFactory = new KeyPairFactory(RSA_ENCRYPTION_ALGORITHM, KEY_SIZE);
        CertServiceClient certServiceClient = new CertServiceClient(appExitHandler);
        //  when
        Optional<KeyPair> keyPair = certServiceClient.generateKeyPair(keyPairFactory);
        //  then
        assertThat(keyPair).hasValueSatisfying(value -> assertThat(value).isInstanceOf(KeyPair.class));
    }

}