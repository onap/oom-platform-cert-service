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

import java.util.stream.Stream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.oom.certservice.certification.model.CertificateData;

class UpdateRequestTypeDetectorTest {

    private static final String SUBJECT = "CN=onap.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";
    private static final String SUBJECT_CHANGED_ORDER = "ST=California,C=US,OU=Linux-Foundation,CN=onap.org,O=ONAP,L=San-Francisco";
    private static final String OTHER_SUBJECT = "CN=onap1.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";
    private static final String DNS_NAME = "test.onap.org";
    private static final String OTHER_DNS_NAME = "test1.onap.org";

    UpdateRequestTypeDetector updateRequestTypeDetector = new UpdateRequestTypeDetector();

    private static Stream<Arguments> equalSubjectParameters() {
        return Stream.of(
            Arguments.of(SUBJECT, SUBJECT),
            Arguments.of(SUBJECT_CHANGED_ORDER, SUBJECT_CHANGED_ORDER),
            Arguments.of(SUBJECT, SUBJECT_CHANGED_ORDER),
            Arguments.of(SUBJECT_CHANGED_ORDER, SUBJECT)
        );
    }

    private static Stream<Arguments> notEqualSubjectParameters() {
        return Stream.of(
            Arguments.of(SUBJECT, OTHER_SUBJECT),
            Arguments.of(OTHER_SUBJECT, SUBJECT),
            Arguments.of(SUBJECT_CHANGED_ORDER, OTHER_SUBJECT),
            Arguments.of(OTHER_SUBJECT, SUBJECT_CHANGED_ORDER)
        );
    }

    @ParameterizedTest
    @MethodSource("equalSubjectParameters")
    void shouldBeKurWhenSameSubjectData(String subject1, String subject2) {
        //given
        final CertificateData certificateData1 = new CertificateData(new X500Name(subject1), null);
        final CertificateData certificateData2 = new CertificateData(new X500Name(subject2), null);
        //when, then
        assertThat(updateRequestTypeDetector.isKur(certificateData1, certificateData2)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("notEqualSubjectParameters")
    void shouldNotBeKurDifferentSubjectData(String subject1, String subject2) {
        //given
        final CertificateData certificateData1 = new CertificateData(new X500Name(subject1), null);
        final CertificateData certificateData2 = new CertificateData(new X500Name(subject2), null);
        //when, then
        assertThat(updateRequestTypeDetector.isKur(certificateData1, certificateData2)).isFalse();
    }

    @Test
    void shouldBeKurWhenEqualSans() {
        //given
        final GeneralName[] sans1 = new GeneralName[]{new GeneralName(GeneralName.dNSName, DNS_NAME)};
        final GeneralName[] sans2 = new GeneralName[]{new GeneralName(GeneralName.dNSName, DNS_NAME)};
        final CertificateData certificateData1 = new CertificateData(new X500Name(SUBJECT), sans1);
        final CertificateData certificateData2 = new CertificateData(new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(updateRequestTypeDetector.isKur(certificateData1, certificateData2)).isTrue();
    }

    @Test
    void shouldNotBeKurWhenNotEqualSans() {
        //given
        final GeneralName[] sans1 = new GeneralName[]{new GeneralName(GeneralName.dNSName, DNS_NAME)};
        final GeneralName[] sans2 = new GeneralName[]{new GeneralName(GeneralName.dNSName, OTHER_DNS_NAME)};
        final CertificateData certificateData1 = new CertificateData(new X500Name(SUBJECT), sans1);
        final CertificateData certificateData2 = new CertificateData(new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(updateRequestTypeDetector.isKur(certificateData1, certificateData2)).isFalse();
    }

    @Test
    void shouldBeKurWhenEqualSansIgnoringOrder() {
        //given
        GeneralName[] sans1 = new GeneralName[]{
            new GeneralName(GeneralName.dNSName, DNS_NAME),
            new GeneralName(GeneralName.dNSName, OTHER_DNS_NAME)
        };
        GeneralName[] sans2 = new GeneralName[]{
            new GeneralName(GeneralName.dNSName, OTHER_DNS_NAME),
            new GeneralName(GeneralName.dNSName, DNS_NAME)
        };
        final CertificateData certificateData1 = new CertificateData(new X500Name(SUBJECT), sans1);
        final CertificateData certificateData2 = new CertificateData(new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(updateRequestTypeDetector.isKur(certificateData1, certificateData2)).isTrue();
    }
}
