/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.oom.certservice.cmpv2client.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;

public class CmpMessageHelperTest {

    private final KeyUsage keyUsage = new KeyUsage(
        KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.nonRepudiation);
    private final ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(
        new KeyPurposeId[]{KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth});

    @Test
    void shouldSetSansInExtensions() throws CmpClientException {
        //when
        Extensions extensions = CmpMessageHelper.generateExtension(getTestSans());
        //then
        GeneralName[] sans = GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName).getNames();
        assertArrayEquals(sans, getTestSans());
    }

    @Test
    void shouldSetKeyUsagesInExtensions() throws CmpClientException {
        //when
        Extensions extensions = CmpMessageHelper.generateExtension(getTestSans());
        //then
        KeyUsage actualKeyUsage = KeyUsage.fromExtensions(extensions);
        ExtendedKeyUsage actualExtendedKeyUsage = ExtendedKeyUsage.fromExtensions(extensions);
        assertEquals(this.keyUsage, actualKeyUsage);
        assertEquals(this.extendedKeyUsage, actualExtendedKeyUsage);
    }

    private GeneralName[] getTestSans() {
        return new GeneralName[]{
            new GeneralName(GeneralName.dNSName, "tetHostName"),
            new GeneralName(GeneralName.iPAddress, "1.2.3.4")
        };
    }

}
