/*
 * ============LICENSE_START=======================================================
 * PROJECT
 * ================================================================================
 * Copyright (C) 2020-2021 Nokia. All rights reserved.
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
        "SANs: [onap@onap.org, localhost, onap.org, test.onap.org, onap://cluster.local/, " + LOCALHOST_IP_IN_HEX + "]";


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


    public static final String TEST_CMPv2_KEYSTORE = "-----BEGIN CERTIFICATE-----\n"
        + "MIIEfTCCAuWgAwIBAgIUdF/Efkrll/wuwfT2w+RO9cJrLvQwDQYJKoZIhvcNAQEL\n"
        + "BQAwUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1MRUwEwYDVQQDDAxNYW5hZ2VtZW50\n"
        + "Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5lciBRdWlja3N0YXJ0MB4XDTIxMDcw\n"
        + "MTE0MjUxMloXDTIzMDcwMTE0MjUxMVowdzERMA8GA1UEAwwIb25hcC5vcmcxGTAX\n"
        + "BgNVBAsMEExpbnV4LUZvdW5kYXRpb24xDTALBgNVBAoMBE9OQVAxFjAUBgNVBAcM\n"
        + "DVNhbi1GcmFuY2lzY28xEzARBgNVBAgMCkNhbGlmb3JuaWExCzAJBgNVBAYTAlVT\n"
        + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEAteHOLPXl93XwT9yDa\n"
        + "3SS1nOV1KN+wZApOqvzbA7NBf8Pw/72i6LXvAHABfzUWkUQnbWw9WygPiEbWfaGO\n"
        + "AArlcVTtXnY5RkfOpt5UXBokZwn1PaT3a1hXrFHA2W2jwD7q2Ft3gRNNFsxenJYi\n"
        + "BcsnJOBkS3hc+zAG7mYw5gYdnPX69D+/+4G0N1k1bBA5rsaK7F3h54NJfPxILpJB\n"
        + "0yuVBE0QBEaHVoqDyq6AwmiHpk2Nt5h4TMDiINwwkoxvNG+ZCaM2CSFbmUyq3j1c\n"
        + "xZ/ZrhLIZ1rRlMTPz+lA1wo1yBB2AoDtoPsAlQG5nIWfr1d5ToUtLOxZ+dVTQkJi\n"
        + "VQIDAQABo4GkMIGhMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAU0pApkFDmdpd/\n"
        + "O4a/byDxTEP2UvAwGAYDVR0RBBEwD4INdGVzdC5vbmFwLm9yZzAnBgNVHSUEIDAe\n"
        + "BggrBgEFBQcDAgYIKwYBBQUHAwQGCCsGAQUFBwMBMB0GA1UdDgQWBBS3Wd6mIs8L\n"
        + "ZxAfYyvM/U2g/XM2wjAOBgNVHQ8BAf8EBAMCBeAwDQYJKoZIhvcNAQELBQADggGB\n"
        + "AF9ecmK2jpi4u8NERcx/HGXGxZDgj8EpRMxzHAPMa8gJRwm87O4tn4QZHFSnDYdl\n"
        + "ZmDWhA1iEvOOrTNDimRslAOoOE3bRAiA5c3cYVhancZq0OqS8dUOyOxSwLoXtnFC\n"
        + "RGnHMmABq3OpdWpeRTv3iLzPDeybP+hJn3WrlX9v4kjwgO5mbwQTG+MCzTWgNsyy\n"
        + "xeEF0pH6JYDyIoRNWwrrRG7zWzjIaFuMtOfbN1lVaaycMsRw+IxvojsDmXK2PWOA\n"
        + "HRu5k2xpQfHF/waN4F0vzKxmHyCVnwbwx6by6G2FJo8CYQeDwgyRARm3xywu1Wwt\n"
        + "CIbRhIekMFY7RLPs5vkTPxs65numYZlbI+z+EdYofQBbJFeyqpzkoIQqWyfkwtki\n"
        + "x7sJ9B6sUPIibxQnMI+tdX+wz5p5Ift+nnSUbx8N+FVvc9kEbvvPpzJRpAyQBVIq\n"
        + "CIvPjAmyZNIztpxLi0bh7voZqH5ZwHdWWEaDrwg5cMjFZMRUGRPUQ8NN04KGiAww\n"
        + "hQ==\n"
        + "-----END CERTIFICATE-----";

    public static final String TEST_CMPv2_TRUSTSTORE = "-----BEGIN CERTIFICATE-----\n"
        + "MIIElzCCAv+gAwIBAgIUcwn218DR9QI1ORrGSb2exaf2tp0wDQYJKoZIhvcNAQEL\n"
        + "BQAwUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1MRUwEwYDVQQDDAxNYW5hZ2VtZW50\n"
        + "Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5lciBRdWlja3N0YXJ0MB4XDTIxMDcw\n"
        + "MTEzNTIwMloXDTMxMDcwMTEzNTIwMVowUzEVMBMGCgmSJomT8ixkAQEMBTEyMzQ1\n"
        + "MRUwEwYDVQQDDAxNYW5hZ2VtZW50Q0ExIzAhBgNVBAoMGkVKQkNBIENvbnRhaW5l\n"
        + "ciBRdWlja3N0YXJ0MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEApU2B\n"
        + "yG6Y5D72dC7jL7kW7HxMDL93sKGo/9GriPL/re+ogipg9SezZoX0UbYH6ZCuPlPn\n"
        + "GKXaLDdrwX/oAaYnq1h4hA75Fy2P7Im3rcuw01F7kBk3bSbFSU0RLbr0POuP+rNr\n"
        + "IXNpFS1tonTpLgS0l7vfMkAfbBBlFLuSZQUD6oq0OHB60uBe8vOF7olq6bewduUz\n"
        + "jNgqjoUz5cR9cuWCXMZUA71+ZEzSNDJtDeFpkd00wklB9Q86JQ3/5poe4ALKuyRj\n"
        + "+6ltNdRtybHY+gT9iltDqJ7d9JWwG5EIBjYWlqLAHV1YP3XnonLiyCfArvnp1fLZ\n"
        + "vOdyxk0cA21EFF6b6MUGI3bMJPdwYjWEKkVFETqsbpxRyFNjqJ1EUibLttRnOKeS\n"
        + "COw8KdP+1QTvygA7lb75lCnVpn8JKDy2FbpuoEnSkA6o/25tlM5BjzFrKeCCdO8t\n"
        + "lcUFlQsxniSXaZF4i46U1BI1x5onY82hpLB3kErPCCA7Y4wu6fpmuOLxvvR3AgMB\n"
        + "AAGjYzBhMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgwFoAU0pApkFDmdpd/O4a/\n"
        + "byDxTEP2UvAwHQYDVR0OBBYEFNKQKZBQ5naXfzuGv28g8UxD9lLwMA4GA1UdDwEB\n"
        + "/wQEAwIBhjANBgkqhkiG9w0BAQsFAAOCAYEAf6cQKAUp1oz/wNu+wGr0ihCtYoTv\n"
        + "0E8Sj6NaVg25iZWwE+eH9iJ5tP84ofVVBSKbnbcWcKOiBWdwMHK926yWhDq1Okj3\n"
        + "cKn4PEo/Tp7DWgSxaBFWvafIMYgD08AeCSBykdwodNxhPWIMIe0Wv+timjacYUFV\n"
        + "Aq8IjFfEwpznlbJWNoctne5YFcbGbo+Z3cMaFG0eWtjpVg5pF3p+D35FBCeNzIx7\n"
        + "PjI4blyKAAIVBppYY7mTC9iEvu1Djt9653LOj2ZalC8mj6ZnvSxEhcQ3PJle8VvT\n"
        + "jq1pCYNegPATlT3eRKXlQOUQQtkw2NK9jQCCbwHnQHreL76iGhoIyFfR8P/EA2KU\n"
        + "CCMFg1yrToWJxDhkuBHu7vqbceC4YNGU1wl7nGJA18PDpglm0WI1mWzh7s+oeNNr\n"
        + "vvPaTimegOkO/u2vbl9McNdSu5Chj+gUpz6w6a3DiBeROYAVQaw44LncJtaGKojU\n"
        + "Q81F/bSyUp6jkdo5Dx2pFQDLDdhmMF4txiqG\n"
        + "-----END CERTIFICATE-----";

}
