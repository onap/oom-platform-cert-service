/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.certservice.postprocessor.merger.model;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.postprocessor.api.ExitableException;
import org.onap.oom.certservice.postprocessor.merger.exception.AliasConflictException;
import org.onap.oom.certservice.postprocessor.merger.exception.MissingTruststoreException;
import org.onap.oom.certservice.postprocessor.merger.model.certificate.CertificateWithAlias;

class JavaTruststoreTest {

    @Test
    void throwExceptionWhenAliasConflictDetected() throws Exception {
        //given
        Truststore p12Truststore = TestCertificateProvider.getSampleP12Truststore();

        List<CertificateWithAlias> certificateFromJks = TestCertificateProvider
            .getSampleJksTruststoreFile().getCertificates();

        //when //then
        assertThatExceptionOfType(AliasConflictException.class)
            .isThrownBy(() -> p12Truststore.addCertificates(certificateFromJks));
    }

    @Test
    void throwExceptionWhenFileNotContainsTruststoreEntry() throws ExitableException {
        //given
        Truststore p12Truststore = TestCertificateProvider.getSampleP12Keystore();

        //when//then
        assertThatExceptionOfType(MissingTruststoreException.class)
            .isThrownBy(() -> p12Truststore.getCertificates());
    }

}
