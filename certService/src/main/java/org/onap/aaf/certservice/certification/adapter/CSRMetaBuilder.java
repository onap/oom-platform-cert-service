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

import java.security.KeyPair;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.CertException;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.external.CSRMeta;
import org.onap.aaf.certservice.cmpv2client.external.RDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class CSRMetaBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSRMetaBuilder.class);

    /**
     * Creates CSRMeta from CsrModel and Cmpv2Server
     *
     * @param csrModel Certificate Signing Request from Service external  API
     * @param server   Cmp Server configuration from cmpServers.json
     * @return AAF native model  for CSR metadata
     */
    CSRMeta build(CsrModel csrModel, Cmpv2Server server) {
        CSRMeta csrMeta = createCsrMeta(csrModel);
        addSans(csrModel, csrMeta);
        csrMeta.keyPair(new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey()));
        csrMeta.password(server.getAuthentication().getIak());
        csrMeta.setIssuerName(server.getIssuerDN());
        csrMeta.caUrl(server.getUrl());
        csrMeta.setName(csrModel.getSubjectData());
        csrMeta.senderKid(server.getAuthentication().getRv());
        return csrMeta;
    }

    private CSRMeta createCsrMeta(CsrModel csrModel) {
        return new CSRMeta((Arrays.stream(csrModel.getSubjectData().getRDNs()).map(this::convertFromBcRDN)
                                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList())));
    }

    private void addSans(CsrModel csrModel, CSRMeta csrMeta) {
        csrModel.getSans().forEach(csrMeta::san);
    }

    private String convertRDNToString(org.bouncycastle.asn1.x500.RDN rdn) {
        return BCStyle.INSTANCE.oidToDisplayName(rdn.getFirst().getType()) + "=" + IETFUtils.valueToString(
                rdn.getFirst().getValue());
    }

    private Optional<RDN> convertFromBcRDN(org.bouncycastle.asn1.x500.RDN rdn) {
        RDN result = null;
        try {
            result = new RDN(convertRDNToString(rdn));
        } catch (CertException e) {
            LOGGER.error("Exception occurred during convert of RDN", e);
        }
        return Optional.ofNullable(result);
    }

}
