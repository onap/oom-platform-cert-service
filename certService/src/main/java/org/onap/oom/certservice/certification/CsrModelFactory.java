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

package org.onap.oom.certservice.certification;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.onap.oom.certservice.certification.exception.CsrDecryptionException;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;
import org.onap.oom.certservice.certification.model.CsrModel;
import org.springframework.stereotype.Service;


@Service
public class CsrModelFactory {

    private final PemObjectFactory pemObjectFactory
            = new PemObjectFactory();
    private final Pkcs10CertificationRequestFactory certificationRequestFactory
            = new Pkcs10CertificationRequestFactory();


    public CsrModel createCsrModel(StringBase64 csr, StringBase64 privateKey)
            throws DecryptionException {
        PKCS10CertificationRequest decodedCsr = decodeCsr(csr);
        PemObject decodedPrivateKey = decodePrivateKey(privateKey);
        return new CsrModel.CsrModelBuilder(decodedCsr, decodedPrivateKey).build();
    }

    private PemObject decodePrivateKey(StringBase64 privateKey)
            throws KeyDecryptionException {

        return privateKey.asString()
                .flatMap(pemObjectFactory::createPemObject)
                .orElseThrow(
                        () -> new KeyDecryptionException("Incorrect Key, decryption failed")
                );
    }

    private PKCS10CertificationRequest decodeCsr(StringBase64 csr)
            throws CsrDecryptionException {
        return csr.asString()
                .flatMap(pemObjectFactory::createPemObject)
                .flatMap(certificationRequestFactory::createPkcs10CertificationRequest)
                .orElseThrow(
                        () -> new CsrDecryptionException("Incorrect CSR, decryption failed")
                );
    }

}


