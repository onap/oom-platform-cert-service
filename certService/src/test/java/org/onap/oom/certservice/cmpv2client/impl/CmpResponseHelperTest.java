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

    private static final String TEST_1LAYER_ENTITY_CERT = ""
            + "-----BEGIN CERTIFICATE-----\n"
            + "MIIEqDCCAxCgAwIBAgIUFioEkVJsxfZGGDMEyCA8Rin3uhQwDQYJKoZIhvcNAQEL\n"
            + "BQAwYTEjMCEGCgmSJomT8ixkAQEME2MtMDM1ZDk4NTAwYzhiN2JiMjIxFTATBgNV\n"
            + "BAMMDE1hbmFnZW1lbnRDQTEjMCEGA1UECgwaRUpCQ0EgQ29udGFpbmVyIFF1aWNr\n"
            + "c3RhcnQwHhcNMjAwMzI0MTEzNTU0WhcNMjIwMzI0MTEzNTU0WjCBljEgMB4GCSqG\n"
            + "SIb3DQEJARYRQ29tbW9uTmFtZUBjbi5jb20xDjAMBgNVBAMMBUNsMTIzMQ0wCwYD\n"
            + "VQQLDARPTkFQMRkwFwYDVQQKDBBMaW51eC1Gb3VuZGF0aW9uMRYwFAYDVQQHDA1T\n"
            + "YW4tRnJhbmNpc2NvMRMwEQYDVQQIDApDYWxpZm9ybmlhMQswCQYDVQQGEwJVUzCC\n"
            + "ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL94FcmRn/g9Y9ZrEL+jKiud\n"
            + "xzDdtVLoF0ijZOGG0rnzyimzzwOjd8LA0jiZlYtpoDef95bbMeZJMKzE3bA8EMFp\n"
            + "hynqUHs/KdsLBV+o3J6EzlpYHrwypX7kOriw9o4dmPAxvJHXTu3HC2SejJjHHArk\n"
            + "FyahEJ03ypvCJx3iPvGXkLI9tZetobiVXslBJd5t0hQj+JQxzAlTwS0fV+xMowFT\n"
            + "css2IlGXfQgd88cdhXBVOE0//qln1ko3G3KeH58iIWLqh9KG660SCeoTCop7bO1N\n"
            + "abVrcXlgdE06hAvzTj3FoBxqO5KEWDPo2Dr11qRdq8bLP2T0EbTzAw4DPUwE+H8C\n"
            + "AwEAAaOBoTCBnjAMBgNVHRMBAf8EAjAAMB8GA1UdIwQYMBaAFDPaBc+EX/hCLe5c\n"
            + "d+oZIxcQZ1tHMB8GA1UdEQQYMBaCBUNsMTIzgg10ZXN0Lm9uYXAub3JnMB0GA1Ud\n"
            + "JQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAdBgNVHQ4EFgQU4dP1HuV9O+sHInl+\n"
            + "WuvdDJ63lp8wDgYDVR0PAQH/BAQDAgXgMA0GCSqGSIb3DQEBCwUAA4IBgQBWTF8C\n"
            + "sH0ir4bj7rTlJMf5o7apkXFeQ/c7+zXnSLCfXqwM6ad0EDh3FixfTC8IpW5CaENt\n"
            + "zTR7IGJr06ccwLgsigR7FxJKnEkxJiBxzkE3zFOEel3KAnV2b7KvOP7cJAzsCdcS\n"
            + "iZU475XHOw4Ox3k8fHzhTJJa0Tzw5EjQ3GO99HTiUClGrjJuYDLfen1q7IQSNuTY\n"
            + "FzxJZjyqzi34pkKeCNSPRj8Z8Q5aZiWqlmzSJmZRT83xzzeW/pQ1JwvIrWwrbEjR\n"
            + "FPXBlUa1n2HztkDgeBQfRyMAj5ixFV+s1Jj+cEYl3pjbugnuHfgBdSJokXFGBo6N\n"
            + "8PTd1CnMGWcWiMyhbTwNm2UiSr5KhQbjABjiUzDp4C7jFhIzmu/4/tm2uA+y0xPN\n"
            + "342uEZC0ZSZmpCIbQMhPaBNjSHeHj8NaLHjnt5jppLkMxScayRqMvSW07eNew2+k\n"
            + "VYJD6z6gfy4y+Y5MSLfvddq1JdPDU86TFprtD1ydcUBS5tduYQG2+1bLgpE="
            + "\n-----END CERTIFICATE-----\n";

    private static final String TEST_1LAYER_CA_CERT = ""
            + "-----BEGIN CERTIFICATE-----\n"
            + "MIIEszCCAxugAwIBAgIUEhkh+zJtXZN3K3kzQYcbp2smyIkwDQYJKoZIhvcNAQEL\n"
            + "BQAwYTEjMCEGCgmSJomT8ixkAQEME2MtMDM1ZDk4NTAwYzhiN2JiMjIxFTATBgNV\n"
            + "BAMMDE1hbmFnZW1lbnRDQTEjMCEGA1UECgwaRUpCQ0EgQ29udGFpbmVyIFF1aWNr\n"
            + "c3RhcnQwHhcNMjAwMzI0MTAyODQyWhcNMzAwMzI0MTAyODQyWjBhMSMwIQYKCZIm\n"
            + "iZPyLGQBAQwTYy0wMzVkOTg1MDBjOGI3YmIyMjEVMBMGA1UEAwwMTWFuYWdlbWVu\n"
            + "dENBMSMwIQYDVQQKDBpFSkJDQSBDb250YWluZXIgUXVpY2tzdGFydDCCAaIwDQYJ\n"
            + "KoZIhvcNAQEBBQADggGPADCCAYoCggGBAJyKZyKIRyW6cbga/I1YFJGCEEgs9JVU\n"
            + "sV7MD5/yF4SIkJlZqFjJ9kfw8D5thg68zAx2vEWIpNTMroqb1eptIn/XsFoyM//6\n"
            + "HzKrY3UUYWHx9sQMDZPenTL8LTRx+4szSen7rzrozH2pJat7kfX4EODEtQ6q7RQ2\n"
            + "hmXoo7heeSgiHoeHsPGZixPGzcB27WBaY00Z/sP/n+f0CFaE04MKLw8WeQmq/RkC\n"
            + "pj628+eBK0lGtEmUcT7z4CBy4x3hbhn9XHOb0+RlDk7rqFbsc09vHoZK2BfQ/r6e\n"
            + "HguZjBQ5Ebqf6PiLF3HqkSW73toIdIy/olvQ2dLbOEyI4OnlObc+8xs/1AC7l9xX\n"
            + "FkXY+NBv24KG1C2POXx14+ufHhWY0k2nIRUUlkUIJ7WGMWbuiNUXc1wSE1VrmY/c\n"
            + "iXlhsJERqFc6bL/STlhOGuwmkdAD1/K8WS+o/QmIIX6cXlOR0U9bHMbD40F9fur6\n"
            + "PV8wSKcQQNd0VHRLhmFwo4kkhZpDpuUp4QIDAQABo2MwYTAPBgNVHRMBAf8EBTAD\n"
            + "AQH/MB8GA1UdIwQYMBaAFDPaBc+EX/hCLe5cd+oZIxcQZ1tHMB0GA1UdDgQWBBQz\n"
            + "2gXPhF/4Qi3uXHfqGSMXEGdbRzAOBgNVHQ8BAf8EBAMCAYYwDQYJKoZIhvcNAQEL\n"
            + "BQADggGBAFGsyu5nWycdk8iva+uY98QnPQe/M6uaUGUis0vGn9UYxoz5ddtpF3Z+\n"
            + "MsHgbS51BH9iRYn4ZkQoRoukIjt1iO86d6sgpUS5AStCXsylL4DwAY5G/K5i/Qw5\n"
            + "x0lP/tRYwqh2tUhmnx1xZLOWbRFZ63A0YHdguj3CqaXQ/cxafYZe0zcNhX3iH3gf\n"
            + "5kHH8E682RT0x4ibb1JtPioQ48+pweyfMlOJkJ7WmZEfiVQitQSSNOnw1hRORiUz\n"
            + "oFb0MlYHqe/9lIb9nmzD8QQ9q0H8J6RBCFsntx/Z6oUM8GHr80zAvNjqFfR14lOo\n"
            + "jp05w2mr7wxIHFpM6h1HGY1QaeGp6W/fi+N7+gSL3nu1LzXVCYNCTcGkBDeasovB\n"
            + "ma70KHGO4ZyRcEMKFCxxE8y4GZnw/EhMhDDevXAVsHEzr6XsBCJkC8e2l3iW5IKH\n"
            + "4N/f/k06d4kS5pL290dJ450zx/mBxYGJm+pPHZfDszqVeKn1m1ZhGT80150OePGQ\n"
            + "Cc2ir84HwQ=="
            + "\n-----END CERTIFICATE-----\n";

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
