/**
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
 * ===========================================================================
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
 * ============LICENSE_END====================================================
 *
 */
package org.onap.aaf.certservice.cmpv2client.external;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertException;

public class RDN {

    public String tag;
    public String value;
    public ASN1ObjectIdentifier aoi;

    public RDN(final String tagValue) throws CertException {
        String[] tv = Split.splitTrim('=', tagValue);
        switch (tv[0]) {
            case "cn":
            case "CN":
                aoi = BCStyle.CN;
                break;
            case "c":
            case "C":
                aoi = BCStyle.C;
                break;
            case "st":
            case "ST":
                aoi = BCStyle.ST;
                break;
            case "l":
            case "L":
                aoi = BCStyle.L;
                break;
            case "o":
            case "O":
                aoi = BCStyle.O;
                break;
            case "ou":
            case "OU":
                aoi = BCStyle.OU;
                break;
            case "dc":
            case "DC":
                aoi = BCStyle.DC;
                break;
            case "gn":
            case "GN":
                aoi = BCStyle.GIVENNAME;
                break;
            case "sn":
            case "SN":
                aoi = BCStyle.SN;
                break; // surname
            case "email":
            case "EMAIL":
            case "emailaddress":
            case "EMAILADDRESS":
                aoi = BCStyle.EmailAddress;
                break; // should be SAN extension
            case "initials":
                aoi = BCStyle.INITIALS;
                break;
            case "pseudonym":
                aoi = BCStyle.PSEUDONYM;
                break;
            case "generationQualifier":
                aoi = BCStyle.GENERATION;
                break;
            case "serialNumber":
                aoi = BCStyle.SERIALNUMBER;
                break;
            default:
                throw new CertException(
                    "Unknown ASN1ObjectIdentifier for " + tv[0] + " in " + tagValue);
        }
        tag = tv[0];
        value = tv[1];
    }

    /**
     * Parse various forms of DNs into appropriate RDNs, which have the ASN1ObjectIdentifier
     *
     * @param delim
     * @param dnString
     * @return
     * @throws CertException
     */
    public static List<RDN> parse(final char delim, final String dnString) throws CertException {
        List<RDN> lrnd = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < dnString.length(); ++i) {
            char c = dnString.charAt(i);
            if (inQuotes) {
                if ('"' == c) {
                    inQuotes = false;
                } else {
                    sb.append(dnString.charAt(i));
                }
            } else {
                if ('"' == c) {
                    inQuotes = true;
                } else if (delim == c) {
                    if (sb.length() > 0) {
                        lrnd.add(new RDN(sb.toString()));
                        sb.setLength(0);
                    }
                } else {
                    sb.append(dnString.charAt(i));
                }
            }
        }
        if (sb.indexOf("=") > 0) {
            lrnd.add(new RDN(sb.toString()));
        }
        return lrnd;
    }

    @Override
    public String toString() {
        return tag + '=' + value;
    }
}
