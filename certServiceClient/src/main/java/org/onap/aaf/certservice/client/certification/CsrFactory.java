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

package org.onap.aaf.certservice.client.certification;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import org.onap.aaf.certservice.client.certification.exception.CsrGenerationException;
import org.onap.aaf.certservice.client.configuration.model.CsrConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.util.Optional;

import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.COMMON_NAME;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.COUNTRY;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.LOCATION;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.ORGANIZATION;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.ORGANIZATION_UNIT;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.SIGN_ALGORITHM;
import static org.onap.aaf.certservice.client.certification.EncryptionAlgorithmConstants.STATE;


public class CsrFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(CsrFactory.class);
    private static final String SANS_DELIMITER = ":";
    private final CsrConfiguration configuration;


    public CsrFactory(CsrConfiguration configuration) {
        this.configuration = configuration;
    }


    public String createCsrInPem(KeyPair keyPair) throws CsrGenerationException {
        PKCS10CertificationRequest request;
        String csrParameters = getMandatoryParameters().append(getOptionalParameters()).toString();
        X500Principal subject = new X500Principal(csrParameters);
        request = createPKCS10Csr(subject, keyPair);
        return convertPKC10CsrToPem(request);
    }


    private StringBuilder getMandatoryParameters() {
        return new StringBuilder(String.format("%s=%s, %s=%s, %s=%s, %s=%s",
                COMMON_NAME, configuration.getCommonName(),
                COUNTRY, configuration.getCountry(),
                STATE, configuration.getState(),
                ORGANIZATION, configuration.getOrganization()));
    }

    private String getOptionalParameters() {
        StringBuilder optionalParameters = new StringBuilder();
        Optional.ofNullable(configuration.getOrganizationUnit())
                .filter(CsrFactory::isParameterPresent)
                .map(unit -> optionalParameters.append(String.format(", %s=%s", ORGANIZATION_UNIT, unit)));
        Optional.ofNullable(configuration.getLocation())
                .filter(CsrFactory::isParameterPresent)
                .map(location -> optionalParameters.append(String.format(", %s=%s", LOCATION, location)));
        return optionalParameters.toString();
    }

    private PKCS10CertificationRequest createPKCS10Csr(X500Principal subject, KeyPair keyPair) throws CsrGenerationException {
        JcaPKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());

        if (isParameterPresent(configuration.getSans())) {
            builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, generateSansExtension());
        }

        return builder.build(getContentSigner(keyPair));
    }

    private ContentSigner getContentSigner(KeyPair keyPair) throws CsrGenerationException {
        ContentSigner contentSigner;
        try {
            contentSigner = new JcaContentSignerBuilder(SIGN_ALGORITHM).build(keyPair.getPrivate());
        } catch (OperatorCreationException e) {
            LOGGER.error("Creation of PKCS10Csr failed, exception message: {}", e.getMessage());
            throw new CsrGenerationException(e);

        }
        return contentSigner;
    }

    private String convertPKC10CsrToPem(PKCS10CertificationRequest request) throws CsrGenerationException {
        final StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(request);
        } catch (IOException e) {
            LOGGER.error("Conversion to PEM failed, exception message: {}", e.getMessage());
            throw new CsrGenerationException(e);
        }
        return stringWriter.toString();
    }

    private Extensions generateSansExtension() throws CsrGenerationException {
        ExtensionsGenerator generator = new ExtensionsGenerator();
        try {
            generator.addExtension(Extension.subjectAlternativeName, false, createGeneralNames());
        } catch (IOException e) {
            LOGGER.error("Generation of SANs parameter failed, exception message: {}", e.getMessage());
            throw new CsrGenerationException(e);
        }
        return generator.generate();
    }

    private GeneralNames createGeneralNames() {
        String[] sansTable = this.configuration.getSans().split(SANS_DELIMITER);
        int length = sansTable.length;
        GeneralName[] generalNames = new GeneralName[length];
        for (int i = 0; i < length; i++) {
            generalNames[i] = new GeneralName(GeneralName.dNSName, sansTable[i]);
        }
        return new GeneralNames(generalNames);
    }

    private static Boolean isParameterPresent(String parameter) {
        return parameter != null && !"".equals(parameter);
    }
}
