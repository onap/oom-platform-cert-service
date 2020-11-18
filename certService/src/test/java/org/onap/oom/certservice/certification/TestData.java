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

public final class TestData {

    private TestData() {
    }

    public static final String LOCALHOST_IP_IN_HEX = "#7f000001"; //127.0.0.1

    public static final String EXPECTED_CERT_SUBJECT = "C=US,ST=California,L=San-Francisco,O=Linux-Foundation,OU=ONAP,CN=onap.org";
    public static final String EXPECTED_CERT_SANS =
        "SANs: [localhost, onap.org, test.onap.org, onap@onap.org, " + LOCALHOST_IP_IN_HEX + ", onap://cluster.local/]";


    public static final String TEST_CSR = "-----BEGIN CERTIFICATE REQUEST-----\n"
        + "MIIDNTCCAh0CAQAwdzELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWEx\n"
        + "FjAUBgNVBAcTDVNhbi1GcmFuY2lzY28xGTAXBgNVBAoTEExpbnV4LUZvdW5kYXRp\n"
        + "b24xDTALBgNVBAsTBE9OQVAxETAPBgNVBAMTCG9uYXAub3JnMIIBIjANBgkqhkiG\n"
        + "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxRYVFOosyABMq+yANz9phmYyfmHbw9F9r3Ca\n"
        + "v1oZ2xw1LbF2HGBq8F45nXfMjX2H+Lxk8m/XmIDb+9lzINU6J6xmDrKZiiif5ORa\n"
        + "oRENfQZNWkAWPguWyKGtHk6ueeSjS8D0SWwloc1g0hB3GREffocuJ24K+t2nXglf\n"
        + "7XVgmHxjiE8k+pD3SUo5rA7Fx1TmLguEA8aCRGaYg/aofCNe9hDm34iqUzm5tPPQ\n"
        + "OgR3Lpqx2JW0iJYbQXmX3cG/RE0qFl+rgrNhCd8ptX7IUiWtQmttssR3bE8JVgaf\n"
        + "x9EU9GZ5dZXifSFJzs42UY7X6DPiQDFerfWRNc3dRTYBlkbTiwIDAQABoHkwdwYJ\n"
        + "KoZIhvcNAQkOMWowaDBZBgNVHREEUjBQgglsb2NhbGhvc3SCCG9uYXAub3Jngg10\n"
        + "ZXN0Lm9uYXAub3JngQ1vbmFwQG9uYXAub3JnhwR/AAABhhVvbmFwOi8vY2x1c3Rl\n"
        + "ci5sb2NhbC8wCwYDVR0PBAQDAgWgMA0GCSqGSIb3DQEBCwUAA4IBAQAk9lRwbWyL\n"
        + "VRWSM5cBiRK2nCKhfur20khHFQgYcPAD8BRXEk5/F0KBSBMNGMrBgOYqq3IYsoMc\n"
        + "mvs9KKVqIV3+lBej2QTF3cxdHYPTrCvvkoheMYt5qqjkrQRbiydzj7/wvflmBXs1\n"
        + "7TViU+TqoJ8q5DWTEvv0X5t/WF6sSIxFHHKD7otDXPW5CAeqXO5A99bTrSiXmVAH\n"
        + "72/n/JFHueURv+NbpHyBNXweezNnB5BDrrqduabkhn31ThA0wzePDNR02aXwxxHn\n"
        + "77sSa3iuAN3IaVWYfxCOX4fEw8F+wMAAMTiWItM8Lc9DT5rsYeRHAZmOMVEnowc2\n"
        + "3eKLFeWDIi2Z\n"
        + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_WRONG_CSR = ""
        + "-----BEGIN CERTIFICATE REQUEST-----\n"
        + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh\n"
        + "MRYwFAYDVQQHDA1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKDBBMaW51eC1Gb3VuZGF0\n"
        + "aW9uMQ0wCwYDVQQLDARPTkFQMREwDwYDVQQDDAhvbmFwLm9yZzEeMBwGCSqGSIb3\n"
        + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_PK = "-----BEGIN PRIVATE KEY-----\n"
        + "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDFFhUU6izIAEyr\n"
        + "7IA3P2mGZjJ+YdvD0X2vcJq/WhnbHDUtsXYcYGrwXjmdd8yNfYf4vGTyb9eYgNv7\n"
        + "2XMg1TonrGYOspmKKJ/k5FqhEQ19Bk1aQBY+C5bIoa0eTq555KNLwPRJbCWhzWDS\n"
        + "EHcZER9+hy4nbgr63adeCV/tdWCYfGOITyT6kPdJSjmsDsXHVOYuC4QDxoJEZpiD\n"
        + "9qh8I172EObfiKpTObm089A6BHcumrHYlbSIlhtBeZfdwb9ETSoWX6uCs2EJ3ym1\n"
        + "fshSJa1Ca22yxHdsTwlWBp/H0RT0Znl1leJ9IUnOzjZRjtfoM+JAMV6t9ZE1zd1F\n"
        + "NgGWRtOLAgMBAAECggEABG7Etp21uCHZl5xQHe39L5qo1BLbYIIbs5Byyo76OeVe\n"
        + "hNKS93xrq1BTN2l0XlJOdpe2JYXCcZmkWPvBDSH+ltnXycjWjzbusbU5HJpHlWJI\n"
        + "5xi951NXZtfMDvxyDCfKTG/gjq4yAnueC9t28kdiT/Q2Y4ikEpRdqU3IrIyRSZyo\n"
        + "duBWfr3ADU5xxnWcTt61vpAQsYh4XiwosyBhXTwsMnWgRkOr6e4Vu2J+wL6vUid7\n"
        + "7VOr8PtOu73CjYA7zIy0XSOrRq5Q3H7eGgyln0AQtaO0qO2COJHa6cv3yIgesSUL\n"
        + "8ltiWAGiZZ6qZ72B3tDnKmoEkuvE1/KpeitewGcKkQKBgQDxqHR9IJBOBRjjmyKi\n"
        + "ra54mJjKwHQ5dxJQpVFLEIRL2H3ujjRNH3ggLAOiH02TqZGS3fnTsTsApnkpy5J/\n"
        + "qtysjV0SFxP0gprQQ1wM64NWTaeDAt9lXII918YrALAAR86ikrTxOyoS1kqOSEmX\n"
        + "QZu3VrgkAvs+V5ckvEXjZWxO1wKBgQDQyHErT7aJeUBukj6skahnzhmVNTmjsn3P\n"
        + "zyy/cOmBz8wn7JsxgTdpWETpHOVsO0G5wg9Ts7V3Krh6AmrEf/6/NlWLdygDfIvM\n"
        + "9Jxc8D2dLEUUm18jw15tEsQtItj3Rt0e5GJiQO1rNBMb+2Q8FDlX1tu0xgMMZ4En\n"
        + "izjnAEKObQKBgHnWZrTXgCn14/CNPM8sJfTjatV+Zpq6b999GhlwgGMFCakGxVPE\n"
        + "8/m0dzh7887pBV440EZs6sSPKjNqUbhQWuYcd7oxLHxwhMFP1M8mxpbym+wvvJYM\n"
        + "KBYp/d2cgSADFClfMh8Vp0bMB9bol0HNcEblT/3ICwgJfUimK85USmENAoGBAMnl\n"
        + "O0LF19/C6CLEu2THihGvxR97k9yPy4f8cOpD9xq35lWpQT4zFXGCkUjXz6fE+b73\n"
        + "QTkQ7GdrYW9jDPouSBuCIGE4ffI5KzusQ9S/4OUvnTHbObpsv9A8OIbpTuR4m3W3\n"
        + "JsiavrxPZDdH99r9N6KQvG9omCQTp1qlEAaaQsJVAoGBAO4ccgmzbku62OKLIAqy\n"
        + "JN4Z8i9PaCEPgqfs0THMIKuj1l8FO723zTZMwsBWgZ4Gd32EbYW9tbwvLblGdd6H\n"
        + "xAXLfLjRWefKm6i2iIdkeNMJmTTCYjHFyoTe84Miq9d3cEnW7s055Pm1uxRPXYk+\n"
        + "GFRpHltg2qX2u6M8ryskAMah\n"
        + "-----END PRIVATE KEY-----\n";

    public static final String TEST_PEM = ""
        + "-----BEGIN CERTIFICATE REQUEST-----\n"
        + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh\n"
        + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_WRONG_PEM = ""
        + "-----BEGIN WRONG REQUEST-----"
        + "MIIDIzCCAgsCAQAwgZcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlh"
        + "-----END WRONG REQUEST-----";

}
