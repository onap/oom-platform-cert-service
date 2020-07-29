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

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.model.CsrModel;

import java.io.IOException;
import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.onap.oom.certservice.certification.TestUtils.createCsrModel;

public class X509CertificateBuilderTest {

    private X509CertificateBuilder certificateBuilder;


    @BeforeEach
    void setUp() {
        certificateBuilder = new X509CertificateBuilder();
    }

    @Test
    void shouldBuildCertificateBuilderWhenGivenProperCertificationRequest()
            throws DecryptionException, IOException, OperatorCreationException {
        // Given
        CsrModel testCsrModel = createCsrModel();
        PKCS10CertificationRequest testCertificationRequest = testCsrModel.getCsr();
        PrivateKey testPrivateKey = testCsrModel.getPrivateKey();
        RsaContentSignerBuilder rsaContentSignerBuilder = new RsaContentSignerBuilder();
        ContentSigner createdContentSigner = rsaContentSignerBuilder.build(testCertificationRequest, testPrivateKey);

        // When
        X509v3CertificateBuilder certificateBuilder = this.certificateBuilder.build(testCertificationRequest);
        X509CertificateHolder certificateHolder = certificateBuilder.build(createdContentSigner);

        // Then
        assertThat(certificateHolder.getIssuer())
                .isEqualToComparingFieldByField(testCsrModel.getSubjectData());
        assertThat(certificateHolder.getSubjectPublicKeyInfo())
                .isEqualToComparingFieldByField(testCertificationRequest.getSubjectPublicKeyInfo());
    }
}
