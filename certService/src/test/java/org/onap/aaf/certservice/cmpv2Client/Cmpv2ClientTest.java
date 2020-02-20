/*
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
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
package org.onap.aaf.certservice.cmpv2Client;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.aaf.certservice.cmpv2client.external.CSRMeta;
import org.onap.aaf.certservice.cmpv2client.external.RDN;
import org.onap.aaf.certservice.cmpv2client.impl.CmpClientImpl;

class Cmpv2ClientTest {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private CSRMeta csrMeta;
  private Date notBefore;
  private Date notAfter;

  @Mock KeyPairGenerator kpg;

  @Mock X509Certificate cert;

  @Mock CloseableHttpClient httpClient;

  @Mock CloseableHttpResponse httpResponse;

  @Mock HttpEntity httpEntity;

  private static KeyPair keyPair;
  private static ArrayList<RDN> rdns;

  @BeforeEach
  void setUp() throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyPairGenerator keyGenerator;
    keyGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
    keyGenerator.initialize(2048);
    keyPair = keyGenerator.generateKeyPair();
    rdns = new ArrayList<>();
    try {
      rdns.add(new RDN("O=CommonCompany"));
    } catch (CertException e) {
      e.printStackTrace();
    }
    initMocks(this);
  }

  @Test
  void shouldReturnValidPkiMessageWhenCreateCertificateRequestMessageMethodCalledWithValidCsr()
      throws Exception {
    // given
    Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
    Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
    setCsrMetaValuesAndDateValues(
        rdns,
        "CN=CommonName",
        "CN=ManagementCA",
        "CommonName.com",
        "CommonName@cn.com",
        "password",
        "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
        beforeDate,
        afterDate);
    when(httpClient.execute(any())).thenReturn(httpResponse);
    when(httpResponse.getEntity()).thenReturn(httpEntity);

    try (final InputStream is =
            this.getClass().getResourceAsStream("/ReturnedSuccessPKIMessageWithCertificateFile");
        BufferedInputStream bis = new BufferedInputStream(is)) {

      byte[] ba = IOUtils.toByteArray(bis);
      doAnswer(
              invocation -> {
                OutputStream os = (ByteArrayOutputStream) invocation.getArguments()[0];
                os.write(ba);
                return null;
              })
          .when(httpEntity)
          .writeTo(any(OutputStream.class));
    }
    CmpClientImpl cmpClient = spy(new CmpClientImpl(httpClient));
    // when
    Certificate certificate =
        cmpClient.createCertificate("data", "RA", csrMeta, cert, notBefore, notAfter);
    // then
    assertNull(certificate);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhencreateCertificateCalledWithInvalidCsr()
      throws ParseException {
    // given
    Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
    Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
    setCsrMetaValuesAndDateValues(
        rdns,
        "CN=CommonName",
        "CN=ManagementCA",
        "CommonName.com",
        "CommonName@cn.com",
        "password",
        "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
        beforeDate,
        afterDate);
    CmpClientImpl cmpClient = new CmpClientImpl(httpClient);
    // then
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            cmpClient.createCertificate(
                "data", "RA", csrMeta, cert, notBefore, notAfter));
  }

  @Test
  void shouldThrowIOExceptionWhenCreateCertificateCalledWithNoServerAvailable()
      throws IOException, ParseException {
    // given
    Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
    Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
    setCsrMetaValuesAndDateValues(
        rdns,
        "CN=Common",
        "CN=CommonCA",
        "Common.com",
        "Common@cn.com",
        "myPassword",
        "http://127.0.0.1/ejbca/publicweb/cmp/cmpTest",
        beforeDate,
        afterDate);
    when(httpClient.execute(any())).thenThrow(IOException.class);
    CmpClientImpl cmpClient = spy(new CmpClientImpl(httpClient));
    // then
    Assertions.assertThrows(
        CmpClientException.class,
        () ->
            cmpClient.createCertificate(
                "data", "RA", csrMeta, cert, notBefore, notAfter));
  }

  private void setCsrMetaValuesAndDateValues(
      List<RDN> rdns,
      String cn,
      String issuerCn,
      String san,
      String email,
      String password,
      String externalCaUrl,
      Date notBefore,
      Date notAfter) {
    csrMeta = new CSRMeta(rdns);
    csrMeta.cn(cn);
    csrMeta.san(san);
    csrMeta.password(password);
    csrMeta.email(email);
    csrMeta.issuerCn(issuerCn);
    when(kpg.generateKeyPair()).thenReturn(keyPair);
    csrMeta.keypair();
    csrMeta.caUrl(externalCaUrl);

    this.notBefore = notBefore;
    this.notAfter = notAfter;
  }
}
