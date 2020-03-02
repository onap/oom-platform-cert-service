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

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.onap.aaf.certservice.client.httpclient.exception.CertServiceApiResponseException;
import org.onap.aaf.certservice.client.httpclient.exception.HttpClientException;
import org.onap.aaf.certservice.client.httpclient.model.CertServiceResponse;
import org.onap.aaf.certservice.client.httpclient.model.ErrorCertServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClient {

    private static final String CSR_HEADER_NAME = "CSR";
    private static final String PK_HEADER_NAME = "PK";
    private static final String CHARSET_UTF_8 = "UTF-8";

    private final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    private final Gson gson = new Gson();
    private final CloseableHttpClientProvider httpClientProvider;
    private final String certServiceAddress;

    public HttpClient(CloseableHttpClientProvider httpClientProvider, String certServiceAddress) {
        this.httpClientProvider = httpClientProvider;
        this.certServiceAddress = certServiceAddress;
    }

    public CertServiceResponse retrieveCertServiceData(String caName, String csr, String encodedPk)
            throws CertServiceApiResponseException, HttpClientException {

        try (CloseableHttpClient httpClient = httpClientProvider.getClient()) {
            HttpResponse httpResponse = httpClient.execute(createHttpPayload(caName, csr, encodedPk));
            return extractCertServiceResponse(httpResponse);

        } catch (IOException e) {
            LOGGER.error(String.format("Failed on communication between client and API for URL: '%s' . Exception message: '%s'",
                    certServiceAddress + caName, e.getMessage()));
            throw new HttpClientException(e);
        }
    }

    private int getStatusCode(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }

    private CertServiceResponse extractCertServiceResponse(HttpResponse httpResponse)
            throws CertServiceApiResponseException, IOException {
        int httpResponseCode = getStatusCode(httpResponse);
        if (HttpStatus.SC_OK != httpResponseCode) {
            LOGGER.error(String.format("Error on API response. Response Code: %d", httpResponseCode));
            throw generateApiResponseException(httpResponse);
        }
        String jsonResponse = getStringResponse(httpResponse.getEntity());
        return gson.fromJson(jsonResponse, CertServiceResponse.class);
    }

    private String getStringResponse(HttpEntity httpEntity) throws IOException {
        return EntityUtils.toString(httpEntity, CHARSET_UTF_8);
    }

    private HttpGet createHttpPayload(String caName, String csr, String pk) {
        String url = certServiceAddress + caName;
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(CSR_HEADER_NAME, csr);
        httpGet.addHeader(PK_HEADER_NAME, pk);
        return httpGet;
    }


    private CertServiceApiResponseException generateApiResponseException(HttpResponse httpResponse) throws IOException {
        String stringResponse = getStringResponse(httpResponse.getEntity());
        ErrorCertServiceResponse errorCertServiceResponse =
                gson.fromJson(stringResponse, ErrorCertServiceResponse.class);

        String messageFromApi = errorCertServiceResponse.getMessage();
        String path = errorCertServiceResponse.getPath();
        int httpResponseCode = getStatusCode(httpResponse);

        return new CertServiceApiResponseException(certServiceAddress + path, httpResponseCode, messageFromApi);
    }
}
