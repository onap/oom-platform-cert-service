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

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Component;

@Component
public class X509CertificateBuilder {

    private static final int SECURE_NEXT_BYTES = 16;
    private static final int VALID_PERIOD_IN_DAYS = 365;

    public X509v3CertificateBuilder build(PKCS10CertificationRequest csr) throws IOException {
        return new X509v3CertificateBuilder(csr.getSubject(), createSerial(),
                Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)),
                Date.from(LocalDateTime.now().plusDays(VALID_PERIOD_IN_DAYS).toInstant(ZoneOffset.UTC)),
                new PKCS10CertificationRequest(csr.getEncoded()).getSubject(),
                SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(csr.getSubjectPublicKeyInfo().getEncoded())));

    }

    private BigInteger createSerial() {
        byte[] serial = new byte[SECURE_NEXT_BYTES];
        new SecureRandom().nextBytes(serial);
        return new BigInteger(serial).abs();
    }

}
