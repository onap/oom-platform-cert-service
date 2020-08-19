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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.AliasCertificateWrapper;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;

class P12TruststoreTest {

    private static final String TRUSTSTORE_P12_FILE_PATH = "src/test/resources/truststore-p12.p12";
    private static final String TRUSTSTORE_P12_PASSWORD = "88y9v5D8H3SG6bZWRVHDfOAo";
    private static final String KEYSTORE_INSTANCE_P12 = "PKCS12";
    private static final String TRUSTSTORE_P12_COPIED_FILE_PATH = "src/test/resources/truststore-new-p12.p12";
    private static final String TRUSTSTORE_P12_NEW_FILE_PASSWORD = "secret";
    private static final int EXPECTED_ONE = 1;
    private static final String TRUSTSTORE_JKS_FILE_PATH = "src/test/resources/truststore-jks.jks";
    private static final String TRUSTSTORE_JKS_PASSWORD = "EOyuFbuYDyq_EhpboM72RHua";
    private static final String KEYSTORE_INSTANCE_JKS = "JKS";
    private static final String TRUSTSTORE_PEM_FILE_PATH = "src/test/resources/truststore.pem";


    @Test
    void P12TruststoreShouldReadCertificatesFromFile() throws ExitableException {
        //given
        P12Truststore p12Truststore = getSampleTruststoreFile();

        //when
        List<CertificateWrapper> certificateWrappers = p12Truststore.getCertificates();

        //then
        assertThat(certificateWrappers).hasSize(EXPECTED_ONE);
    }



    @Test
    void P12TruststoreShouldAddDifferentCertificates() throws Exception {
        //given
        P12Truststore emptyP12Truststore = getCopiedP12TruststoreFile();
//        JksTruststore jksTruststore =
        PemTruststore pemTruststore = getSamplePemTruststore();
//        JksTruststore jksTruststore = getSampleJksTruststore();

//        Certificate certificate = mock(Certificate.class);
//        CertificateWrapper certificateWrapper = new AliasCertificateWrapper(certificate,"alias");
//        List<CertificateWrapper> certificateWrappers1 = new ArrayList<>();
//        certificateWrappers1.add(certificateWrapper);

        //when
        emptyP12Truststore.addCertificate(pemTruststore.getCertificates());
//        emptyP12Truststore.addCertificate(jksTruststore.getCertificates());
        //TODO modify files of JKS to has different alias
        //then
//        emptyP12Truststore.addCertificate(certificateWrappers1);
        List<CertificateWrapper> certificateWrappers = emptyP12Truststore.getCertificates();
        assertThat(certificateWrappers).hasSize(2);
    }

    @Test
    void P12TruststoreShouldThrowExceptionWhenCannotInitializeKeystoreInstance() {
        File file = mock(File.class);
        String samplePassword = "Sample password";
        String notSupportedKeystoreInstance = "notSupported";
        List<CertificateWrapper> emptyList = new ArrayList<>();

        P12Truststore p12Truststore = new P12Truststore(file, samplePassword, notSupportedKeystoreInstance);

        assertThatExceptionOfType(KeystoreInstanceException.class).isThrownBy(p12Truststore::getCertificates);
        assertThatExceptionOfType(KeystoreInstanceException.class)
            .isThrownBy(() -> p12Truststore.addCertificate(emptyList));

    }

    private P12Truststore getSampleTruststoreFile() {
        File truststoreFile = new File(TRUSTSTORE_P12_FILE_PATH);
        return new P12Truststore(truststoreFile, TRUSTSTORE_P12_PASSWORD, KEYSTORE_INSTANCE_P12);
    }

    private P12Truststore getCopiedP12TruststoreFile() throws IOException {
        Files.copy(Paths.get(TRUSTSTORE_P12_FILE_PATH), Paths.get(TRUSTSTORE_P12_COPIED_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        File truststoreFile = new File(TRUSTSTORE_P12_COPIED_FILE_PATH);
        return new P12Truststore(truststoreFile, TRUSTSTORE_P12_PASSWORD, KEYSTORE_INSTANCE_P12);
    }

    private PemTruststore getSamplePemTruststore() {
        File truststoreFile = new File(TRUSTSTORE_PEM_FILE_PATH);
        return new PemTruststore(truststoreFile);
    }

    private JksTruststore getSampleJksTruststore() {
        File truststoreFile = new File(TRUSTSTORE_JKS_FILE_PATH);
        return new JksTruststore(truststoreFile, TRUSTSTORE_JKS_PASSWORD, KEYSTORE_INSTANCE_JKS);
    }

}
