/*-
 * ============LICENSE_START=======================================================
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.certservice.cmpv2client.impl;

import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.cmpv2client.exceptions.CmpClientException;
import org.onap.oom.certservice.cmpv2client.model.Cmpv2CertificationModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CmpResponseHelperTest {


    private static final String EXPECTED_ERROR_MESSAGE = "Something was wrong with the supplied certificate";

    //Sample Certificate (keystore.pem) received from client
    private static final String TEST_1LAYER_ENTITY_CERT = "-----BEGIN CERTIFICATE-----\n"
        + "MIIEtjCCAx6gAwIBAgIUeNg1jY0CV+zwcJ4CdQiDN2ihx0IwDQYJKoZIhvcNAQEL\n"
        + "BQAwUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1MRUwEwYDVQQDDAxNYW5hZ2VtZW50\n"
        + "Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5lciBRdWlja3N0YXJ0MB4XDTIyMDUx\n"
        + "ODE3MTYyOVoXDTMyMDUxNTE3MTAwOVowdzERMA8GA1UEAwwIb25hcC5vcmcxDTAL\n"
        + "BgNVBAsMBE9OQVAxGTAXBgNVBAoMEExpbnV4LUZvdW5kYXRpb24xFjAUBgNVBAcM\n"
        + "DVNhbi1GcmFuY2lzY28xEzARBgNVBAgMCkNhbGlmb3JuaWExCzAJBgNVBAYTAlVT\n"
        + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn7jV9kysrzF/LOAtiEs+\n"
        + "DpmEY/10j92TyMLy4CUYqbWhj5KWNGHJ2L8GqfWivubxTTS3svbQPLyQEXrhc1fB\n"
        + "TD1Q32q99mFaieUAnYoMIGzPZOCvsWP3A3fU1z0VsbALyJGabwA3YR9+aabcPK+D\n"
        + "be54HsvyDzU3dj85J7Mbh6w+QncRVXCN/7IMceYpUY/H00TVa3KRPMqT1IFOAsT2\n"
        + "JTcJwPkhmo6Grka7wz9QEcGKPq7MT+YFwPsvpq9/Ma8J1hVUJQEgNvOjIligPsp6\n"
        + "CZxu33A9xW51yT8Hl2zyYM/dklithNvTFXAIuu99fyWu3edn6kH0WsqHIh3L9O6P\n"
        + "gwIDAQABo4HdMIHaMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAU6ZNXe9TmP/rY\n"
        + "K69j3+AQ3CSeVWEwUQYDVR0RBEowSIENb25hcEBvbmFwLm9yZ4ILZXhhbXBsZS5v\n"
        + "cmeCDXRlc3Qub25hcC5vcmeGFW9uYXA6Ly9jbHVzdGVyLmxvY2FsL4cEfwAAATAn\n"
        + "BgNVHSUEIDAeBggrBgEFBQcDAgYIKwYBBQUHAwQGCCsGAQUFBwMBMB0GA1UdDgQW\n"
        + "BBQpQyXaSwlrBlTE3j8DEqWCHDJhKjAOBgNVHQ8BAf8EBAMCBeAwDQYJKoZIhvcN\n"
        + "AQELBQADggGBAKp65hA59bX2TpfBBbdd9p8E1k1A+b8SszlIRkE755LmJOK1rEcS\n"
        + "xuN2mOGx4/fhiycgNfuVUfVo9BMfjHct4nJ3EObK6N1tklgbNhLdwVG1BFSwDQgR\n"
        + "guxjn+UUZRp6iUYVAjo2ju5Hgn3v4xrrKIUXgwleyG18e6leKOBmfEF8vpevSXNK\n"
        + "v+OXUqJk0MFjkBG+HqFrmBY2Bwb8ZhDBc46ye5URxS1eZ8kpD5vtye3dQxI9Yi9G\n"
        + "D2AsAckq13dLXSHpqBQYFeyKzHJyjXMxjYOIUUThtVhGPNVJt4Glt1FtIXllBCkR\n"
        + "CNen6kXQjr1ocPlomx1fOj4ihVOseWxbK5WuWNFFWObA3YkwjdtmAMvb57Zm9M8S\n"
        + "67myPUbMx9ZbU9WmBXtntKREGcrYxRgcwwk8ljDT0Z8FT+YFKmtZmDxCzvSK0Znz\n"
        + "ysi80vDtXWH64OnyJ6wdugRRR6RKTuiiJh+xigN5HuveqIGu2gdzMAr5w5wh+LkW\n"
        + "oTNRWh8PGkjPFA==\n"
        + "-----END CERTIFICATE-----\n";

    //ManagementCa.pem from EJBCA
    private static final String TEST_1LAYER_CA_CERT = "-----BEGIN CERTIFICATE-----\n"
        + "MIIElzCCAv+gAwIBAgIUUrxLMcvZmK8Y9qMrOXea8CfY/NswDQYJKoZIhvcNAQEL\n"
        + "BQAwUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1MRUwEwYDVQQDDAxNYW5hZ2VtZW50\n"
        + "Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5lciBRdWlja3N0YXJ0MB4XDTIyMDUx\n"
        + "ODE3MTYyOVoXDTMyMDUxNzE3MTYyOFowUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1\n"
        + "MRUwEwYDVQQDDAxNYW5hZ2VtZW50Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5l\n"
        + "ciBRdWlja3N0YXJ0MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAs5TO\n"
        + "bBiTf3pZ+b46KIVqIQKesB3HWHWp0TvvhhVUiRTxMMJXthUEg/NiSZ51G2cuzz9B\n"
        + "eREbMkEmQYltlPrJ2OFIsFEMshePYm9O8MLDr8uWkN553l5bDfCNQcFdX/nwZcIa\n"
        + "pNlPZ0f9KTMhzax/C9vXt6fUqBTTzSuIdmlx51y42viLWqVu31zHr2fMFGZLkk0G\n"
        + "MMIHaEgY+SadySf6VfvoEkYXzrenrH9Lgk/7KXRHy5/AmqxmwMgqYNlJ+o5mwdA6\n"
        + "DAERtyWDSOUFZNgeqRELY9nBn0HxHoCESIOAxIREyZL1oeXUpSHuxzdG9HuhrAJ8\n"
        + "Kb5yjbTzn+sYweaWjARGVG2+xQS+ZIRlteOXDkOI9oseJuLOIVFYwj3bB72Za/MR\n"
        + "b8cD7q9d2G8ZFt2mUOuK0JnsU3tv4okmPmMOcwLA0U1tgVaX/WCNuoHIbXoBQy9N\n"
        + "GKIEfhMBkzrG4Q8oqTxbDRGzVRRq13kVP3aKgIrwbjwj0ztc1S4GH4K4Ata1AgMB\n"
        + "AAGjYzBhMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgwFoAU6ZNXe9TmP/rYK69j\n"
        + "3+AQ3CSeVWEwHQYDVR0OBBYEFOmTV3vU5j/62CuvY9/gENwknlVhMA4GA1UdDwEB\n"
        + "/wQEAwIBhjANBgkqhkiG9w0BAQsFAAOCAYEAAaTpoqWIpx65BVd+OllQ72k4/cv/\n"
        + "PckS/lrvQNJtxCZxz3nfO9/VakoiQOxx1f8MfLJdfi+dB8ePd1BlpBJWzF9eyTAI\n"
        + "lUyJkQAUHe0nMl477DUgPTooQwQmSbbO0ek0TBEBAhmjkfz3S6t+Dp3t2Q2sNP/H\n"
        + "136xHgqFrODvEBRsjw18Kdc2326rWVHqF7joW6o1rug3kVbjVDPBIsUS833U6aD5\n"
        + "mOCZP6nenPY1FBh8SAQmAoJ2Xr17Jj8gJpUhApU8Awc973OHBCcE4ao39XIqMzuh\n"
        + "7Yl8I0Zy6q9Gq+UeRIN/VMeADuPxNkQA7NcUtHCXkhVI5+DlBQhPetCIHnCEyEG+\n"
        + "tRGy9etWDW4adyJQL/hMKJTCyST0F2J1WOjr3+6kSH7oKcFsiQ+Xpg1MFo1LBdcg\n"
        + "XtlCUMTyb0pHYsyenj3Bop2mJQCuqXNW4WzHkNjjZBE5HYsF46LPbJoDRgK1UExX\n"
        + "YkBM+KWJQWV+eJDiZUR7Ag4mSCjEhVKh8Zw0\n"
        + "-----END CERTIFICATE-----\n";

    private static final String TEST_2LAYER_ENTITY_CERT = ""
            + "-----BEGIN CERTIFICATE-----\n"
            + "MIIDjDCCAnSgAwIBAgICEAIwDQYJKoZIhvcNAQELBQAwgYQxCzAJBgNVBAYTAlVT\n"
            + "MRMwEQYDVQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkw\n"
            + "FwYDVQQKDBBMaW51eC1Gb3VuZGF0aW9uMQ0wCwYDVQQLDARPTkFQMR4wHAYDVQQD\n"
            + "DBVpbnRlcm1lZGlhdGUub25hcC5vcmcwHhcNMjAwMjEyMDk1MTI2WhcNMjIxMTA4\n"
            + "MDk1MTI2WjB7MQswCQYDVQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQG\n"
            + "A1UEBwwNU2FuLUZyYW5jaXNjbzEZMBcGA1UECgwQTGludXgtRm91bmRhdGlvbjEN\n"
            + "MAsGA1UECwwET05BUDEVMBMGA1UEAwwMdmlkLm9uYXAub3JnMIIBIjANBgkqhkiG\n"
            + "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw+GIRzJzUOh0gtc+wzFJEdTnn+q5F10L0Yhr\n"
            + "G1xKdjPieHIFGsoiXwcuCU8arNSqlz7ocx62KQRkcA8y6edlOAsYtdOEJvqEI9vc\n"
            + "eyTB/HYsbzw3URPGch4AmibrQkKU9QvGwouHtHn4R2Ft2Y0tfEqv9hxj9v4njq4A\n"
            + "EiDLAFLl5FmVyCZu/MtKngSgu1smcaFKTYySPMxytgJZexoa/ALZyyE0gRhsvwHm\n"
            + "NLGCPt1bmE/PEGZybsCqliyTO0S56ncD55The7+D/UDS4kE1Wg0svlWon/YsE6QW\n"
            + "B3oeJDX7Kr8ebDTIAErevIAD7Sm4ee5se2zxYrsYlj0MzHZtvwIDAQABoxAwDjAM\n"
            + "BgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQCvQ1pTvjON6vSlcJRKSY4r\n"
            + "8q7L4/9ZaVXWJAjzEYJtPIqsgGiPWz0vGfgklowU6tZxp9zRZFXfMil+mPQSe+yo\n"
            + "ULrZSQ/z48YHPueE/BNO/nT4aaVBEhPLR5aVwC7uQVX8H+m1V1UGT8lk9vdI9rej\n"
            + "CI9l524sLCpdE4dFXiWK2XHEZ0Vfylk221u3IYEogVVA+UMX7BFPSsOnI2vtYK/i\n"
            + "lwZtlri8LtTusNe4oiTkYyq+RSyDhtAswg8ANgvfHolhCHoLFj6w1IkG88UCmbwN\n"
            + "d7BoGMy06y5MJxyXEZG0vR7eNeLey0TIh+rAszAFPsIQvrOHW+HuA+WLQAj1mhnm\n"
            + "-----END CERTIFICATE-----";

    private static final String TEST_2LAYER_INTERMEDIATE_CERT = ""
            + "-----BEGIN CERTIFICATE-----\n"
            + "MIIDqTCCApGgAwIBAgICEAAwDQYJKoZIhvcNAQELBQAwgZcxCzAJBgNVBAYTAlVT\n"
            + "MRMwEQYDVQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkw\n"
            + "FwYDVQQKDBBMaW51eC1Gb3VuZGF0aW9uMQ0wCwYDVQQLDARPTkFQMREwDwYDVQQD\n"
            + "DAhvbmFwLm9yZzEeMBwGCSqGSIb3DQEJARYPdGVzdGVyQG9uYXAub3JnMB4XDTIw\n"
            + "MDIxMjA5NDAxMloXDTIyMTEwODA5NDAxMlowgYQxCzAJBgNVBAYTAlVTMRMwEQYD\n"
            + "VQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkwFwYDVQQK\n"
            + "DBBMaW51eC1Gb3VuZGF0aW9uMQ0wCwYDVQQLDARPTkFQMR4wHAYDVQQDDBVpbnRl\n"
            + "cm1lZGlhdGUub25hcC5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB\n"
            + "AQC1oOYMZ6G+2DGDAizYnzdCNiogivlht1s4oqgem7fM1XFPxD2p31ATIibOdqr/\n"
            + "gv1qemO9Q4r1xn6w1Ufq7T1K7PjnMzdSeTqZefurE2JM/HHx2QvW4TjMlz2ILgaD\n"
            + "L1LN60kmMQSOi5VxKJpsrCQxbOsxhvefd212gny5AZMcjJe23kUd9OxUrtvpdLEv\n"
            + "wI3vFEvT7oRUnEUg/XNz7qeg33vf1C39yMR+6O4s6oevgsEebVKjb+yOoS6zzGtz\n"
            + "72wZjm07C54ZlO+4Uy+QAlMjRiU3mgWkKbkOy+4CvwehjhpTikdBs2DX39ZLGHhn\n"
            + "L/0a2NYtGulp9XEqmTvRoI+PAgMBAAGjEDAOMAwGA1UdEwQFMAMBAf8wDQYJKoZI\n"
            + "hvcNAQELBQADggEBADcitdJ6YswiV8jAD9GK0gf3+zqcGegt4kt+79JXlXYbb1sY\n"
            + "q3o6prcB7nSUoClgF2xUPCslFGpM0Er9FCSFElQM/ru0l/KVmJS6kSpwEHvsYIH3\n"
            + "q5anta+Pyk8JSQWAAw+qrind0uBQMnhR8Tn13tgV+Kjvg/xlH/nZIEdN5YtLB1cA\n"
            + "beVsZRyRfVL9DeZU8s/MZ5wC3kgcEp5A4m5lg7HyBxBdqhzFcDr6xiy6OGqW8Yep\n"
            + "xrwfc8Fw8a/lOv4U+tBeGNKPQDYaL9hh+oM+qMkNXsHXDqdJsuEGJtU4i3Wcwzoc\n"
            + "XGN5NWV//4bP+NFmwgcn7AYCdRvz04A8GU/0Cwg=\n"
            + "-----END CERTIFICATE-----";

    private static final String TEST_2LAYER_CA_CERT = ""
            + "-----BEGIN CERTIFICATE-----\n"
            + "MIIDtzCCAp8CFAwqQddh4/iyGfP8UZ3dpXlxfAN8MA0GCSqGSIb3DQEBCwUAMIGX\n"
            + "MQswCQYDVQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2Fu\n"
            + "LUZyYW5jaXNjbzEZMBcGA1UECgwQTGludXgtRm91bmRhdGlvbjENMAsGA1UECwwE\n"
            + "T05BUDERMA8GA1UEAwwIb25hcC5vcmcxHjAcBgkqhkiG9w0BCQEWD3Rlc3RlckBv\n"
            + "bmFwLm9yZzAeFw0yMDAyMTIwOTM0MjdaFw0yMTAyMTEwOTM0MjdaMIGXMQswCQYD\n"
            + "VQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuLUZyYW5j\n"
            + "aXNjbzEZMBcGA1UECgwQTGludXgtRm91bmRhdGlvbjENMAsGA1UECwwET05BUDER\n"
            + "MA8GA1UEAwwIb25hcC5vcmcxHjAcBgkqhkiG9w0BCQEWD3Rlc3RlckBvbmFwLm9y\n"
            + "ZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMCFrnO7/eT6V+7XkPPd\n"
            + "eiL/6xXreuegvit/1/jTVjG+3AOVcmTn2WXwXXRcQLvkWQfJVPoltsY8E3FqFRti\n"
            + "797XjY6cdQJFVDyzNU0+Fb4vJL9FK5wSvnS6EFjBEn3JvXRlENorDCs/mfjkjJoa\n"
            + "Dl74gXQEJYcg4nsTeNIj7cm3Q7VK3mZt1t7LSJJ+czxv69UJDuNJpmQ/2WOKyLZA\n"
            + "gTtBJ+Hyol45/OLsrqwq1dAn9ZRWIFPvRt/XQYH9bI/6MtqSreRVUrdYCiTe/XpP\n"
            + "B/OM6NEi2+p5QLi3Yi70CEbqP3HqUVbkzF+r7bwIb6M5/HxfqzLmGwLvD+6rYnUn\n"
            + "Bm8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAhXoO65DXth2X/zFRNsCNpLwmDy7r\n"
            + "PxT9ZAIZAzSxx3/aCYiuTrKP1JnqjkO+F2IbikrI4n6sKO49SKnRf9SWTFhd+5dX\n"
            + "vxq5y7MaqxHAY9J7+Qzq33+COVFQnaF7ddel2NbyUVb2b9ZINNsaZkkPXui6DtQ7\n"
            + "/Fb/1tmAGWd3hMp75G2thBSzs816JMKKa9WD+4VGATEs6OSll4sv2fOZEn+0mAD3\n"
            + "9q9c+WtLGIudOwcHwzPb2njtNntQSCK/tVOqbY+vzhMY3JW+p9oSrLDSdGC+pAKK\n"
            + "m/wB+2VPIYcsPMtIhHC4tgoSaiCqjXYptaOh4b8ye8CPBUCpX/AYYkN0Ow==\n"
            + "-----END CERTIFICATE-----";


    @BeforeAll
    static void setUpSecurity() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }


    @Test
    void returnListOfCertificationWhenGivenCaCertInCaPubsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        PKIMessage respPkiMessage = mockExtraCerts(null);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityCertificate(certs, TEST_1LAYER_ENTITY_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(certs, caCmpCertificate);
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CertRepMessage certRepMessage = mockCaPubs(null);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityCertificate(certs, TEST_1LAYER_ENTITY_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(certs, caCmpCertificate);
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertInExtraCertsAndExtraTrustAnchorInCaPubsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CMPCertificate extraTrustAnchor = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {extraTrustAnchor};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityCertificate(certs, TEST_1LAYER_ENTITY_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate, extraTrustAnchor
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertInExtraCertsAndExtraTrustAnchorInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate trustedCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate, trustedCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CertRepMessage certRepMessage = mockCaPubs(null);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityCertificate(certs, TEST_1LAYER_ENTITY_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate, trustedCmpCertificate
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertAndIntermediateCertInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate, intermediateCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CertRepMessage certRepMessage = mockCaPubs(null);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertAndIntermediateCertInCmpCertificatesAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        PKIMessage respPkiMessage = mockExtraCerts(null);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate, intermediateCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertInCaPubsAndIntermediateCertInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate[] extraCmpCertificates = {intermediateCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertInCaPubsAndExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);
        CMPCertificate[] cmpCertificates = {mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT)};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);
        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityCertificate(certs, TEST_1LAYER_ENTITY_CERT);
        assertThatRootCaAndTrustedCaAreInSecondList(certs, mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT));

    }

    @Test
    void returnListOfCertificationWhenGivenCaCertAndIntermediateCertInExtraCertsAndIntermediateCertInCaPubsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate[] extraCmpCertificates = {caCmpCertificate, intermediateCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);
        CMPCertificate[] cmpCertificates = {intermediateCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);
        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);
        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertAndExtraTrustAnchorInCaPubsAndIntermediateCertInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate[] extraCmpCertificates = {intermediateCmpCertificate};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate extraTrustAnchor = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate, extraTrustAnchor};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate, extraTrustAnchor
        );
    }

    @Test
    void returnListOfCertificationWhenGivenCaCertAndFirstExtraTrustAnchorInCaPubsAndIntermediateCertAndSecondExtraTrustAnchorInExtraCertsAndEntityCertInLeafCertificate()
            throws CertificateException, CmpClientException, IOException, NoSuchProviderException {
        //  given
        CMPCertificate intermediateCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_INTERMEDIATE_CERT);
        CMPCertificate extraTrustAnchor01 = mockCmpCertificateFromPem(TEST_1LAYER_ENTITY_CERT);
        CMPCertificate[] extraCmpCertificates = {intermediateCmpCertificate, extraTrustAnchor01};
        PKIMessage respPkiMessage = mockExtraCerts(extraCmpCertificates);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate extraTrustAnchor02 = mockCmpCertificateFromPem(TEST_1LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate, extraTrustAnchor02};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Cmpv2CertificationModel certs = CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                respPkiMessage, certRepMessage, leafCertificate);

        // then
        assertThatChainContainsEntityAndIntermediateCertificate(certs, TEST_2LAYER_ENTITY_CERT, TEST_2LAYER_INTERMEDIATE_CERT);

        assertThatRootCaAndTrustedCaAreInSecondList(
                certs,
                caCmpCertificate, extraTrustAnchor01, extraTrustAnchor02
        );
    }

    @Test
    void throwsExceptionWhenNoCaCertForEntityCertIsGivenAndOnlyExtraTrustAnchorIsReturned()
            throws CertificateException, IOException, NoSuchProviderException {
        //  given

        PKIMessage respPkiMessage = mockExtraCerts(null);

        CMPCertificate trustedCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {trustedCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Exception exception = assertThrows(
                CmpClientException.class,
                () -> CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                        respPkiMessage, certRepMessage, leafCertificate
                )
        );

        String actualMessage = exception.getMessage();

        // then
        assertThat(actualMessage).isEqualTo(EXPECTED_ERROR_MESSAGE);
    }

    @Test
    void throwsExceptionWhenBothExtraCertsAndCaPubsAreEmpty()
            throws CertificateException, IOException, NoSuchProviderException {
        //  given

        PKIMessage respPkiMessage = mockExtraCerts(null);
        CertRepMessage certRepMessage = mockCaPubs(null);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_1LAYER_ENTITY_CERT);

        //  when
        Exception exception = assertThrows(
                CmpClientException.class,
                () -> CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                        respPkiMessage, certRepMessage, leafCertificate
                )
        );

        String actualMessage = exception.getMessage();

        // then
        assertThat(actualMessage).isEqualTo(EXPECTED_ERROR_MESSAGE);
    }

    @Test
    void throwsExceptionWhenNoIntermediateCertForEntityCertIsGiven()
            throws CertificateException, IOException, NoSuchProviderException {
        //  given

        PKIMessage respPkiMessage = mockExtraCerts(null);

        CMPCertificate caCmpCertificate = mockCmpCertificateFromPem(TEST_2LAYER_CA_CERT);
        CMPCertificate[] cmpCertificates = {caCmpCertificate};
        CertRepMessage certRepMessage = mockCaPubs(cmpCertificates);

        X509Certificate leafCertificate = getX509CertificateFromPem(TEST_2LAYER_ENTITY_CERT);

        //  when
        Exception exception = assertThrows(
                CmpClientException.class,
                () -> CmpResponseHelper.verifyAndReturnCertChainAndTrustSTore(
                        respPkiMessage, certRepMessage, leafCertificate
                )
        );

        String actualMessage = exception.getMessage();

        // then
        assertThat(actualMessage).isEqualTo(EXPECTED_ERROR_MESSAGE);
    }


    private void assertThatRootCaAndTrustedCaAreInSecondList(
            Cmpv2CertificationModel certs, CMPCertificate... rootAndTrustedCerts
    ) throws IOException {
        assertThat(certs.getTrustedCertificates().size()).isEqualTo(rootAndTrustedCerts.length);
        for (CMPCertificate certificate : rootAndTrustedCerts) {
            assertThat(certs.getTrustedCertificates())
                    .extracting(Certificate::getEncoded)
                    .contains(certificate.getEncoded());
        }
    }

    private void assertThatChainContainsEntityCertificate(
            Cmpv2CertificationModel certs, String entityCertificate
    ) throws CertificateEncodingException, IOException {
        assertThat(certs.getCertificateChain().size()).isEqualTo(1);
        assertThat(certs.getCertificateChain().get(0).getEncoded()).isEqualTo(createPemObject(entityCertificate).getContent());
    }

    private void assertThatChainContainsEntityAndIntermediateCertificate(
            Cmpv2CertificationModel certs, String entityCertificate, String intermediateCertificate
    ) throws CertificateEncodingException, IOException {
        assertThat(certs.getCertificateChain().size()).isEqualTo(2);
        assertThat(certs.getCertificateChain().get(0).getEncoded()).isEqualTo(createPemObject(entityCertificate).getContent());
        assertThat(certs.getCertificateChain().get(1).getEncoded()).isEqualTo(createPemObject(intermediateCertificate).getContent());
    }

    private X509Certificate getX509CertificateFromPem(String pem) throws CertificateException, NoSuchProviderException, IOException {
        return (X509Certificate)
                CertificateFactory.getInstance("X.509", "BC").generateCertificate(
                        new ByteArrayInputStream(createPemObject(pem).getContent())
                );
    }

    private PKIMessage mockExtraCerts(CMPCertificate[] cmpCertificates) {
        PKIMessage respPkiMessage = mock(PKIMessage.class);
        when(respPkiMessage.getExtraCerts()).thenReturn(cmpCertificates);
        return respPkiMessage;
    }

    private CertRepMessage mockCaPubs(CMPCertificate[] cmpCertificates) {
        CertRepMessage certRepMessage = mock(CertRepMessage.class);
        when(certRepMessage.getCaPubs()).thenReturn(cmpCertificates);
        return certRepMessage;
    }

    private CMPCertificate mockCmpCertificateFromPem(String pem) throws IOException {
        return mockCmpCertificate(createPemObject(pem).getContent());
    }

    private CMPCertificate mockCmpCertificate(byte[] encodedCertificate) throws IOException {
        CMPCertificate cmpCertificate01 = mock(CMPCertificate.class);
        when(cmpCertificate01.getEncoded()).thenReturn(encodedCertificate);
        return cmpCertificate01;
    }

    private PemObject createPemObject(String pem) throws IOException {
        try (StringReader stringReader = new StringReader(pem);
             PemReader pemReader = new PemReader(stringReader)) {
            return pemReader.readPemObject();
        }
    }
}
