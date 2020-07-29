/*============LICENSE_START=======================================================
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

package org.onap.oom.certservice.client.certification.conversion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.client.certification.exception.CertFileWriterException;
import org.onap.oom.certservice.client.certification.exception.PemConversionException;
import org.onap.oom.certservice.client.certification.writer.CertFileWriter;

import java.security.PrivateKey;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConvertedArtifactsCreatorTest {

    private static final int PASSWORD_LENGTH = 24;
    private static final String CERTIFICATE_ALIAS = "certificate";
    private static final String TRUSTED_CERTIFICATE_ALIAS = "trusted-certificate-";

    private static final Password SAMPLE_PASSWORD = new Password("d9D_u8LooYaXH4G48DtN#vw0");
    private static final List<String> SAMPLE_KEYSTORE_CERTIFICATE_CHAIN = List.of("a", "b");
    private static final List<String> SAMPLE_TRUSTED_CERTIFICATE_CHAIN = List.of("c", "d");
    private static final byte[] SAMPLE_KEYSTORE_BYTES = "this is a keystore test".getBytes();
    private static final byte[] SAMPLE_TRUSTSTORE_BYTES = "this is a truststore test".getBytes();
    private static final String P12_EXTENSION = "p12";

    private CertFileWriter certFileWriter;
    private RandomPasswordGenerator passwordGenerator;
    private PemConverter converter;
    private PrivateKey privateKey;
    private ConvertedArtifactsCreator artifactsCreator;


    @BeforeEach
    void setUp() {
        certFileWriter = mock(CertFileWriter.class);
        passwordGenerator = mock(RandomPasswordGenerator.class);
        converter = mock(PemConverter.class);
        privateKey = mock(PrivateKey.class);
        artifactsCreator = new ConvertedArtifactsCreator(certFileWriter, passwordGenerator, converter, P12_EXTENSION);
    }

    @Test
    void convertedArtifactCreatorShouldTryCreateFileWithGivenExtension()
            throws CertFileWriterException, PemConversionException {
        //given
        mockPasswordGeneratorAndPemConverter();
        final String keystore = "keystore";
        final String testExtension = "testExt";
        final String keystoreFileName = String.format("%s.%s", keystore, testExtension);
        artifactsCreator = new ConvertedArtifactsCreator(certFileWriter, passwordGenerator, converter, testExtension);

        //when
        artifactsCreator.create(SAMPLE_KEYSTORE_CERTIFICATE_CHAIN, SAMPLE_TRUSTED_CERTIFICATE_CHAIN, privateKey);

        //then
        verify(certFileWriter, times(1))
                .saveData(SAMPLE_KEYSTORE_BYTES, keystoreFileName);
    }

    @Test
    void convertedArtifactsCreatorShouldCallConverterAndFilesCreatorMethods()
            throws PemConversionException, CertFileWriterException {
        // given
        mockPasswordGeneratorAndPemConverter();
        final String keystoreP12 = "keystore.p12";
        final String keystorePass = "keystore.pass";

        //when
        artifactsCreator.create(SAMPLE_KEYSTORE_CERTIFICATE_CHAIN, SAMPLE_TRUSTED_CERTIFICATE_CHAIN, privateKey);

        // then
        verify(converter, times(1))
                .convertKeystore(SAMPLE_KEYSTORE_CERTIFICATE_CHAIN, SAMPLE_PASSWORD, CERTIFICATE_ALIAS, privateKey);
        verify(certFileWriter, times(1))
                .saveData(SAMPLE_KEYSTORE_BYTES, keystoreP12);
        verify(certFileWriter, times(1))
                .saveData(SAMPLE_PASSWORD.getCurrentPassword().getBytes(), keystorePass);
        verify(converter, times(1))
                .convertTruststore(SAMPLE_TRUSTED_CERTIFICATE_CHAIN, SAMPLE_PASSWORD, TRUSTED_CERTIFICATE_ALIAS);
    }

    @Test
    void convertedArtifactsCreatorShouldCallPasswordGeneratorTwice()
            throws PemConversionException, CertFileWriterException {
        // given
        mockPasswordGeneratorAndPemConverter();

        //when
        artifactsCreator.create(SAMPLE_KEYSTORE_CERTIFICATE_CHAIN, SAMPLE_TRUSTED_CERTIFICATE_CHAIN, privateKey);

        // then
        verify(passwordGenerator, times(2)).generate(PASSWORD_LENGTH);
    }

    private void mockPasswordGeneratorAndPemConverter() throws PemConversionException {
        when(passwordGenerator.generate(PASSWORD_LENGTH)).thenReturn(SAMPLE_PASSWORD);
        when(converter.convertKeystore(SAMPLE_KEYSTORE_CERTIFICATE_CHAIN, SAMPLE_PASSWORD, CERTIFICATE_ALIAS, privateKey))
                .thenReturn(SAMPLE_KEYSTORE_BYTES);
        when(converter.convertTruststore(SAMPLE_TRUSTED_CERTIFICATE_CHAIN, SAMPLE_PASSWORD, TRUSTED_CERTIFICATE_ALIAS))
                .thenReturn(SAMPLE_TRUSTSTORE_BYTES);
    }
}
