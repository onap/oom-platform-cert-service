/*
 * ============LICENSE_START=======================================================
 * Cert Service
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

package org.onap.aaf.certservice.certification.adapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.exception.Cmpv2ClientAdapterException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.api.CmpClient;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Cmpv2ClientAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cmpv2ClientAdapter.class);

    private final CmpClient cmpClient;
    private final CSRMetaBuilder csrMetaBuilder;
    private final RSAContentSignerBuilder rsaContentSignerBuilder;
    private final X509CertificateBuilder x509CertificateBuilder;
    private final CertificateFactoryProvider certificateFactoryProvider;

    @Autowired
    public Cmpv2ClientAdapter(CmpClient cmpClient, CSRMetaBuilder csrMetaBuilder,
            RSAContentSignerBuilder rsaContentSignerBuilder, X509CertificateBuilder x509CertificateBuilder,
            CertificateFactoryProvider certificateFactoryProvider) {
        this.cmpClient = cmpClient;
        this.csrMetaBuilder = csrMetaBuilder;
        this.rsaContentSignerBuilder = rsaContentSignerBuilder;
        this.x509CertificateBuilder = x509CertificateBuilder;
        this.certificateFactoryProvider = certificateFactoryProvider;
    }

    /**
     * Uses CmpClient to call to Cmp Server and gather certificates data
     *
     * @param csrModel Certificate Signing Request from Service external  API
     * @param server   Cmp Server configuration from cmpServers.json
     * @return container for returned certificates
     * @throws CmpClientException          Exceptions which comes from Cmp Client
     * @throws Cmpv2ClientAdapterException Exceptions which comes from Adapter itself
     */
    public CertificationModel callCmpClient(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException, Cmpv2ClientAdapterException {
        List<List<X509Certificate>> certificates = cmpClient.createCertificate(server.getCaName(),
                server.getCaMode().getProfile(), csrMetaBuilder.build(csrModel, server),
                convertCSRToX509Certificate(csrModel.getCsr(), csrModel.getPrivateKey()));
        return new CertificationModel(convertFromX509CertificateListToPEMList(certificates.get(0)),
                convertFromX509CertificateListToPEMList(certificates.get(1)));
    }

    private String convertFromX509CertificateToPEM(X509Certificate certificate) {
        StringWriter sw = new StringWriter();
        try (PemWriter pw = new PemWriter(sw)) {
            PemObjectGenerator gen = new JcaMiscPEMGenerator(certificate);
            pw.writeObject(gen);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during convert of X509 certificate", e);
        }
        return sw.toString();
    }

    private X509Certificate convertCSRToX509Certificate(PKCS10CertificationRequest csr, PrivateKey privateKey)
            throws Cmpv2ClientAdapterException {
        try {
            X509v3CertificateBuilder certificateGenerator = x509CertificateBuilder.build(csr);
            ContentSigner signer = rsaContentSignerBuilder.build(csr, privateKey);
            X509CertificateHolder holder = certificateGenerator.build(signer);
            return certificateFactoryProvider
                           .generateCertificate(new ByteArrayInputStream(holder.toASN1Structure().getEncoded()));
        } catch (IOException | CertificateException | OperatorCreationException | NoSuchProviderException e) {
            throw new Cmpv2ClientAdapterException(e);
        }
    }

    private List<String> convertFromX509CertificateListToPEMList(List<X509Certificate> certificates) {
        return certificates.stream().map(this::convertFromX509CertificateToPEM).filter(cert -> !cert.isEmpty())
                       .collect(Collectors.toList());
    }

}
