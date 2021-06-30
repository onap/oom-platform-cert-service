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

    public static final String TEST_CSR_FOR_UPDATE_KUR = ""
        + "-----BEGIN CERTIFICATE REQUEST-----\n"
        + "MIIC5zCCAc8CAQAwdzELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWEx\n"
        + "FjAUBgNVBAcMDVNhbi1GcmFuY2lzY28xDTALBgNVBAoMBE9OQVAxGTAXBgNVBAsM\n"
        + "EExpbnV4LUZvdW5kYXRpb24xETAPBgNVBAMMCG9uYXAub3JnMIIBIjANBgkqhkiG\n"
        + "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwc728q6xImqAY/JJmtkJgvlS5prVhVsgCYEq\n"
        + "2AOJMbsq40AluCchRr5s22KFBRSzhYX6tfFY07NHG08rF/HBUn5FdhChhEQWmZ2U\n"
        + "ULKOkiCTRmt8THuRR40RMt+DadOCB40kONniAYHDYiGnWuI4BJyEEGECUj0LR5Hz\n"
        + "jAujvt0aU/3h7b6Obq0dejBWNSurKJ1mIrSOU6RCkaM2r+s6lP6LR4J+Y0S68o9C\n"
        + "GctIo5QX0ik8axX2a9yqDA7nVRH1DLbKhEd6aHNYBZKpgkSt/ATzYmO7+SgE0Qfn\n"
        + "1jfj++ZsON3a8fayJqfibaTlLAyvKyAXfKOGnzb6/1q8oAQfKwIDAQABoCswKQYJ\n"
        + "KoZIhvcNAQkOMRwwGjAYBgNVHREEETAPgg10ZXN0Lm9uYXAub3JnMA0GCSqGSIb3\n"
        + "DQEBCwUAA4IBAQBxENehnBC8qpPZtSI+OQS6lu3gQj27r3Z5Tfp0JGrBvRKWBV3I\n"
        + "0lISiW9pcDsQG5TmJ4GwumYh+8z7Hml7dVNFpgglG9taiJ4aZFUzLb9wzqs5HzxI\n"
        + "DJnXtdpc2jgVf2eCwEU5uQbZYbCXZb6cFLY6TLZ9JLa/3iJ99u3kGwjhmoiFI+pD\n"
        + "OAqRjz4D3f2JYplS6XmnMerWikRWrohyD+3d3tF9Qzg11nnU/x4m0dct+v1Zs6Xl\n"
        + "JmhgIUKecUBzgqVfk/hCCGSL9O86qDb89SCXw35aqPx6S+QFk7QK4sQTFavhCSa8\n"
        + "h9lh0I2tRqhEVdXGmpXqJZLUjesuCC8Jpxtx\n"
        + "-----END CERTIFICATE REQUEST-----\n";

    public static final String TEST_CSR_FOR_UPDATE_CR = ""
        + "-----BEGIN CERTIFICATE REQUEST-----\n"
        + "MIIC6zCCAdMCAQAwezELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWEx\n"
        + "FjAUBgNVBAcMDVNhbi1GcmFuY2lzY28xDTALBgNVBAoMBE9OQVAxGTAXBgNVBAsM\n"
        + "EExpbnV4LUZvdW5kYXRpb24xFTATBgNVBAMMDG5ldy1vbmFwLm9yZzCCASIwDQYJ\n"
        + "KoZIhvcNAQEBBQADggEPADCCAQoCggEBALx14k3KjLMo3WDYQVA9Wpyn9HBhvMHp\n"
        + "vNak4joQ2ONV/F8sWqX5WZvKzZYKM604uCbsIRbiDGi06mJGYne5SydoboK5Z6xk\n"
        + "FU6Jmkyrq8Gd4iPSszzYeikNu+noqqnHfID8+1ipUCiJGpZqU9k4ymXoVJ7m/spL\n"
        + "rPQQ1YdMusmBRoQ5sb6FiGxjoCEWA7gMlYxAdZzsSLloJTe8TDfPvo43gR2rr7bG\n"
        + "V8KCLwssoY9GQdedLbwmZ+4aqH6F761zS1MCNgQP6Y9x1Stmt8jVZzpt29SvaFEK\n"
        + "BKcvsY2tmA6BzNxfQylj51NcKz4yuwW0NATStH+koz9/L4C/OGz/uAkCAwEAAaAr\n"
        + "MCkGCSqGSIb3DQEJDjEcMBowGAYDVR0RBBEwD4INdGVzdC5vbmFwLm9yZzANBgkq\n"
        + "hkiG9w0BAQsFAAOCAQEATNYNOSIhsqwzRsz8vjiRFGSv4yfblyYhUjHRAtxZUDId\n"
        + "lpcyM0v1wu4loqKyE2TuSpyD/orKG64t/NtmpUw9u72TEsEvcO9jXvHJjWLhi7yg\n"
        + "hxX9GqWzMlmBPBDPgOX5UFAxEKUZyWVIuV4BQ4KJP5FQHvYbMUb0bbHYQ+tDWyLv\n"
        + "xbgLH4GTUGFwSJdacGWQanQj5wWDH/oZ8wW4IabKhYNR7FAnkOpgoa5Nk7D7iCeh\n"
        + "r16PTrcpGjso6wnziDcNYocRMcsFbu7lVpy45/eMou8ouwLOtKhqOm+xwjsPa3Hb\n"
        + "eBScc5yFZ3WFWZavC7jnRrCY1YCjEWNjo36xQ1asqA==\n"
        + "-----END CERTIFICATE REQUEST-----";

    public static final String TEST_OLD_CERT_FOR_UPDATE = ""
        + "-----BEGIN CERTIFICATE-----\n"
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
        + "-----END CERTIFICATE-----\n"
        + "-----BEGIN CERTIFICATE-----\n"
        + "MIIEszCCAxugAwIBAgIUNMec9tfRDSqOBRFi1dX7vD+2ToswDQYJKoZIhvcNAQEL\n"
        + "BQAwYTEjMCEGCgmSJomT8ixkAQEME2MtMGpiZnE4cWExZm8wd2ttbnkxFTATBgNV\n"
        + "BAMMDE1hbmFnZW1lbnRDQTEjMCEGA1UECgwaRUpCQ0EgQ29udGFpbmVyIFF1aWNr\n"
        + "c3RhcnQwHhcNMjEwNjI5MDY0OTA1WhcNMzEwNjI5MDY0OTA0WjBhMSMwIQYKCZIm\n"
        + "iZPyLGQBAQwTYy0wamJmcThxYTFmbzB3a21ueTEVMBMGA1UEAwwMTWFuYWdlbWVu\n"
        + "dENBMSMwIQYDVQQKDBpFSkJDQSBDb250YWluZXIgUXVpY2tzdGFydDCCAaIwDQYJ\n"
        + "KoZIhvcNAQEBBQADggGPADCCAYoCggGBAKoF7nK43P0ZF/mYEBmNFXCp2ffkk+pZ\n"
        + "KXBtTEcU0eVfANV34E4vDrqRsI4StEvky4lbYmOPLyJuZntdsWrP/kn/dPCGXA3A\n"
        + "/PN8ZckPCAAYpkBn8Sl1WqimTy07M+O3E18DtNpNK/Q5edYm+FqWSpUXv94C3n6U\n"
        + "eW++ViceYg4eAC1PdkMKdewTGgXf3+YHCWrIoIyj3p3B9JxD7WUU3LFwRGGZycUM\n"
        + "btekaTexht6u7bLzWtbX5dxpm0nGWYkNJVmf7JI8q+0jYuCCqwcazS4r2zJ9+PjW\n"
        + "2rVtNGTuQ4Fd22mksTL+AQnKIioc4BZuW0NC4zcOgfmsgQvplGk5difcDYxu9sxd\n"
        + "QFjjL+2uQDZ+DLMidasGDlDoqRApgWIgj1qfYN1SMmrEfGJZ1nwNVq9fzk0WeG4H\n"
        + "ZeAb9gSpqJOK6ZzwAtM5y6Qq9z8D/8ShcKbb7MphLbJuFTTr/YVhMPEVDHp9DfLl\n"
        + "Xo8FnBBEZePOEbgOfNWo9aNH8FW3pw5fwwIDAQABo2MwYTAPBgNVHRMBAf8EBTAD\n"
        + "AQH/MB8GA1UdIwQYMBaAFH0zWpSdAzQKkdTPfgXmxVRRwgO2MB0GA1UdDgQWBBR9\n"
        + "M1qUnQM0CpHUz34F5sVUUcIDtjAOBgNVHQ8BAf8EBAMCAYYwDQYJKoZIhvcNAQEL\n"
        + "BQADggGBAKeARtppxLhu8Ynk9DrEYAfdupoJDIe4+DGbUrCmv/9HqZ3ElRhzXYgj\n"
        + "RBJX3J4uxwMpFPdWNBOg1gsWaPbGXHh8+U4coG/5cVnZnf3DmOzYbUniau8y1grP\n"
        + "icr56Kt7+CkQ0R+YlTupF3s9Z9HenThp7bd97xY5pE+iwpma63fpraHUAoMjAvDa\n"
        + "TKvQP4YKzIcVnjkDw9jQLr4cqa7ZyMDQYMM7yaBY6Ltl61mOqCixCbLY+K2iLWIM\n"
        + "thOh+WttqvQz1OslxVEuqfmjN30paS/qIf828KdzxPCzKDgMUOf7oLFTkBBpehYY\n"
        + "aWyIAMwhDkX+Ev3Ex8ftsFbRQGV9c/p2OQwvLGS9wAqU23ZDrHZM1ekByDNEeEiR\n"
        + "SHdV8KMI2nCBt6XTxXSz900NDWIxirDODiZe9HbIRnaZR30A2T+SxIqaJl5vUC62\n"
        + "yKn3brqT3U22Seneigl7mo5mhKlO0tq+siI+V4OyNFHZvrGACZQ3qaATfZ3bSwEW\n"
        + "bH5B4elthw==\n"
        + "-----END CERTIFICATE-----";

}
