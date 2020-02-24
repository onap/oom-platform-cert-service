/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.aaf.certservice.cmpv2client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Cmpv2HttpClient {

  private static final Logger LOG = LoggerFactory.getLogger(Cmpv2HttpClient.class);

  private static final String CONTENT_TYPE = "Content-type";
  private static final String CMP_REQUEST_MIMETYPE = "application/pkixcmp";
  private final CloseableHttpClient httpClient;

  /**
   * constructor for Cmpv2HttpClient
   *
   * @param httpClient CloseableHttpClient used for sending/recieve request.
   */
  public Cmpv2HttpClient(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Send Post Request to Server
   *
   * @param pkiMessage PKIMessage to send to server
   * @param urlString url for the server we're sending request
   * @param caName name of CA server
   * @return
   * @throws CmpClientException thrown if problems with connecting or parsing response to server
   */
  public byte[] postRequest(
      final PKIMessage pkiMessage, final String urlString, final String caName)
      throws CmpClientException {
    try (final ByteArrayOutputStream byteArrOutputStream = new ByteArrayOutputStream()) {
      final HttpPost postRequest = new HttpPost(urlString);
      final byte[] requestBytes = pkiMessage.getEncoded();

      postRequest.setEntity(new ByteArrayEntity(requestBytes));
      postRequest.setHeader(CONTENT_TYPE, CMP_REQUEST_MIMETYPE);

      try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
        response.getEntity().writeTo(byteArrOutputStream);
      }
      return byteArrOutputStream.toByteArray();
    } catch (IOException ioe) {
      CmpClientException cmpClientException =
          new CmpClientException(String.format("IOException error while trying to connect CA %s",caName), ioe);
      LOG.error("IOException error {}, while trying to connect CA {}", ioe.getMessage(), caName);
      throw cmpClientException;
    }
  }
}
