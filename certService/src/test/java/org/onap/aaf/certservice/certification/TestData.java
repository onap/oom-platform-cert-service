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

package org.onap.aaf.certservice.certification;

public final class TestData {

    private TestData() {
    }

    public static final String TEST_CSR = ""
            + "-----BEGIN CERTIFICATE REQUEST-----\n"
            + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh\n"
            + "MRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKDBBMaW51eC1Gb3VuZGF0\n"
            + "aW9uMQ0wCwYDVQQLDARPTkFQMREwDwYDVQQDDAhvbmFwLm9yZzEeMBwGCSqGSIb3\n"
            + "DQEJARYPdGVzdGVyQG9uYXAub3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n"
            + "CgKCAQEA13K1LrQ1L6eL7B8K4kucNct0sSjZe7Ww91V40s6mjcWajeFJk+pObZKz\n"
            + "BfnImkVJwxdNMDD6tX16wykbGfQPyh4BBiAjLVk9XSeoPHFRBQ4LKTuyPtXhEXyr\n"
            + "qwatYXGWZE554qq64pbReddOUJHgMc38SrOk/eMAKxB0uRrXpA0mPH7zwIZ4X8g2\n"
            + "PoxJKI1BSYc8kOvvujsGSMw3e5nS8A+doFUwVi3jJMnaVCoZrvJbtREfXHZqBLQ5\n"
            + "XQ8mNpIFfmGYF/tvW/O6LBdlZkuAQ9i4FBgf5+HdIVZOXrn09ksIZxW6vxIvAVi0\n"
            + "5AOSgXictyphcNP2i/erBeCQCVB7MwIDAQABoEYwRAYJKoZIhvcNAQkOMTcwNTAz\n"
            + "BgNVHREELDAqgg9nZXJyaXQub25hcC5vcmeCDXRlc3Qub25hcC5vcmeCCG9uYXAu\n"
            + "Y29tMA0GCSqGSIb3DQEBCwUAA4IBAQBXH2nRwodQRJTuyrLe/VSg3PUdcPyAx2Ew\n"
            + "63tWiGO+qWo8rK2a9Rr/t/zkQe2lx6NHqcMc2Rt6NeKGbrAvHGxTiYM35gktBdxG\n"
            + "UaQS1ymrBWHAwbC+kv78r+5lCfafNm/EVdhUZbEw+crsw2wx4iKEW0byS4Ln0o5g\n"
            + "aXVUW3i4G5FaYiYBUIDsujDdnH1IoxunEA6pDzDv1h6R9/TYu6Se8HToREIjOPBZ\n"
            + "pDI5lDRu0YmI8r+TmAU3tTT1sY2WVxYDnhJut9ofegfMPQV4FIohxtPcCfoLSWti\n"
            + "ml6jbcFqDvlzq3B3CXH9HU3jdJt33iSjCQGsSqy6bmCOdMS6XTPU\n"
            + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_WRONG_CSR = ""
            + "-----BEGIN CERTIFICATE REQUEST-----\n"
            + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh\n"
            + "MRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKDBBMaW51eC1Gb3VuZGF0\n"
            + "aW9uMQ0wCwYDVQQLDARPTkFQMREwDwYDVQQDDAhvbmFwLm9yZzEeMBwGCSqGSIb3\n"
            + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_PK = "-----BEGIN PRIVATE KEY-----\n"
            + "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDXcrUutDUvp4vs\n"
            + "HwriS5w1y3SxKNl7tbD3VXjSzqaNxZqN4UmT6k5tkrMF+ciaRUnDF00wMPq1fXrD\n"
            + "KRsZ9A/KHgEGICMtWT1dJ6g8cVEFDgspO7I+1eERfKurBq1hcZZkTnniqrriltF5\n"
            + "105QkeAxzfxKs6T94wArEHS5GtekDSY8fvPAhnhfyDY+jEkojUFJhzyQ6++6OwZI\n"
            + "zDd7mdLwD52gVTBWLeMkydpUKhmu8lu1ER9cdmoEtDldDyY2kgV+YZgX+29b87os\n"
            + "F2VmS4BD2LgUGB/n4d0hVk5eufT2SwhnFbq/Ei8BWLTkA5KBeJy3KmFw0/aL96sF\n"
            + "4JAJUHszAgMBAAECggEAJ1StdsU3IGf5xzUzi3Q6JCfsOZs3eLoGgGB+Gh3XkfIM\n"
            + "8PG7uOEBSEeLnv+me2NCv/a1BKMsYY1yp8YNSIOhjkhD75ZWVaUA6syejcox/DZA\n"
            + "G1rmg0oQOF0GCcbCSBOwXMdmwNZiH5Ng0llX1qWKxAzSjeCVsjOKiFIMvO4Fh9D4\n"
            + "9Io6/dRRNCxB6MEs1GT5IDfCV2PGDIalJ3znFqDnfdu9RDEDfNVHSUr6Jdu3Hrf5\n"
            + "3qCcSEkMGuXYLotCNtTP1x0H0wW5gVpcbQEb29qdmHL1qkp3UiA3afsHnO/3k0gv\n"
            + "gV5FxaldugyZAjqUGERdKaY6BMDJkDuu0qD0tPQK4QKBgQDuP5X5BcQ4iHNej+il\n"
            + "xxT8QaEcZj0YEzcXzfm3ztZP7g+Jc1MbQXh6BuHLkXG5LeCwdnmk+LUD0MLoUSm3\n"
            + "N2ZdtVuOHX7VEBrhrTwK/kMDpC7ganQzfvgOr9WQGmgGMRiUYAyK1J/x78yX967Z\n"
            + "IAzdVZ/JSDdsyA983JckLL7CPQKBgQDngDkEJKYGfDt2mfItD8c8nhczGbDdoyYh\n"
            + "s93ppTtgzFoNgFL4y/DOvisWMGgoeeYXSgH5uoPv6yY7IIkQzYySY6qQ3gmk1/X+\n"
            + "bO+IsKVtlHBzqqojFteg3MfVojisMoAx6y5aBw1BXE2nAU8yWBTtuk+3KgGn9Oxk\n"
            + "+Z4rdP06LwKBgA4b09zIW6NhaTubWBKhJHv/wvO0lj+bu7J8LyKUbBqVpXPlUXGW\n"
            + "wfSv/aUZetuVfO3WRkPfupB8R16Ml+TSsgwwljhnRMCHUKA2qwyXnA5WJbSCeVkn\n"
            + "Vrc/8Gy1M53SQHtg6L079DDWm44QS9ltzXU6Adlgnm+htVEWmxi4UZ+dAoGAfr6z\n"
            + "+LG7+GcCA2AruEIgOe7wErkpHV+am+8nOymMxeV8FFJCmxbFQ9vYKTDdhfOfZvbM\n"
            + "+BYG8E8VQmAAyyNOqENK+j+mlgrrEp4/0t2r5L/VhW5V8hoqelcGTc+gKZ8IkswJ\n"
            + "N58Owc8wcJQF8TFKXBGaXVTxTSyKVIpZ778AeV8CgYAAvuicDkdwWv5EhDFf3aTI\n"
            + "wfRFYflA6oiygnI63HzVyY4a+SyZs+nQpB5HBDo+Lyz8RaVRC5E7jQ8kiXJpxAu7\n"
            + "1wnspz+pa3q61yR32N+zGuub71FXdLWSOlys6rzJqvqYihKxY22C2TyDyBCR2tMj\n"
            + "mdnshXNAJfKkfghkJhFHrg==\n"
            + "-----END PRIVATE KEY-----";

    public static final String TEST_PEM = ""
            + "-----BEGIN CERTIFICATE REQUEST-----\n"
            + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh\n"
            + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_WRONG_PEM = ""
            + "-----BEGIN WRONG REQUEST-----"
            + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh"
            + "-----END WRONG REQUEST-----";

}
