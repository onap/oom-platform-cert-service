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
import java.util.Optional;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.CertException;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.external.CsrMeta;
import org.onap.aaf.certservice.cmpv2client.external.Rdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class CsrMetaBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrMetaBuilder.class);

    /**
     * Creates CSRMeta from CsrModel and Cmpv2Server
     *
     * @param csrModel Certificate Signing Request from Service external  API
     * @param server   Cmp Server configuration from cmpServers.json
     * @return AAF native model  for CSR metadata
     */
    CsrMeta build(CsrModel csrModel, Cmpv2Server server) {
        CsrMeta csrMeta = createCsrMeta(csrModel);
        addSans(csrModel, csrMeta);
        csrMeta.setKeyPair(new KeyPair(csrModel.getPublicKey(), csrModel.getPrivateKey()));
        csrMeta.setPassword(server.getAuthentication().getIak());
        csrMeta.setIssuerName(server.getIssuerDN());
        csrMeta.setCaUrl(server.getUrl());
        csrMeta.setName(csrModel.getSubjectData());
        csrMeta.setSenderKid(server.getAuthentication().getRv());
        return csrMeta;
    }

    private CsrMeta createCsrMeta(CsrModel csrModel) {
        return new CsrMeta((Arrays.stream(csrModel.getSubjectData().getRDNs()).map(this::convertFromBcRdn)
                                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList())));
    }

    private void addSans(CsrModel csrModel, CsrMeta csrMeta) {
        csrModel.getSans().forEach(csrMeta::addSan);
    }

    private Optional<Rdn> convertFromBcRdn(org.bouncycastle.asn1.x500.RDN rdn) {
        Rdn result = null;
        try {
            result = convertRdn(rdn);
        } catch (CertException e) {
            LOGGER.error("Exception occurred during convert of RDN", e);
        }
        return Optional.ofNullable(result);
    }

    private Rdn convertRdn(org.bouncycastle.asn1.x500.RDN rdn) throws CertException {
        AttributeTypeAndValue rdnData = rdn.getFirst();
        String tag = BCStyle.INSTANCE.oidToDisplayName(rdnData.getType());
        String value = IETFUtils.valueToString(rdnData.getValue());
        return new Rdn(tag, value);
    }

}
