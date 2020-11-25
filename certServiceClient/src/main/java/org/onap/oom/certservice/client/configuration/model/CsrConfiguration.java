/*
 * ============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.configuration.model;


import java.util.List;
import org.onap.oom.certservice.client.configuration.CsrConfigurationEnvs;

public class CsrConfiguration implements ConfigurationModel {

    private String commonName;
    private String organization;
    private String state;
    private String country;
    private String organizationUnit;
    private String location;
    private List<San> sans;


    public String getCommonName() {
        return commonName;
    }

    public CsrConfiguration setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public String getOrganization() {
        return organization;
    }

    public CsrConfiguration setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public String getState() {
        return state;
    }

    public CsrConfiguration setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public CsrConfiguration setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public CsrConfiguration setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public CsrConfiguration setLocation(String location) {
        this.location = location;
        return this;
    }

    public List<San> getSans() {
        return sans;
    }

    public CsrConfiguration setSans(List<San> subjectAlternativeNames) {
        this.sans = List.copyOf(subjectAlternativeNames);
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s: %s, %s: %s, %s: %s, %s: %s, %s: %s, %s: %s, %s: %s",
                CsrConfigurationEnvs.COMMON_NAME, commonName,
                CsrConfigurationEnvs.COUNTRY, country,
                CsrConfigurationEnvs.STATE, state,
                CsrConfigurationEnvs.ORGANIZATION, organization,
                CsrConfigurationEnvs.ORGANIZATION_UNIT, organizationUnit,
                CsrConfigurationEnvs.LOCATION, location,
                CsrConfigurationEnvs.SANS, sans);
    }
}
