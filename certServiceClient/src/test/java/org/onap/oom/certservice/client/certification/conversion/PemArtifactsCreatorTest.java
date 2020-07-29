/*============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.certification.conversion;

import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.api.ExitableException;
import org.onap.oom.certservice.client.certification.PrivateKeyToPemEncoder;
import org.onap.oom.certservice.client.certification.writer.CertFileWriter;

import java.security.PrivateKey;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PemArtifactsCreatorTest {
    private static final String KEYSTORE_PEM = "keystore.pem";
    private static final String TRUSTSTORE_PEM = "truststore.pem";
    private static final String KEY_PEM = "key.pem";
    private static final String KEY = "my private key";
    private CertFileWriter certFileWriter = mock(CertFileWriter.class);
    private PrivateKey privateKey = mock(PrivateKey.class);
    private PrivateKeyToPemEncoder pkEncoder = mock(PrivateKeyToPemEncoder.class);

    @Test
    void pemArtifactsCreatorShouldCallRequiredMethods() throws ExitableException {
        // given
        final PemArtifactsCreator creator = new PemArtifactsCreator(certFileWriter, pkEncoder);

        // when
        when(pkEncoder.encodePrivateKeyToPem(privateKey)).thenReturn(KEY);
        creator.create(List.of("one", "two"), List.of("three", "four"), privateKey);

        // then
        verify(certFileWriter, times(1)).saveData("one\ntwo".getBytes(), KEYSTORE_PEM);
        verify(certFileWriter, times(1)).saveData("three\nfour".getBytes(), TRUSTSTORE_PEM);
        verify(certFileWriter, times(1)).saveData(KEY.getBytes(), KEY_PEM);
    }
}
