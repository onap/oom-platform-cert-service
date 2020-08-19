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

package org.onap.oom.truststoremerger.certification.file.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.TestCertificateProvider;
import org.onap.oom.truststoremerger.certification.file.model.JavaTruststore;

class JavaTruststoreTest {
    public static final String X_509_CERTIFICATE = "X.509";
    public static final int FIRST_ELEMENT = 0;
    private static final int EXPECTED_ONE = 1;
    public static final int EXPECTED_THREE = 3;

    @Test
    void jksTruststoreShouldReadCertificatesFromFile() throws ExitableException {

        //given
        JavaTruststore jksTruststoreFile = TestCertificateProvider.getSampleJksTruststoreFile();

        //when
        List<CertificateWithAlias> certificates = jksTruststoreFile.getCertificates();
        Certificate certificate = certificates.get(FIRST_ELEMENT).getCertificate();

        //then
        assertThat(certificates).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }

    @Test
    void jksTruststoreShouldAddDifferentCertificates() throws Exception {

        //given
        JavaTruststore jksTruststore = TestCertificateProvider.createTmpJksTruststoreFileWithUniqAlias();
        List<CertificateWithAlias> p12certificates = TestCertificateProvider.getSampleP12Truststore()
            .getCertificates();
        List<CertificateWithAlias> pemCertificates = TestCertificateProvider.getSamplePemTruststoreFile()
            .getCertificates();

        //when
        jksTruststore.addCertificate(p12certificates);
        jksTruststore.addCertificate(pemCertificates);

        //then
        assertThat(jksTruststore.getCertificates()).hasSize(EXPECTED_THREE);

    }

    @Test
    void p12TruststoreShouldReadCertificatesFromFile() throws ExitableException {
        //given
        JavaTruststore p12Truststore = TestCertificateProvider.getSampleP12Truststore();

        //when
        List<CertificateWithAlias> certificatesWithAliases = p12Truststore.getCertificates();
        Certificate certificate = certificatesWithAliases.get(FIRST_ELEMENT).getCertificate();

        //then
        assertThat(certificatesWithAliases).hasSize(EXPECTED_ONE);
        assertThat(certificate.getType()).isEqualTo(X_509_CERTIFICATE);
    }


    @Test
    void p12TruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        JavaTruststore p12Truststore = TestCertificateProvider.createTmpP12TruststoreFile();
        List<CertificateWithAlias> jksTruststoreCertificates = TestCertificateProvider
            .getSampleJksTruststoreFileWithUniqueAlias()
            .getCertificates();
        List<CertificateWithAlias> pemTruststoreCertificates = TestCertificateProvider.getSamplePemTruststoreFile()
            .getCertificates();

        //when
        p12Truststore.addCertificate(jksTruststoreCertificates);
        p12Truststore.addCertificate(pemTruststoreCertificates);
        p12Truststore.saveFile();


        //then
        JavaTruststore p12TruststoreSaved = TestCertificateProvider.getTmpP12TruststoreFile();
        assertThat(p12TruststoreSaved.getCertificates()).hasSize(EXPECTED_THREE);
    }



    @AfterAll
    static void removeTemporaryFiles() throws IOException {
        TestCertificateProvider.removeTemporaryFiles();
    }
}
