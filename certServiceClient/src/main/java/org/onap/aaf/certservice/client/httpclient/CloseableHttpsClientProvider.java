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

package org.onap.aaf.certservice.client.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;

public class CloseableHttpsClientProvider {

    private final int timeout;
    private final SSLContext sslContext;

    public CloseableHttpsClientProvider(SSLContext sslContext, int timeout) {
        this.sslContext = sslContext;
        this.timeout = timeout;
    }

    public CloseableHttpClient getClient() {
        RequestConfig config =
                RequestConfig.custom()
                        .setConnectionRequestTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

        return HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .setDefaultRequestConfig(config).build();
    }
}
