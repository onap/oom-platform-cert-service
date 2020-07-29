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

package org.onap.oom.certservice.certification;


final class CertificationData {

    private CertificationData() {
    }

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    static final String EXTRA_CA_CERT = ""
            + BEGIN_CERTIFICATE
            + "MIIDvzCCAqcCFF5DejiyfoNfPiiMmBXulniBewBGMA0GCSqGSIb3DQEBCwUAMIGb\n"
            + "MQswCQYDVQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2Fu\n"
            + "LUZyYW5jaXNjbzEZMBcGA1UECgwQTGludXgtRm91bmRhdGlvbjENMAsGA1UECwwE\n"
            + "T05BUDEVMBMGA1UEAwwMbmV3Lm9uYXAub3JnMR4wHAYJKoZIhvcNAQkBFg90ZXN0\n"
            + "ZXJAb25hcC5vcmcwHhcNMjAwMjEyMDk1OTM3WhcNMjEwMjExMDk1OTM3WjCBmzEL\n"
            + "MAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbi1G\n"
            + "cmFuY2lzY28xGTAXBgNVBAoMEExpbnV4LUZvdW5kYXRpb24xDTALBgNVBAsMBE9O\n"
            + "QVAxFTATBgNVBAMMDG5ldy5vbmFwLm9yZzEeMBwGCSqGSIb3DQEJARYPdGVzdGVy\n"
            + "QG9uYXAub3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtF4FXeDV\n"
            + "ng/inC/bTACmZnLC9IiC7PyG/vVbMxxN1bvQLRAwC/Hbl3i9zD68Vs/jPPr/SDr9\n"
            + "2rgItdDdUY1V30Y3PT06F11XdEaRb+t++1NX0rDf1AqPaBZgnBmB86s1wbqHdJTr\n"
            + "wEImDZ5xMPfP3fiWy/9Yw/U7iRMIi1/oI0lWuHJV0bn908shuJ6dvInpRCoDnoTX\n"
            + "YP/FiDSZCFVewQcq4TigB7kRqZrDcPZWbSlqHklDMXRwbCxAiFSziuX6TBwru9Rn\n"
            + "HhIeXVSgMU1ZSSopVbJGtQ4zSsU1nvTK5Bhc2UHGcAOZy1xTN5D9EEbTqh7l+Wtx\n"
            + "y8ojkEXvFG8lVwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQAE+bUphwHit78LK8sb\n"
            + "OMjt4DiEu32KeSJOpYgPLeBeAIynaNsa7sQrpuxerGNTmQWIcw6olXI0J+OOwkik\n"
            + "II7elrYtd5G1uALxXWdamNsaY0Du34moVL1YjexJ7qQ4oBUxg2tuY8NAQGDK+23I\n"
            + "nCA+ZwzdTJo73TYS6sx64d/YLWkX4nHGUoMlF+xUH34csDyhpuTSzQhC2quB5N8z\n"
            + "tSFdpe4z2jqx07qo2EBFxi03EQ8Q0ex6l421QM2gbs7cZQ66K0DkpPcF2+iHZnyx\n"
            + "xq1lnlsWHklElF2bhyXTn3fPp5wtan00P8IolKx7CAWb92QjkW6M0RvTW/xuwIzh\n"
            + "0rTO\n"
            + END_CERTIFICATE;

    static final String CA_CERT = ""
            + BEGIN_CERTIFICATE
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
            + END_CERTIFICATE;

    static final String INTERMEDIATE_CERT = ""
            + BEGIN_CERTIFICATE
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
            + END_CERTIFICATE;

    static final String ENTITY_CERT = ""
            + BEGIN_CERTIFICATE
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
            + END_CERTIFICATE;

}
