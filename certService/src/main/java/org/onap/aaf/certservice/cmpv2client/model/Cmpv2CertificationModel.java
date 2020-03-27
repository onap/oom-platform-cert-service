/*-
 * ============LICENSE_START=======================================================
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.aaf.certservice.cmpv2client.model;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

public class Cmpv2CertificationModel {

    private final List<X509Certificate> certificateChain;
    private final List<X509Certificate> trustedCertificates;

    public Cmpv2CertificationModel(List<X509Certificate> certificateChain, List<X509Certificate> trustedCertificates) {
        this.certificateChain = certificateChain;
        this.trustedCertificates = trustedCertificates;
    }

    public List<X509Certificate> getCertificateChain() {
        return Collections.unmodifiableList(certificateChain);
    }

    public List<X509Certificate> getTrustedCertificates() {
        return Collections.unmodifiableList(trustedCertificates);
    }
}
