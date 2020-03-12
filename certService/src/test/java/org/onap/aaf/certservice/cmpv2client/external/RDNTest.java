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

package org.onap.aaf.certservice.cmpv2client.external;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RDNTest {

    @Test
    public void shouldCreateCorrectRDN() throws CertException {
        //when
        RDN rdn1 = new RDN("CN=ManagmentCA");
        RDN rdn2 = new RDN("CN = ManagmentCA ");
        RDN rdn3 = new RDN("CN", "ManagmentCA");

        //then
        String expectedValue = "ManagmentCA";
        ASN1ObjectIdentifier expectedAoi = BCStyle.CN;

        assertEquals(expectedValue, rdn1.getValue());
        assertEquals(expectedValue, rdn2.getValue());
        assertEquals(expectedValue, rdn3.getValue());
        assertEquals(expectedAoi, rdn1.getAoi());
        assertEquals(expectedAoi, rdn2.getAoi());
        assertEquals(expectedAoi, rdn3.getAoi());
    }

    @Test
    public void shouldCorrectlySplitAndTrimString() {
        //given
        String value1 = " T  =  Test";
        List<String> expected1 = Arrays.asList("T", "Test");

        String value2 = "This 123 is 99 tested 12345 string";
        List<String> expected2 = Arrays.asList("This", "is 99 tested", "string");

        //when
        List<String> actual1 = RDN.parseRDN("=", value1);
        List<String> actual2 = RDN.parseRDN("[0-9]{3,}", value2);

        //then
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    public void shouldConvertAoiStringToEnum() throws CertException {
        RDN rdn = new RDN("CN", "ManagmentCA");

        assertEquals(BCStyle.CN, rdn.getAoi("CN"));
        assertEquals(BCStyle.C, rdn.getAoi("C"));
        assertEquals(BCStyle.ST, rdn.getAoi("ST"));
        assertEquals(BCStyle.L, rdn.getAoi("L"));
        assertEquals(BCStyle.O, rdn.getAoi("O"));
        assertEquals(BCStyle.OU, rdn.getAoi("OU"));
        assertEquals(BCStyle.DC, rdn.getAoi("DC"));
        assertEquals(BCStyle.GIVENNAME, rdn.getAoi("GN"));
        assertEquals(BCStyle.SN, rdn.getAoi("SN"));
        assertEquals(BCStyle.E, rdn.getAoi("E"));
        assertEquals(BCStyle.E, rdn.getAoi("EMAIL"));
        assertEquals(BCStyle.E, rdn.getAoi("EMAILADDRESS"));
        assertEquals(BCStyle.INITIALS, rdn.getAoi("INITIALS"));
        assertEquals(BCStyle.PSEUDONYM, rdn.getAoi("PSEUDONYM"));
        assertEquals(BCStyle.GENERATION, rdn.getAoi("GENERATIONQUALIFIER"));
        assertEquals(BCStyle.SERIALNUMBER, rdn.getAoi("SERIALNUMBER"));
        assertThrows(CertException.class, () -> rdn.getAoi("INVALIDTAG"));
    }
}