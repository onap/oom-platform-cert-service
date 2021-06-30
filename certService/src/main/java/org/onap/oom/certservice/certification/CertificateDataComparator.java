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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.springframework.stereotype.Service;

@Service
public class CertificateDataComparator {

    public boolean compare(CertificateDataComparable comparable1, CertificateDataComparable comparable2) {
        return isSubjectDataEqual(comparable1, comparable2) && areSansEqualIgnoringOrder(comparable1, comparable2);
    }

    private boolean areSansEqualIgnoringOrder(CertificateDataComparable comparable1,
        CertificateDataComparable comparable2) {
        List<GeneralName> sans1 = comparable1.getSansAsList();
        List<GeneralName> sans2 = comparable2.getSansAsList();
        return sans1.containsAll(sans2) && sans2.containsAll(sans1);
    }

    private boolean isSubjectDataEqual(CertificateDataComparable comparable1, CertificateDataComparable comparable2) {
        return Objects.equals(comparable1.getSubjectData(), comparable2.getSubjectData());
    }

    public interface CertificateDataComparable {

        X500Name getSubjectData();

        GeneralName[] getSans();

        default List<GeneralName> getSansAsList() {
            final GeneralName[] sans = this.getSans();
            return sans != null ? Arrays.asList(sans) : Collections.emptyList();
        }
    }

}
