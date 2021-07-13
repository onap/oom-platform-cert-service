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

package org.onap.oom.certservice.certification;

import org.onap.oom.certservice.certification.configuration.Cmpv2ServerProvider;
import org.onap.oom.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.oom.certservice.certification.conversion.CsrModelFactory;
import org.onap.oom.certservice.certification.conversion.OldCertificateModelFactory;
import org.onap.oom.certservice.certification.conversion.StringBase64;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel;
import org.onap.oom.certservice.certification.model.CertificationResponseModel;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.onap.oom.certservice.certification.model.OldCertificateModel;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificationResponseModelFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificationResponseModelFactory.class);

    private final CsrModelFactory csrModelFactory;
    private final Cmpv2ServerProvider cmpv2ServerProvider;
    private final CertificationProvider certificationProvider;
    private final OldCertificateModelFactory oldCertificateModelFactory;
    private final UpdateRequestTypeDetector updateRequestTypeDetector;

    @Autowired
    CertificationResponseModelFactory(
            CsrModelFactory csrModelFactory,
            Cmpv2ServerProvider cmpv2ServerProvider,
            CertificationProvider certificationProvider,
            OldCertificateModelFactory oldCertificateModelFactory,
            UpdateRequestTypeDetector updateRequestTypeDetector) {
        this.cmpv2ServerProvider = cmpv2ServerProvider;
        this.csrModelFactory = csrModelFactory;
        this.certificationProvider = certificationProvider;
        this.oldCertificateModelFactory = oldCertificateModelFactory;
        this.updateRequestTypeDetector = updateRequestTypeDetector;
    }

    public CertificationResponseModel provideCertificationModelFromInitialRequest(String encodedCsr, String encodedPrivateKey, String caName)
            throws DecryptionException, CmpClientException {
        CsrModel csrModel = csrModelFactory.createCsrModel(
                new StringBase64(encodedCsr),
                new StringBase64(encodedPrivateKey)
        );
        LOGGER.debug("Received CSR meta data: \n{}", csrModel);

        Cmpv2Server cmpv2Server = cmpv2ServerProvider.getCmpv2Server(caName);
        LOGGER.debug("Found server for given CA name: \n{}", cmpv2Server);

        LOGGER.info("Sending sign request for certification model for CA named: {}, and certificate signing request:\n{}",
                caName, csrModel);
        return certificationProvider.executeInitializationRequest(csrModel, cmpv2Server);
    }

    public CertificationResponseModel provideCertificationModelFromUpdateRequest(CertificateUpdateModel certificateUpdateModel)
        throws DecryptionException, CmpClientException {
        LOGGER.info("CSR: {}, old cert: {}, CA: {}", certificateUpdateModel.getEncodedCsr(),
                        certificateUpdateModel.getEncodedOldCert(), certificateUpdateModel.getCaName());
        final CsrModel csrModel = csrModelFactory.createCsrModel(
            new StringBase64(certificateUpdateModel.getEncodedCsr()),
            new StringBase64(certificateUpdateModel.getEncodedPrivateKey())
        );
        final OldCertificateModel certificateModel = oldCertificateModelFactory.createCertificateModel(
            new StringBase64(certificateUpdateModel.getEncodedOldCert()), certificateUpdateModel.getEncodedOldPrivateKey());

        Cmpv2Server cmpv2Server = cmpv2ServerProvider.getCmpv2Server(certificateUpdateModel.getCaName());
        LOGGER.debug("Found server for given CA name: \n{}", cmpv2Server);
        LOGGER.info("Sending update request for certification model for CA named: {}, and certificate update request:\n{}",
            certificateUpdateModel.getCaName(), csrModel);

        if (updateRequestTypeDetector.isKur(csrModel.getCertificateData(), certificateModel.getCertificateData())) {
            LOGGER.info(
                "Certificate Signing Request and Old Certificate have the same parameters. Preparing Key Update Request");
            return certificationProvider.executeKeyUpdateRequest(csrModel, cmpv2Server, certificateModel);
        } else {
            LOGGER.info(
                "Certificate Signing Request and Old Certificate have different parameters. Preparing Certification Request");
            return certificationProvider.executeCertificationRequest(csrModel, cmpv2Server);
        }
    }
}
