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

package org.onap.aaf.certservice.cmpv2client.api;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;

/**
 * This class represent CmpV2Client Interface for obtaining X.509 Digital Certificates in a Public
 * Key Infrastructure (PKI), making use of Certificate Management Protocol (CMPv2) operating on
 * newest version: cmp2000(2).
 */
public interface CmpClient {

  /**
   * Requests for a External Root CA Certificate to be created for the passed public keyPair wrapped
   * in a CSRMeta with common details, accepts self-signed certificate. Basic Authentication using
   * IAK/RV, Verification of the signature (proof-of-possession) on the request is performed and an
   * Exception thrown if verification fails or issue encountered in fetching certificate from CA.
   *
   * @param caName    Information about the External Root Certificate Authority (CA) performing the
   *                  event CA Name. Could be {@code null}.
   * @param profile   Profile on CA server Client/RA Mode configuration on Server. Could be {@code
   *                  null}.
   * @param csrModel  Certificate Signing Request model. Must not be {@code null}.
   * @param server    CMPv2 Server. Must not be {@code null}.
   * @param csr       Certificate Signing Request {.cer} file. Must not be {@code null}.
   * @param notBefore An optional validity to set in the created certificate, Certificate not valid
   *                  before this date.
   * @param notAfter  An optional validity to set in the created certificate, Certificate not valid
   *                  after this date.
   * @return {@link X509Certificate} The newly created Certificate.
   * @throws CmpClientException if client error occurs.
   */
  List<List<X509Certificate>> createCertificate(
      String caName,
      String profile,
      CsrModel csrModel,
      Cmpv2Server server,
      X509Certificate csr,
      Date notBefore,
      Date notAfter)
      throws CmpClientException;

  /**
   * Requests for a External Root CA Certificate to be created for the passed public keyPair wrapped
   * in a CSRMeta with common details, accepts self-signed certificate. Basic Authentication using
   * IAK/RV, Verification of the signature (proof-of-possession) on the request is performed and an
   * Exception thrown if verification fails or issue encountered in fetching certificate from CA.
   *
   * @param caName    Information about the External Root Certificate Authority (CA) performing the
   *                  event CA Name. Could be {@code null}.
   * @param profile   Profile on CA server Client/RA Mode configuration on Server. Could be {@code
   *                  null}.
   * @param csrModel  Certificate Signing Request Model. Must not be {@code null}.
   * @param server    CMPv2 server. Must not be {@code null}.
   * @param csr       Certificate Signing Request {.cer} file. Must not be {@code null}.
   * @return {@link X509Certificate} The newly created Certificate.
   * @throws CmpClientException if client error occurs.
   */
  List<List<X509Certificate>> createCertificate(
      String caName,
      String profile,
      CsrModel csrModel,
      Cmpv2Server server,
      X509Certificate csr)
      throws CmpClientException;
}
