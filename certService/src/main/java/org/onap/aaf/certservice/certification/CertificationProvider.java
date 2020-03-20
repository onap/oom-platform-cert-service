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

package org.onap.aaf.certservice.certification;

import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.api.CmpClient;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationProvider.class);

    private final CmpClient cmpClient;

    @Autowired
    public CertificationProvider(CmpClient cmpClient) {
        this.cmpClient = cmpClient;
    }

    public CertificationModel signCsr(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException {
        List<List<X509Certificate>> certificates = cmpClient.createCertificate(csrModel, server);
        return new CertificationModel(convertFromX509CertificateListToPemList(certificates.get(0)),
                convertFromX509CertificateListToPemList(certificates.get(1)));
    }

    private static List<String> convertFromX509CertificateListToPemList(List<X509Certificate> certificates) {
        return certificates.stream().map(CertificationProvider::convertFromX509CertificateToPem).filter(cert -> !cert.isEmpty())
                .collect(Collectors.toList());
    }

    private static String convertFromX509CertificateToPem(X509Certificate certificate) {
        StringWriter sw = new StringWriter();
        try (PemWriter pw = new PemWriter(sw)) {
            PemObjectGenerator gen = new JcaMiscPEMGenerator(certificate);
            pw.writeObject(gen);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during convert of X509 certificate", e);
        }
        return sw.toString();
    }

}
