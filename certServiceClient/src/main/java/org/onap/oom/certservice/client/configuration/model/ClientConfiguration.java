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

import org.onap.oom.certservice.client.configuration.ClientConfigurationEnvs;

public class ClientConfiguration implements ConfigurationModel {

    private static final Integer DEFAULT_TIMEOUT_MS = 30000;
    private static final String DEFAULT_REQUEST_URL = "https://oom-cert-service:8443/v1/certificate/";
    private static final String DEFAULT_OUTPUT_TYPE = "P12";

    private String urlToCertService;
    private Integer requestTimeoutInMs;
    private String certsOutputPath;
    private String caName;
    private String outputType;


    public ClientConfiguration() {
        urlToCertService = DEFAULT_REQUEST_URL;
        requestTimeoutInMs = DEFAULT_TIMEOUT_MS;
        outputType = DEFAULT_OUTPUT_TYPE;
    }


    public String getUrlToCertService() {
        return urlToCertService;
    }

    public ClientConfiguration setUrlToCertService(String urlToCertService) {
        this.urlToCertService = urlToCertService;
        return this;
    }

    public Integer getRequestTimeoutInMs() {
        return requestTimeoutInMs;
    }

    public ClientConfiguration setRequestTimeoutInMs(Integer requestTimeoutInMs) {
        this.requestTimeoutInMs = requestTimeoutInMs;
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

    public String getOutputType() {
        return outputType;
    }

    public ClientConfiguration setOutputType(String outputType) {
        this.outputType = outputType;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s: %s, %s: %s, %s: %s, %s: %s, %s: %s",
                ClientConfigurationEnvs.REQUEST_URL, urlToCertService,
                ClientConfigurationEnvs.REQUEST_TIMEOUT, requestTimeoutInMs,
                ClientConfigurationEnvs.OUTPUT_PATH, certsOutputPath,
                ClientConfigurationEnvs.CA_NAME, caName,
                ClientConfigurationEnvs.OUTPUT_TYPE, outputType);
    }
}
