/*
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
 * Copyright (C) 2021 Nokia. All rights reserved.
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

package org.onap.oom.certservice.cmpv2client;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.onap.oom.certservice.certification.configuration.model.Authentication;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpServerException;
import org.onap.oom.certservice.cmpv2client.impl.CmpClientImpl;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;

class Cmpv2ClientTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private CsrModel csrModel;
    private Cmpv2Server server;
    private Date notBefore;
    private Date notAfter;
    private X500Name dn;


    @Mock
    CloseableHttpClient httpClient;

    @Mock
    CloseableHttpResponse httpResponse;

    @Mock
    HttpEntity httpEntity;

    private static KeyPair keyPair;

    @BeforeEach
    void setUp()
            throws NoSuchProviderException, NoSuchAlgorithmException, IOException,
            InvalidKeySpecException {
        keyPair = loadKeyPair();
        dn = new X500NameBuilder()
                .addRDN(BCStyle.O, "TestOrganization")
                .build();
        initMocks(this);
    }

    public KeyPair loadKeyPair()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchProviderException {

        final InputStream privateInputStream = this.getClass().getResourceAsStream("/privateKey");
        final InputStream publicInputStream = this.getClass().getResourceAsStream("/publicKey");
        BufferedInputStream bis = new BufferedInputStream(privateInputStream);
        byte[] privateBytes = IOUtils.toByteArray(bis);
        bis = new BufferedInputStream(publicInputStream);
        byte[] publicBytes = IOUtils.toByteArray(bis);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    @Test
    void shouldReturnValidPkiMessageWhenCreateCertificateRequestMessageMethodCalledWithValidCsr()
            throws Exception {
        // given
        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        setCsrModelAndServerValues(
                "mypassword",
                "senderKID",
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
        Cmpv2CertificationModel cmpClientResult =
                cmpClient.createCertificate(csrModel, server, notBefore, notAfter);
        // then
        assertNotNull(cmpClientResult);
    }

    @Disabled
    @Test
    void
    shouldThrowCmpClientExceptionWhenCreateCertificateRequestMessageMethodCalledWithWrongProtectedBytesInResponse()
            throws Exception {
        // given
        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        setCsrModelAndServerValues(
                "password",
                "senderKID",
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
        // then
        Assertions.assertThrows(
                CmpClientException.class,
                () -> cmpClient.createCertificate(csrModel, server, notBefore, notAfter));
    }

    @Test
    void shouldThrowCmpClientExceptionWithPkiErrorExceptionWhenCmpClientCalledWithBadPassword()
            throws Exception {
        // given
        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        setCsrModelAndServerValues(
                "password",
                "senderKID",
                "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
                beforeDate,
                afterDate);
        when(httpClient.execute(any())).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        try (final InputStream is =
                     this.getClass().getResourceAsStream("/ReturnedFailurePKIMessageBadPassword");
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

        // then
        Assertions.assertThrows(
                CmpServerException.class,
                () -> cmpClient.createCertificate(csrModel, server, notBefore, notAfter));
    }


    @Test
    void shouldThrowExceptionWhenResponseNotContainProtectionAlgorithmField()
        throws IOException, ParseException {

        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        setCsrModelAndServerValues(
            "password",
            "senderKID",
            "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
            beforeDate,
            afterDate);

        when(httpClient.execute(any())).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        try (
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(
                preparePKIMessageWithoutProtectionAlgorithm().getEncoded()
            ))) {

            byte[] ba = IOUtils.toByteArray(bis);
            doAnswer(
                invocation -> {
                    OutputStream os = invocation.getArgument(0);
                    os.write(ba);
                    return null;
                })
                .when(httpEntity)
                .writeTo(any(OutputStream.class));
        }

        CmpClientImpl cmpClient = new CmpClientImpl(httpClient);

        assertThatExceptionOfType(CmpClientException.class)
            .isThrownBy(() -> cmpClient.createCertificate(csrModel, server, notBefore, notAfter))
            .withMessageContaining("CMP response does not contain Protection Algorithm field");

    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhencreateCertificateCalledWithInvalidCsr()
            throws ParseException {
        // given
        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        setCsrModelAndServerValues(
                "password",
                "senderKID",
                "http://127.0.0.1/ejbca/publicweb/cmp/cmp",
                beforeDate,
                afterDate);
        CmpClientImpl cmpClient = new CmpClientImpl(httpClient);
        // then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cmpClient.createCertificate(csrModel, server, notBefore, notAfter));
    }

    @Test
    void shouldThrowIoExceptionWhenCreateCertificateCalledWithNoServerAvailable()
            throws IOException, ParseException {
        // given
        Date beforeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2019/11/11 12:00:00");
        Date afterDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2020/11/11 12:00:00");
        setCsrModelAndServerValues(
                "myPassword",
                "sender",
                "http://127.0.0.1/ejbca/publicweb/cmp/cmpTest",
                beforeDate,
                afterDate);
        when(httpClient.execute(any())).thenThrow(IOException.class);
        CmpClientImpl cmpClient = spy(new CmpClientImpl(httpClient));
        // then
        Assertions.assertThrows(
                CmpClientException.class,
                () -> cmpClient.createCertificate(csrModel, server, notBefore, notAfter));
    }

    private void setCsrModelAndServerValues(String iak, String rv, String externalCaUrl, Date notBefore, Date notAfter) {
        csrModel = new CsrModel(null, dn, keyPair.getPrivate(), keyPair.getPublic(), new GeneralName[0]);

        Authentication authentication = new Authentication();
        authentication.setIak(iak);
        authentication.setRv(rv);
        server = new Cmpv2Server();
        server.setAuthentication(authentication);
        server.setUrl(externalCaUrl);
        server.setIssuerDN(dn);
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    private PKIMessage preparePKIMessageWithoutProtectionAlgorithm() {

        CertTemplateBuilder certTemplateBuilder = new CertTemplateBuilder();
        X500Name issuerDN = getTestIssuerDN();

        certTemplateBuilder.setIssuer(issuerDN);
        certTemplateBuilder.setSerialNumber(new ASN1Integer(0L));

        CertRequest certRequest = new CertRequest(4, certTemplateBuilder.build(), null);
        CertReqMsg certReqMsg = new CertReqMsg(certRequest, new ProofOfPossession(), null);
        CertReqMessages certReqMessages = new CertReqMessages(certReqMsg);

        PKIHeaderBuilder pkiHeaderBuilder = new PKIHeaderBuilder(PKIHeader.CMP_2000, new GeneralName(issuerDN), new GeneralName(issuerDN));
        pkiHeaderBuilder.setMessageTime(new ASN1GeneralizedTime(new Date()));
        pkiHeaderBuilder.setProtectionAlg(null);

        PKIBody pkiBody = new PKIBody(PKIBody.TYPE_INIT_REQ, certReqMessages);
        return new PKIMessage(pkiHeaderBuilder.build(), pkiBody, new DERBitString("test".getBytes()));
    }

    private X500Name getTestIssuerDN() {
        return new X500NameBuilder()
            .addRDN(BCStyle.O, "Test_Organization")
            .addRDN(BCStyle.UID, "Test_UID")
            .addRDN(BCStyle.CN, "Test_CA")
            .build();
    }

}
