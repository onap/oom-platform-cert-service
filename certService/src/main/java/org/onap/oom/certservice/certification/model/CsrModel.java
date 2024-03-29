/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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

package org.onap.oom.certservice.certification.model;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.oom.certservice.certification.exception.CsrDecryptionException;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.stream.Collectors;


public class CsrModel {

    private final PKCS10CertificationRequest csr;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final CertificateData certificateData;

    public CsrModel(PKCS10CertificationRequest csr, X500Name subjectData, PrivateKey privateKey, PublicKey publicKey,
        GeneralName[] sans) {
        this.csr = csr;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.certificateData = new CertificateData(subjectData, sans);
    }

    public PKCS10CertificationRequest getCsr() {
        return csr;
    }

    public X500Name getSubjectData() {
        return certificateData.getSubject();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public GeneralName[] getSans() {
        return certificateData.getSortedSans();
    }

    public CertificateData getCertificateData() {
        return certificateData;
    }

    @Override
    public String toString() {
        return "CSR: { Subject: { " + certificateData.getSubject() + " }, SANs: [" + getSansInReadableFormat() + "] }";
    }

    private String getSansInReadableFormat() {
        return Arrays.stream(this.certificateData.getSortedSans())
            .map(generalName -> generalName.getName().toString())
            .collect(Collectors.joining(", "));
    }

    public static class CsrModelBuilder {
        private final PKCS10CertificationRequest csr;

        private final PrivateKey privateKey;

        public CsrModel build() throws DecryptionException {

            X500Name subjectData = getSubjectData();
            PublicKey javaPublicKey = convertingPemPublicKeyToJavaSecurityPublicKey(getPublicKey());
            GeneralName[] sans = getSansData();

            return new CsrModel(csr, subjectData, privateKey, javaPublicKey, sans);
        }

        public CsrModelBuilder(PKCS10CertificationRequest csr, PrivateKey privateKey) {
            this.csr = csr;
            this.privateKey = privateKey;
        }

        private PemObject getPublicKey() throws CsrDecryptionException {
            try {
                return new PemObject("PUBLIC KEY", csr.getSubjectPublicKeyInfo().getEncoded());
            } catch (IOException e) {
                throw new CsrDecryptionException("Reading Public Key from CSR failed", e.getCause());
            }
        }

        private X500Name getSubjectData() {
            return csr.getSubject();
        }

        private GeneralName[] getSansData() {
            if (!isAttrsEmpty() && !isAttrsValuesEmpty()) {
                Extensions extensions = Extensions.getInstance(csr.getAttributes()[0].getAttrValues().getObjectAt(0));
                return GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName).getNames();
            }
            return new GeneralName[0];
        }

        private boolean isAttrsValuesEmpty() {
            return csr.getAttributes()[0].getAttrValues().size() == 0;
        }

        private boolean isAttrsEmpty() {
            return csr.getAttributes().length == 0;
        }

        private PublicKey convertingPemPublicKeyToJavaSecurityPublicKey(PemObject publicKey)
            throws KeyDecryptionException {
            try {
                KeyFactory factory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getContent());
                return factory.generatePublic(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new KeyDecryptionException("Converting Public Key from CSR failed", e.getCause());
            }
        }

    }
}
