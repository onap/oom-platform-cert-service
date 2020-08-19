/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.truststoremerger.certification.file.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.entry.CertificateWithAliasFactory;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemManipulator {

    private static final String X_509_INSTANCE = "X.509";
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";

    private static final Logger LOGGER = LoggerFactory.getLogger(PemManipulator.class);

    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();

    public List<CertificateWithAlias> getNotEmptyCertificateList(File file)
        throws TruststoreDataOperationException, MissingTruststoreException {
        if (isFileWithoutPemCertificate(file)) {
            throw new MissingTruststoreException("PEM file does not contain any certificate");
        }
        List<Certificate> extractedCertificate = extractCertificatesFromFile(file);
        return wrapCertificates(extractedCertificate);
    }

    public String transformToStringInPemFormat(List<Certificate> certificates) throws TruststoreDataOperationException {
        StringWriter sw = new StringWriter();
        List<PemObjectGenerator> generators = transformToPemGenerators(certificates);
        try (PemWriter pemWriter = new PemWriter(sw)) {
            for (PemObjectGenerator generator : generators) {
                pemWriter.writeObject(generator);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot convert certificates to PEM format");
            throw new TruststoreDataOperationException(e);
        }
        return sw.toString();
    }


    public boolean isFileWithoutPemCertificate(File file) throws TruststoreDataOperationException {
        List<Certificate> certificateList = extractCertificatesFromFile(file);
        return certificateList.isEmpty();
    }

    private List<Certificate> extractCertificatesFromFile(File file) throws TruststoreDataOperationException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory factory = CertificateFactory.getInstance(X_509_INSTANCE, BOUNCY_CASTLE_PROVIDER);
            return new ArrayList<>(factory.generateCertificates(inputStream));
        } catch (Exception e) {
            LOGGER.error("Cannot read certificates from file: {}", file.getPath());
            throw new TruststoreDataOperationException(e);
        }
    }


    private List<PemObjectGenerator> transformToPemGenerators(List<Certificate> certificates)
        throws TruststoreDataOperationException {
        List<PemObjectGenerator> generators = new ArrayList<>();
        for (Certificate certificate : certificates) {
            PemObjectGenerator generator = createPemGenerator(certificate);
            generators.add(generator);
        }
        return generators;
    }

    private JcaMiscPEMGenerator createPemGenerator(Certificate certificate)
        throws TruststoreDataOperationException {
        try {
            return new JcaMiscPEMGenerator(certificate);
        } catch (IOException e) {
            LOGGER.error("Cannot convert Certificate Object to PemGenerator Object");
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<CertificateWithAlias> wrapCertificates(List<Certificate> rawCertificates) {
        return rawCertificates.stream()
            .map(factory::createPemCertificate)
            .collect(Collectors.toList());
    }

}
