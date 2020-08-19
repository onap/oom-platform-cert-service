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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.onap.oom.truststoremerger.certification.file.CertificatesTestFileManager.getSampleJksTruststoreFile;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.exception.AliasConflictException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;

class ExitableKeystoreInstanceTest {


    @Test
    void throwExceptionWhenAliasConflictDetected() throws Exception {
        //given
        P12Truststore p12Truststore = CertificatesTestFileManager.getSampleP12Truststore();
        List<CertificateWithAlias> jksTruststoreCertificates = getSampleJksTruststoreFile().getCertificates();

        //when //then
        assertThatExceptionOfType(AliasConflictException.class)
            .isThrownBy(() -> p12Truststore.addCertificate(jksTruststoreCertificates));
    }


    @Test
    void throwExceptionWhenFileNotContainsTruststoreEntry() {
        //given
        P12Truststore p12Truststore = CertificatesTestFileManager.getSampleP12Keystore();

        //when//then
        assertThatExceptionOfType(LoadTruststoreException.class)
            .isThrownBy(p12Truststore::getCertificates);
    }

}
