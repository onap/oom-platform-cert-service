/*
 * Copyright (C) 2020 Ericsson Software Technology AB. All rights reserved.
 *
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
 * limitations under the License
 */

package org.onap.aaf.certservice.cmpv2client.impl;

import java.security.cert.X509Certificate;
import java.util.Date;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.aaf.certservice.cmpv2client.api.CmpClient;
import org.onap.aaf.certservice.cmpv2client.external.CSRMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the CmpClient Interface conforming to RFC4210 (Certificate Management Protocol
 * (CMP)) and RFC4211 (Certificate Request Message Format (CRMF)) standards.
 */
public class CmpClientImpl implements CmpClient {

  private final Logger LOG = LoggerFactory.getLogger(CmpClientImpl.class);
  private final CloseableHttpClient httpClient;

  private static final String DEFAULT_PROFILE = "RA";
  private static final String DEFAULT_CA_NAME = "Certification Authority";

  public CmpClientImpl(CloseableHttpClient httpClient){
    this.httpClient = httpClient;
  }

  @Override
  public X509Certificate createCertificate(
      String caName,
      String profile,
      CSRMeta csrMeta,
      X509Certificate cert,
      Date notBefore,
      Date notAfter)
      throws CmpClientException {
    // Validate inputs for Certificate Request
    validate(csrMeta, cert, caName, profile, httpClient, notBefore, notAfter);

    final CreateCertRequest certRequest =
        CmpMessageBuilder.of(CreateCertRequest::new)
            .with(CreateCertRequest::setIssuerDn, csrMeta.issuerx500Name())
            .with(CreateCertRequest::setSubjectDn, csrMeta.x500Name())
            .with(CreateCertRequest::setSansList, csrMeta.sans())
            .with(CreateCertRequest::setSubjectKeyPair, csrMeta.keyPair())
            .with(CreateCertRequest::setNotBefore, notBefore)
            .with(CreateCertRequest::setNotAfter, notAfter)
            .with(CreateCertRequest::setInitAuthPassword, csrMeta.password())
            .build();

    final PKIMessage pkiMessage = certRequest.generateCertReq();
    Cmpv2HttpClient cmpv2HttpClient = new Cmpv2HttpClient(httpClient);
    final byte[] respBytes =
        cmpv2HttpClient.postRequest(pkiMessage, csrMeta.caUrl(), caName);
    final PKIMessage respPkiMessage = PKIMessage.getInstance(respBytes);
    // todo: add response validation and return Certificate
    return null;
  }

  @Override
  public X509Certificate createCertificate(
      String caName,
      String profile,
      CSRMeta csrMeta,
      X509Certificate csr)
      throws CmpClientException {
    return createCertificate(caName, profile, csrMeta, csr, null, null);
  }

  /**
   * Validate inputs for Certificate Creation.
   *
   * @param csrMeta CSRMeta Object containing variables for creating a Certificate Request.
   * @param cert Certificate object needed to validate response from CA server.
   * @param incomingCaName Date specifying certificate is not valid before this date.
   * @param incomingProfile Date specifying certificate is not valid after this date.
   * @throws IllegalArgumentException if Before Date is set after the After Date.
   */
  private void validate(
      final CSRMeta csrMeta,
      final X509Certificate cert,
      final String incomingCaName,
      final String incomingProfile,
      final CloseableHttpClient httpClient,
      final Date notBefore,
      final Date notAfter)
      throws IllegalArgumentException {

    String caName;
    String caProfile;
    caName = CmpUtil.isNullOrEmpty(incomingCaName) ? incomingCaName : DEFAULT_CA_NAME;
    caProfile = CmpUtil.isNullOrEmpty(incomingProfile) ? incomingProfile : DEFAULT_PROFILE;
    LOG.info(
        "Validate before creating Certificate Request for CA :{} in Mode {} ", caName, caProfile);

    CmpUtil.notNull(csrMeta, "CSRMeta Instance");
    CmpUtil.notNull(csrMeta.x500Name(), "Subject DN");
    CmpUtil.notNull(csrMeta.issuerx500Name(), "Issuer DN");
    CmpUtil.notNull(csrMeta.password(), "IAK/RV Password");
    CmpUtil.notNull(cert, "Certificate Signing Request (CSR)");
    CmpUtil.notNull(csrMeta.caUrl(), "External CA URL");
    CmpUtil.notNull(csrMeta.keypair(), "Subject KeyPair");
    CmpUtil.notNull(httpClient, "Closeable Http Client");

    if (notBefore != null && notAfter != null && notBefore.compareTo(notAfter) > 0) {
      throw new IllegalArgumentException("Before Date is set after the After Date");
    }
  }
}
