/*
 * ============LICENSE_START=======================================================
 * Cert Service
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

package org.onap.oom.certservice.certification.configuration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.onap.oom.certservice.cmpv2client.api.CmpClient;
import org.onap.oom.certservice.cmpv2client.impl.CmpClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class CmpClientConfig {

    @Bean
    CmpClient cmpClient(CloseableHttpClient closeableHttpClient) {
        return new CmpClientImpl(closeableHttpClient);
    }

    @Bean
    @RequestScope
    CloseableHttpClient closeableHttpClient(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    @Bean
    HttpClientBuilder httpClientBuilder() {
        return HttpClientBuilder.create();
    }

}
