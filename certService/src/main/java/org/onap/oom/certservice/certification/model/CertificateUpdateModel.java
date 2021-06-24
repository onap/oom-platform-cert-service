/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nokia.
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

package org.onap.oom.certservice.certification.model;

import java.util.Objects;

public final class CertificateUpdateModel {

    private final String encodedCsr;
    private final String encodedPrivateKey;
    private final String encodedOldCert;
    private final String encodedOldPrivateKey;
    private final String caName;

    private CertificateUpdateModel(String encodedCsr, String encodedPrivateKey, String encodedOldCert,
                                   String encodedOldPrivateKey, String caName) {
        this.encodedCsr = encodedCsr;
        this.encodedPrivateKey = encodedPrivateKey;
        this.encodedOldCert = encodedOldCert;
        this.encodedOldPrivateKey = encodedOldPrivateKey;
        this.caName = caName;
    }

    public String getEncodedCsr() {
        return encodedCsr;
    }

    public String getEncodedPrivateKey() {
        return encodedPrivateKey;
    }

    public String getEncodedOldCert() {
        return encodedOldCert;
    }

    public String getEncodedOldPrivateKey() {
        return encodedOldPrivateKey;
    }

    public String getCaName() {
        return caName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateUpdateModel that = (CertificateUpdateModel) o;
        return Objects.equals(encodedCsr, that.encodedCsr)
                && Objects.equals(encodedPrivateKey, that.encodedPrivateKey)
                && Objects.equals(encodedOldCert, that.encodedOldCert)
                && Objects.equals(encodedOldPrivateKey, that.encodedOldPrivateKey)
                && Objects.equals(caName, that.caName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encodedCsr, encodedPrivateKey, encodedOldCert, encodedOldPrivateKey, caName);
    }

    public static class CertificateUpdateModelBuilder {

        private String encodedCsr;
        private String encodedPrivateKey;
        private String encodedOldCert;
        private String encodedOldPrivateKey;
        private String caName;

        public CertificateUpdateModelBuilder setEncodedCsr(String encodedCsr) {
            this.encodedCsr = encodedCsr;
            return this;
        }

        public CertificateUpdateModelBuilder setEncodedPrivateKey(String encodedPrivateKey) {
            this.encodedPrivateKey = encodedPrivateKey;
            return this;
        }

        public CertificateUpdateModelBuilder setEncodedOldCert(String encodedOldCert) {
            this.encodedOldCert = encodedOldCert;
            return this;
        }

        public CertificateUpdateModelBuilder setEncodedOldPrivateKey(String encodedOldPrivateKey) {
            this.encodedOldPrivateKey = encodedOldPrivateKey;
            return this;
        }

        public CertificateUpdateModelBuilder setCaName(String caName) {
            this.caName = caName;
            return this;
        }

        public CertificateUpdateModel createCertificateUpdateData() {
            return new CertificateUpdateModel(encodedCsr, encodedPrivateKey, encodedOldCert, encodedOldPrivateKey, caName);
        }
    }
}
