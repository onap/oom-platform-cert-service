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

package org.onap.oom.truststoremerger.certification.file;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;

class P12TruststoreTest {

    private static final int EXPECTED_ONE = 1;
    public static final int EXPECTED_THREE = 3;

    @Test
    void P12TruststoreShouldReadCertificatesFromFile() throws ExitableException {
        //given
        P12Truststore p12Truststore = CertificatesTestFileManager.getSampleP12Truststore();

        //when
        List<CertificateWrapper> certificateWrappers = p12Truststore.getCertificates();

        //then
        assertThat(certificateWrappers).hasSize(EXPECTED_ONE);
    }


    @Test
    void P12TruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        P12Truststore p12Truststore = CertificatesTestFileManager.createTmpP12TruststoreFile();
        List<CertificateWrapper> jksTruststoreCertificates = CertificatesTestFileManager
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWrapper> pemTruststoreCertificates = CertificatesTestFileManager.getSamplePemTruststoreFile()
            .getCertificates();

        //when
        p12Truststore.addCertificate(jksTruststoreCertificates);
        p12Truststore.addCertificate(pemTruststoreCertificates);

        //then
        assertThat(p12Truststore.getCertificates()).hasSize(EXPECTED_THREE);
    }

    @AfterAll
    static void removeTemporaryFiles() throws IOException {
        CertificatesTestFileManager.removeTemporaryFiles();
    }
}
