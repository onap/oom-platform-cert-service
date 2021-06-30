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

import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.onap.oom.certservice.certification.CertificateDataComparator.CertificateDataComparable;

public class X509CertificateModel implements CertificateDataComparable {

    private final X509Certificate certificate;
    private final X500Name subjectData;
    private final GeneralName[] sans;

    public X509CertificateModel(X509Certificate certificate, X500Name subjectData,
        GeneralName[] sans) {
        this.certificate = certificate;
        this.subjectData = subjectData;
        this.sans = sans;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public X500Name getSubjectData() {
        return subjectData;
    }

    public GeneralName[] getSans() {
        return sans;
    }
}
