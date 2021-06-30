/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;

class X509CertificateParserTest {

    private static final String SUBJECT = "CN=onap.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";

    X509CertificateParser parser = new X509CertificateParser();

    @Test
    void getSubject_shouldReturnCorrectSubject() {
        //given
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSubjectX500Principal())
            .thenReturn(new X500Principal(SUBJECT));
        //when
        final X500Name subject = parser.getSubject(certificate);
        //then
        assertThat(subject).isEqualTo(new X500Name(SUBJECT));
    }

    @Test
    void getSans_shouldReturnCorrectSansArray() throws CertificateParsingException {
        //given
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSubjectAlternativeNames())
            .thenReturn(List.of(
                List.of(GeneralName.dNSName, "test.onap.org"),
                List.of(GeneralName.dNSName, "test2.onap.org"),
                List.of(GeneralName.iPAddress, "127.0.0.1")
            ));
        //when
        final GeneralName[] sans = parser.getSans(certificate);
        //then
        assertThat(sans).containsExactlyInAnyOrder(
            new GeneralName(GeneralName.dNSName, "test.onap.org"),
            new GeneralName(GeneralName.dNSName, "test2.onap.org"),
            new GeneralName(GeneralName.iPAddress, "127.0.0.1")
        );
    }

    @Test
    void getSans_shouldReturnEmptyArrayWhenNoSans() throws CertificateParsingException {
        //given
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSubjectAlternativeNames()).thenReturn(null);
        //when
        final GeneralName[] sans = parser.getSans(certificate);
        //then
        assertThat(sans).isEmpty();
    }

}
