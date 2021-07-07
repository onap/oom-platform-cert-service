/*
 * ============LICENSE_START=======================================================
 * Cert Service
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.oom.certservice.certification.conversion.PemObjectFactory;
import org.onap.oom.certservice.certification.conversion.StringBase64;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;

public class OldCertificateModel {

    private final CertificateData certificateData;

    private final JcaX509CertificateHolder bcCertificate;

    private static final PemObjectFactory PEM_OBJECT_FACTORY = new PemObjectFactory();

    private final PrivateKey oldPrivateKey;


    public OldCertificateModel(X509Certificate certificate, X500Name subjectData,
        GeneralName[] sans, String encodedOldPrivateKey)
        throws CertificateEncodingException, InvalidKeySpecException, KeyDecryptionException, NoSuchAlgorithmException {
        this.certificateData = new CertificateData(subjectData, sans);
        this.bcCertificate = new JcaX509CertificateHolder(certificate);
        this.oldPrivateKey = getOldPrivateKeyObject(encodedOldPrivateKey);
    }

    public JcaX509CertificateHolder getOldCertificate() {
        return bcCertificate;
    }

    public X500Name getSubjectData() {
        return certificateData.getSubject();
    }

    public GeneralName[] getSans() {
        return certificateData.getSortedSans();
    }

    public CertificateData getCertificateData() {
        return certificateData;
    }

    public PrivateKey getOldPrivateKey() {
        return oldPrivateKey;
    }

    private PrivateKey getOldPrivateKeyObject(String encodedOldPrivateKey)
        throws KeyDecryptionException, InvalidKeySpecException, NoSuchAlgorithmException {

        StringBase64 stringBase64 = new StringBase64(encodedOldPrivateKey);
        PemObject pemObject = stringBase64.asString()
            .flatMap(PEM_OBJECT_FACTORY::createPemObject)
            .orElseThrow(
                () -> new KeyDecryptionException("Incorrect Key, decryption failed")
            );
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
