/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.oom.certservice.certification.model;

import java.util.Collections;
import java.util.List;

public class CertificationResponseModel {

    private final List<String> certificateChain;
    private final List<String> trustedCertificates;

    public CertificationResponseModel(List<String> certificateChain, List<String> trustedCertificates) {
        this.certificateChain = certificateChain;
        this.trustedCertificates = trustedCertificates;
    }

    public List<String> getCertificateChain() {
        return Collections.unmodifiableList(certificateChain);
    }

    public List<String> getTrustedCertificates() {
        return Collections.unmodifiableList(trustedCertificates);
    }

}
