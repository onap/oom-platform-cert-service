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

package org.onap.aaf.certservice.client.common;

public class EnvsForCsr {
    private String commonName;
    private String organization;
    private String organizationUnit;
    private String location;
    private String state;
    private String country;
    private String subjectAlternativesName;

    EnvsForCsr() {
        EnvProvider envProvider = new EnvProvider();
        this.commonName = envProvider.readEnvVariable(CsrConfigurationEnvs.COMMON_NAME.toString());
        this.organization = envProvider.readEnvVariable(CsrConfigurationEnvs.ORGANIZATION.toString());
        this.organizationUnit = envProvider.readEnvVariable(CsrConfigurationEnvs.ORGANIZATION_UNIT.toString());
        this.location = envProvider.readEnvVariable(CsrConfigurationEnvs.LOCATION.toString());
        this.state = envProvider.readEnvVariable(CsrConfigurationEnvs.STATE.toString());
        this.country = envProvider.readEnvVariable(CsrConfigurationEnvs.COUNTRY.toString());
        this.subjectAlternativesName = envProvider.readEnvVariable(CsrConfigurationEnvs.SANS.toString());
    }

    public String getCommonName() {
        return commonName;
    }

    public String getOrganization() {
        return organization;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public String getLocation() {
        return location;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }


    public String getSubjectAlternativesName() {
        return subjectAlternativesName;
    }
}
