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

package org.onap.oom.certservice.certification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.Serializable;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.exception.StringToCertificateConversionException;

class PemStringToCertificateConverterTest {

    private static final String CERTIFICATE_PEM_STRING =
        "-----BEGIN CERTIFICATE-----\n"
            + "MIIEizCCAvOgAwIBAgIUGEp2GZ6Y8nzDA9CKl5nURI7CUN8wDQYJKoZIhvcNAQEL\n"
            + "BQAwYTEjMCEGCgmSJomT8ixkAQEME2MtMGpiZnE4cWExZm8wd2ttbnkxFTATBgNV\n"
            + "BAMMDE1hbmFnZW1lbnRDQTEjMCEGA1UECgwaRUpCQ0EgQ29udGFpbmVyIFF1aWNr\n"
            + "c3RhcnQwHhcNMjEwNjI5MDY1MDI1WhcNMjMwNjI5MDY1MDI0WjB3MREwDwYDVQQD\n"
            + "DAhvbmFwLm9yZzEZMBcGA1UECwwQTGludXgtRm91bmRhdGlvbjENMAsGA1UECgwE\n"
            + "T05BUDEWMBQGA1UEBwwNU2FuLUZyYW5jaXNjbzETMBEGA1UECAwKQ2FsaWZvcm5p\n"
            + "YTELMAkGA1UEBhMCVVMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDB\n"
            + "zvbyrrEiaoBj8kma2QmC+VLmmtWFWyAJgSrYA4kxuyrjQCW4JyFGvmzbYoUFFLOF\n"
            + "hfq18VjTs0cbTysX8cFSfkV2EKGERBaZnZRQso6SIJNGa3xMe5FHjREy34Np04IH\n"
            + "jSQ42eIBgcNiIada4jgEnIQQYQJSPQtHkfOMC6O+3RpT/eHtvo5urR16MFY1K6so\n"
            + "nWYitI5TpEKRozav6zqU/otHgn5jRLryj0IZy0ijlBfSKTxrFfZr3KoMDudVEfUM\n"
            + "tsqER3poc1gFkqmCRK38BPNiY7v5KATRB+fWN+P75mw43drx9rImp+JtpOUsDK8r\n"
            + "IBd8o4afNvr/WrygBB8rAgMBAAGjgaQwgaEwDAYDVR0TAQH/BAIwADAfBgNVHSME\n"
            + "GDAWgBR9M1qUnQM0CpHUz34F5sVUUcIDtjAYBgNVHREEETAPgg10ZXN0Lm9uYXAu\n"
            + "b3JnMCcGA1UdJQQgMB4GCCsGAQUFBwMCBggrBgEFBQcDBAYIKwYBBQUHAwEwHQYD\n"
            + "VR0OBBYEFAfqcU6xGi8jzjlRuKQBtqRcXi+uMA4GA1UdDwEB/wQEAwIF4DANBgkq\n"
            + "hkiG9w0BAQsFAAOCAYEAAdw77q7shKtS8lDGcJ/Y8dEjjNSlRtU14EMC59kmKeft\n"
            + "Ri7d0oCQXtdRCut3ymizLVqQkmX6SrGshpWUsNzTdITjQ6JB2KOaiIWIF60NSleW\n"
            + "0vLm36EmY1ErK+zKe7tvGWZhTNVzBXtnq+AMfJc41u2uelkx0LNczsX9aCajLH1v\n"
            + "4z4XsUnm9qiXpnEm632emuJyj6Nt0JWVuNTJTPRnqVZf6KKR83v8JuV0EefWd5WV\n"
            + "cFspL0H3MKJV7uf7hfllnIcRtzXa5pctBCboFShVkRNaAMPpJf0DBLQxm7dAWj5A\n"
            + "hG1rwmTmzM6NpwGHW/I1SFMmtQiF0PACz1Un6nDW/Rf1iHEoGf8YBLP332LJSDug\n"
            + "RKn0cM3QTcyUEzCZxSwKJ2ngC9eG9C2d3YhB6Zxtl+gUIa3AwwPbqr7YR9QkD2Eo\n"
            + "d4LqEH9znyBfi7k2YCwP6rtfSi6ClybXe8eBcuMES4TAQfFK6FVf58tGQIx06I0O\n"
            + "34nemZwkLoOBzZkepaQv\n"
            + "-----END CERTIFICATE-----\n";

    private static final List<? extends Serializable> EXPECTED_SUBJECT_ALTERNATIVE_NAME = List
        .of(GeneralName.dNSName, "test.onap.org");

    private static final String EXPECTED_SUBJECT = "CN=onap.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";

    private final PemStringToCertificateConverter converter = new PemStringToCertificateConverter();

    @Test
    void shouldConvertStringToCertificate() throws CertificateParsingException, StringToCertificateConversionException {
        //given, when
        X509Certificate certificate = converter.convert(CERTIFICATE_PEM_STRING);
        //then
        assertThat(certificate).isNotNull();
        assertThat(certificate.getSubjectDN())
            .hasToString(EXPECTED_SUBJECT);
        assertThat(certificate.getSubjectAlternativeNames())
            .containsExactly(EXPECTED_SUBJECT_ALTERNATIVE_NAME);
    }

    @Test
    void shouldThrowExceptionWhenCertificateStringInvalid() {
        assertThatThrownBy(() -> converter.convert(""))
            .isInstanceOf(StringToCertificateConversionException.class);
    }

}
