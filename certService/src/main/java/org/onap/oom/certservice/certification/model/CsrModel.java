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

package org.onap.oom.certservice.certification.model;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;
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


public class CsrModel {

    private final PKCS10CertificationRequest csr;
    private final X500Name subjectData;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final List<GeneralName> sans;

    public CsrModel(PKCS10CertificationRequest csr, X500Name subjectData, PrivateKey privateKey, PublicKey publicKey,
        List<GeneralName> sans) {
        this.csr = csr;
        this.subjectData = subjectData;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.sans = sans;
    }

    public PKCS10CertificationRequest getCsr() {
        return csr;
    }

    public X500Name getSubjectData() {
        return subjectData;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public List<GeneralName> getSans() {
        return sans;
    }

    @Override
    public String toString() {
        return "Subject: { " + subjectData + " ,SANs: [" + getSansInReadableFormat() + "]}";
    }

    public static class CsrModelBuilder {

        private final PKCS10CertificationRequest csr;
        private final PemObject privateKey;

        public CsrModel build() throws DecryptionException {

            X500Name subjectData = getSubjectData();
            PrivateKey javaPrivateKey = convertingPemPrivateKeyToJavaSecurityPrivateKey(getPrivateKey());
            PublicKey javaPublicKey = convertingPemPublicKeyToJavaSecurityPublicKey(getPublicKey());
            List<GeneralName> sans = getSansData();

            return new CsrModel(csr, subjectData, javaPrivateKey, javaPublicKey, sans);
        }

        public CsrModelBuilder(PKCS10CertificationRequest csr, PemObject privateKey) {
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

        private PemObject getPrivateKey() {
            return privateKey;
        }

        private X500Name getSubjectData() {
            return csr.getSubject();
        }

        private List<GeneralName> getSansData() {
            if (!isAttrsEmpty() && !isAttrsValuesEmpty()) {
                Extensions extensions = Extensions.getInstance(csr.getAttributes()[0].getAttrValues().getObjectAt(0));
                GeneralName[] arrayOfAlternativeNames =
                    GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName).getNames();
                return Arrays.asList(arrayOfAlternativeNames);
            }
            return Collections.emptyList();
        }

        private boolean isAttrsValuesEmpty() {
            return csr.getAttributes()[0].getAttrValues().size() == 0;
        }

        private boolean isAttrsEmpty() {
            return csr.getAttributes().length == 0;
        }

        private PrivateKey convertingPemPrivateKeyToJavaSecurityPrivateKey(PemObject privateKey)
            throws KeyDecryptionException {
            try {
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getContent());
                return factory.generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new KeyDecryptionException("Converting Private Key failed", e.getCause());
            }
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

    private String getSansInReadableFormat() {
        return this.sans.stream()
            .map(generalName -> generalName.getName().toString())
            .collect(Collectors.joining(", "));
    }
}
