/*
 * ============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client.configuration.model;

public class ClientConfiguration implements ConfigurationModel {

    Integer DEFAULT_TIMEOUT_MS = 30000;
    String DEFAULT_REQUEST_URL = "http://cert-service:8080/v1/certificate/";

    private String urlToCertService;
    private Integer requestTimeout;
    private String certsOutputPath;
    private String caName;


    public ClientConfiguration() {
        urlToCertService = DEFAULT_REQUEST_URL;
        requestTimeout = DEFAULT_TIMEOUT_MS;
    }


    public String getUrlToCertService() {
        return urlToCertService;
    }

    public ClientConfiguration setUrlToCertService(String urlToCertService) {
        this.urlToCertService = urlToCertService;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public ClientConfiguration setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public String getCertsOutputPath() {
        return certsOutputPath;
    }

    public ClientConfiguration setCertsOutputPath(String certsOutputPath) {
        this.certsOutputPath = certsOutputPath;
        return this;
    }

    public String getCaName() {
        return caName;
    }

    public ClientConfiguration setCaName(String caName) {
        this.caName = caName;
        return this;
    }
}
