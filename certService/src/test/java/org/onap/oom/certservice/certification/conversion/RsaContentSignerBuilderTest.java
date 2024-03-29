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

package org.onap.oom.certservice.certification.conversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.onap.oom.certservice.certification.TestUtils.createCsrModel;

import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.model.CsrModel;

class RsaContentSignerBuilderTest {

    private RsaContentSignerBuilder rsaContentSignerBuilder;

    @BeforeEach
    void setUp() {
        rsaContentSignerBuilder = new RsaContentSignerBuilder();
    }

    @Test
    void shouldBuildProperContentSignerWhenProvidedCertificationRequestAndPrivateKey()
            throws IOException, OperatorCreationException, DecryptionException {
        // Given
        CsrModel testCsrModel = createCsrModel();
        PKCS10CertificationRequest testCertificationRequest = testCsrModel.getCsr();
        PrivateKey testPrivateKey = testCsrModel.getPrivateKey();

        // When
        ContentSigner createdContentSigner = rsaContentSignerBuilder.build(testCertificationRequest, testPrivateKey);

        // Then
        assertThat(createdContentSigner.getAlgorithmIdentifier())
                .isEqualTo(testCertificationRequest.getSignatureAlgorithm());
    }

}
