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

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.springframework.stereotype.Service;

@Service
public class X509CertificateParser {

    public X500Name getSubject(X509Certificate certificate) {
        final X500Principal subjectX500Principal = certificate.getSubjectX500Principal();
        return new X500Name(subjectX500Principal.getName());
    }

    public GeneralName[] getSans(X509Certificate certificate) throws CertificateParsingException {
        final Collection<List<?>> sans = certificate.getSubjectAlternativeNames();
        if (sans == null) {
            return new GeneralName[0];
        }
        final ArrayList<GeneralName> generalNames = new ArrayList<>();
        for (List<?> san : sans) {
            GeneralName sanGn = new GeneralName((Integer) san.get(0), san.get(1).toString());
            generalNames.add(sanGn);
        }
        return generalNames.toArray(new GeneralName[0]);
    }
}
