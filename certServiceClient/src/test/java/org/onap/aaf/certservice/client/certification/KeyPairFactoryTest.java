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

package org.onap.aaf.certservice.client.certification;

import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.certification.exception.KeyPairGenerationException;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyPairFactoryTest {
    private static final String NOT_EXISTING_ENCRYPTION_ALGORITHM = "FAKE_ALGORITHM";

    @Test
    public void shouldProvideKeyPair_whenCreateKeyPairCalledWithCorrectArguments() throws KeyPairGenerationException {
        //  given
        KeyPairFactory keyPairFactory = new KeyPairFactory(EncryptionAlgorithmConstants.RSA_ENCRYPTION_ALGORITHM,
                EncryptionAlgorithmConstants.KEY_SIZE);
        //  when
        KeyPair keyPair = keyPairFactory.create();
        // then
        assertThat(keyPair).isInstanceOf(KeyPair.class);
    }

    @Test
    public void shouldThrowKeyPairGenerationException_whenCreateTryCalledOnNotExistingAlgorithm() {
        //  given
        KeyPairFactory keyPairFactory = new KeyPairFactory(NOT_EXISTING_ENCRYPTION_ALGORITHM,
                EncryptionAlgorithmConstants.KEY_SIZE);
        //  when, then
        assertThatThrownBy(keyPairFactory::create).isInstanceOf(KeyPairGenerationException.class);
    }

}