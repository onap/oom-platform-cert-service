/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.aaf.certservice.certification.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;

import org.onap.aaf.certservice.certification.exception.CsrDecryptionException;


public class CsrModel {

    private final PKCS10CertificationRequest csr;
    private final PemObject privateKey;

    public CsrModel(PKCS10CertificationRequest csr, PemObject privateKey) {
        this.csr = csr;
        this.privateKey = privateKey;
    }

    public PemObject getPublicKey() throws CsrDecryptionException {
        try {
            return new PemObject("PUBLIC KEY", csr.getSubjectPublicKeyInfo().getEncoded());
        } catch (IOException e) {
            throw new CsrDecryptionException("Reading Public Key from CSR failed", e.getCause());
        }
    }

    public PemObject getPrivateKey() {
        return privateKey;
    }

    public X500Name getSubjectData() {
        return csr.getSubject();
    }

    public List<String> getSansData() {
        Extensions extensions =
                Extensions.getInstance(csr.getAttributes()[0].getAttrValues().getObjectAt(0));
        GeneralName[] arrayOfAlternativeNames =
                GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName).getNames();

        return Arrays.stream(arrayOfAlternativeNames)
                .map(GeneralName::getName)
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Subject: { " + getSubjectData().toString()
                + " ,SANs: " + getSansData().toString() + " }";
    }

}
