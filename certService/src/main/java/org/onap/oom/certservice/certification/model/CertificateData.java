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

package org.onap.oom.certservice.certification.model;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CertificateData {

    private final X500Name subject;
    private final List<GeneralName> sortedSans;

    public CertificateData(X500Name subject, GeneralName[] sans) {
        this.subject = subject;
        this.sortedSans = sans != null ? getSortedSansList(sans) : Collections.emptyList();
    }

    public X500Name getSubject() {
        return subject;
    }

    public GeneralName[] getSortedSans() {
        return sortedSans.toArray(new GeneralName[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateData that = (CertificateData) o;
        return Objects.equals(subject, that.subject) && Objects.equals(sortedSans, that.sortedSans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, sortedSans);
    }

    private List<GeneralName> getSortedSansList(GeneralName[] sans) {
        return Arrays.stream(sans).sorted(Comparator.comparing(GeneralName::toString))
            .collect(Collectors.toUnmodifiableList());
    }

}
