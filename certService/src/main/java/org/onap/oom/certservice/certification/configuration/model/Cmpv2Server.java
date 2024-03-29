/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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

package org.onap.oom.certservice.certification.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bouncycastle.asn1.x500.X500Name;
import org.hibernate.validator.constraints.Length;
import org.onap.oom.certservice.certification.configuration.validation.constraints.Cmpv2Url;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cmpv2Server {

    private static final int MAX_CA_NAME_LENGTH = 128;

    @NotNull
    @Valid
    private Authentication authentication;
    @NotNull
    @Length(min = 1, max = MAX_CA_NAME_LENGTH)
    private String caName;
    @NotNull
    private X500Name issuerDN;
    @Cmpv2Url
    private String url;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public X500Name getIssuerDN() {
        return issuerDN;
    }

    public void setIssuerDN(X500Name issuerDN) {
        this.issuerDN = issuerDN;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Cmpv2Server{"
                + "authentication=" + authentication
                + ", caName='" + caName + '\''
                + ", issuerDN='" + issuerDN + '\''
                + ", url='" + url + '\''
                + '}';
    }
}
