/*============LICENSE_START=======================================================
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

package org.onap.aaf.certservice.client.certification.conversion;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants;
import org.onap.aaf.certservice.client.certification.exception.PemToPKCS12ConverterException;

class PemToPKCS12ConverterTest {

    private static final String RESOURCES_PATH = "src/test/resources";
    private static final String CERT1_PATH = RESOURCES_PATH + "/cert1.pem";
    private static final String CERT2_PATH = RESOURCES_PATH + "/cert2.pem";
    private static final String KEY_PATH = RESOURCES_PATH + "/privateKey";
    private static final String EXPECTED_KEYSTORE_PATH = RESOURCES_PATH + "/expectedKeystore.jks";
    private static final String EXPECTED_TRUSTSTORE_PATH = RESOURCES_PATH + "/expectedTruststore.jks";
    private static final String PKCS12 = "PKCS12";
    private static final String PKCS8 = "PKCS#8";
    private static final String KEY_ERROR_MSG = "java.security.KeyStoreException: Key protection  algorithm not found: java.lang.NullPointerException";
    private static final String CERTIFICATES_ERROR_MSG = "The certificate couldn't be parsed correctly. certificate1";
    private static final String PASSWORD_ERROR_MSG = "Password should be min. 16 chars long and should contain only alphanumeric characters and special characters like Underscore (_), Dollar ($) and Pound (#)";
    private static byte[] key;
    private PrivateKey privateKey = mock(PrivateKey.class);

    @BeforeAll
    static void setUpForAll() throws IOException {
        key = Files.readAllBytes(Path.of(KEY_PATH));
    }

    @Test
    void convertKeystoreShouldReturnKeystoreWithGivenPrivateKeyAndCertificateChain()
        throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, PemToPKCS12ConverterException {
        // given
        final String alias = "keystore-entry";
        final Password password = new Password("d9D_u8LooYaXH4G48DtN#vw0");
        final List<String> certificateChain = getCertificates();
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();
        final KeyStore expectedKeyStore = KeyStore.getInstance(PKCS12);
        expectedKeyStore.load(new ByteArrayInputStream(Files.readAllBytes(Path.of(EXPECTED_KEYSTORE_PATH))),
            password.toCharArray());
        final Certificate[] expectedChain = expectedKeyStore.getCertificateChain(alias);
        privateKeyMockSetup();

        // when
        final byte[] result = converter.convertKeystore(certificateChain, password, alias, privateKey);

        // then
        final KeyStore actualKeyStore = KeyStore.getInstance(PKCS12);
        actualKeyStore.load(new ByteArrayInputStream(result), password.toCharArray());
        final Certificate[] actualChain = actualKeyStore.getCertificateChain(alias);

        assertArrayEquals(key, actualKeyStore.getKey(alias, password.toCharArray()).getEncoded());
        assertEquals(2, expectedChain.length);
        assertArrayEquals(expectedChain, actualChain);
    }

    @Test
    void convertKeystoreShouldThrowPemToPKCS12ConverterExceptionBecauseOfWrongPassword() throws IOException {
        // given
        final String alias = "keystore-entry";
        final Password password = new Password("apple");
        final List<String> certificateChain = getCertificates();
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();
        privateKeyMockSetup();

        // when
        Exception exception = assertThrows(PemToPKCS12ConverterException.class, () ->
            converter.convertKeystore(certificateChain, password, alias, privateKey)
        );

        // then
        assertEquals(PASSWORD_ERROR_MSG, exception.getMessage());
    }

    @Test
    void convertTruststoreShouldReturnTruststoreWithGivenCertificatesArray()
        throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, PemToPKCS12ConverterException {

        // given
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();
        final String alias = "trusted-certificate-";
        final String alias1 = alias + 1;
        final String alias2 = alias + 2;
        final Password password = new Password("9z6oFx1epRSCuBWU4Er8i_0y");
        final List<String> trustedCertificates = getCertificates();
        final KeyStore expectedTrustStore = KeyStore.getInstance(PKCS12);
        expectedTrustStore.load(new ByteArrayInputStream(Files.readAllBytes(Path.of(EXPECTED_TRUSTSTORE_PATH))),
            password.toCharArray());

        // when
        final byte[] result = converter.convertTruststore(trustedCertificates, password, alias);

        // then
        final KeyStore actualKeyStore = KeyStore.getInstance(PKCS12);
        actualKeyStore.load(new ByteArrayInputStream(result), password.toCharArray());

        assertTrue(actualKeyStore.containsAlias(alias1));
        assertTrue(actualKeyStore.containsAlias(alias2));
        assertEquals(expectedTrustStore.getCertificate(alias1), actualKeyStore.getCertificate(alias1));
        assertEquals(expectedTrustStore.getCertificate(alias2), actualKeyStore.getCertificate(alias2));
    }

    @Test
    void convertTruststoreShouldThrowPemToPKCS12ConverterExceptionBecauseOfWrongPassword() throws IOException {
        // given
        final String alias = "trusted-certificate-";
        final Password password = new Password("nokia");
        final List<String> trustedCertificates = getCertificates();
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();

        // when then
        assertThatThrownBy(() ->
            converter.convertTruststore(trustedCertificates, password, alias))
            .isInstanceOf(PemToPKCS12ConverterException.class).hasMessage(PASSWORD_ERROR_MSG);
    }

    @Test
    void convertKeystoreShouldThrowPemToPKCS12ConverterExceptionBecauseOfWrongPrivateKey() throws IOException {
        // given
        final String alias = "keystore-entry";
        final Password password = new Password("d9D_u8LooYaXH4G48DtN#vw0");
        final List<String> certificateChain = getCertificates();
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();

        // when then
        assertThatThrownBy(() -> converter.convertKeystore(certificateChain, password, alias, privateKey))
            .isInstanceOf(PemToPKCS12ConverterException.class).hasMessage(KEY_ERROR_MSG);
    }

    @Test
    void convertKeystoreShouldThrowPemToPKCS12ConverterExceptionBecauseOfWrongCertificates() {
        // given
        final String alias = "keystore-entry";
        final Password password = new Password("d9D_u8LooYaXH4G48DtN#vw0");
        final List<String> certificateChain = List.of("certificate1", "certificate2");
        final PemToPKCS12Converter converter = new PemToPKCS12Converter();
        privateKeyMockSetup();

        // when then
        assertThatThrownBy(() -> converter.convertKeystore(certificateChain, password, alias, privateKey))
            .isInstanceOf(PemToPKCS12ConverterException.class).hasMessage(CERTIFICATES_ERROR_MSG);
    }

    private void privateKeyMockSetup() {
        when(privateKey.getEncoded()).thenReturn(key);
        when(privateKey.getAlgorithm()).thenReturn(EncryptionAlgorithmConstants.RSA_ENCRYPTION_ALGORITHM);
        when(privateKey.getFormat()).thenReturn(PKCS8);
    }

    private List<String> getCertificates() throws IOException {
        return List.of(
            Files.readString(
                Path.of(CERT1_PATH), StandardCharsets.UTF_8),
            Files.readString(
                Path.of(CERT2_PATH), StandardCharsets.UTF_8)
        );
    }
}