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
import org.onap.oom.certservice.certification.CertificateDataComparator.CertificateDataComparable;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.certification.model.X509CertificateModel;

class CertificateDataComparatorTest {

    private static final String SUBJECT = "CN=onap.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";
    private static final String SUBJECT_CHANGED_ORDER = "ST=California,C=US,OU=Linux-Foundation,CN=onap.org,O=ONAP,L=San-Francisco";
    private static final String OTHER_SUBJECT = "CN=onap1.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";
    CertificateDataComparator comparator = new CertificateDataComparator();

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
    void shouldAcceptSameSubjectData(String subject1, String subject2) {
        //given
        CertificateDataComparable comparable1 = new CsrModel(null, new X500Name(subject1), null, null, null);
        CertificateDataComparable comparable2 = new X509CertificateModel(null, new X500Name(subject2), null);
        //when, then
        assertThat(comparator.compare(comparable1, comparable2)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("notEqualSubjectParameters")
    void shouldRejectDifferentSubjectData(String subject1, String subject2) {
        //given
        CertificateDataComparable comparable1 = new CsrModel(null, new X500Name(subject1), null, null, null);
        CertificateDataComparable comparable2 = new X509CertificateModel(null, new X500Name(subject2), null);
        //when, then
        assertThat(comparator.compare(comparable1, comparable2)).isFalse();
    }

    @Test
    void shouldAcceptEqualSans() {
        //given
        GeneralName[] sans1 = new GeneralName[]{new GeneralName(GeneralName.dNSName, "test.onap.org")};
        GeneralName[] sans2 = new GeneralName[]{new GeneralName(GeneralName.dNSName, "test.onap.org")};
        CertificateDataComparable comparable1 = new CsrModel(null, new X500Name(SUBJECT), null, null, sans1);
        CertificateDataComparable comparable2 = new X509CertificateModel(null, new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(comparator.compare(comparable1, comparable2)).isTrue();
    }

    @Test
    void shouldRejectNotEqualSans() {
        //given
        GeneralName[] sans1 = new GeneralName[]{new GeneralName(GeneralName.dNSName, "test.onap.org")};
        GeneralName[] sans2 = new GeneralName[]{new GeneralName(GeneralName.dNSName, "test1.onap.org")};
        CertificateDataComparable comparable1 = new CsrModel(null, new X500Name(SUBJECT), null, null, sans1);
        CertificateDataComparable comparable2 = new X509CertificateModel(null, new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(comparator.compare(comparable1, comparable2)).isFalse();
    }

    @Test
    void shouldAcceptEqualSansIgnoringOrder() {
        //given
        GeneralName[] sans1 = new GeneralName[]{
            new GeneralName(GeneralName.dNSName, "test.onap.org"),
            new GeneralName(GeneralName.dNSName, "test1.onap.org")
        };
        GeneralName[] sans2 = new GeneralName[]{
            new GeneralName(GeneralName.dNSName, "test1.onap.org"),
            new GeneralName(GeneralName.dNSName, "test.onap.org")
        };
        CertificateDataComparable comparable1 = new CsrModel(null, new X500Name(SUBJECT), null, null, sans1);
        CertificateDataComparable comparable2 = new X509CertificateModel(null, new X500Name(SUBJECT), sans2);
        //when, then
        assertThat(comparator.compare(comparable1, comparable2)).isTrue();
    }
}
