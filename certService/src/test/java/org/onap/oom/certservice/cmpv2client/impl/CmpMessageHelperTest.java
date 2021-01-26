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
