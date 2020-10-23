/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
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

package logger

const csrWithoutSkippedProperties = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDETCCAfkCAQAwgYIxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh
MRYwFAYDVQQHEw1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKExBMaW51eC1Gb3VuZGF0
aW9uMQ0wCwYDVQQLEwRPTkFQMRwwGgYDVQQDExNjZXJ0aXNzdWVyLm9uYXAub3Jn
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxhQiSgyYGpEfX/HuCFwT
GHkLe1CheKz2CQzSP9an5BSdET1OgABmuJjtnXZzKpPAZCGJX2QTyDE9zvdTN0Ci
/8WRL/m2tWUPbt8qRVW36PSKazpB+ELZjQi3rmYtmWUlRuJNfLcksK59pcD5W46t
d9eettkex0FAcxpQE/ukhpW9r6QrmlQAQHuF1rBw6uJMGzFSPWh9XFLFbxZJyJCu
AIycvT95bgtot3EMPwGkxAYzxtAu6D5/n65nIZ0f9BuuNFtmnoHmn/9fPUnZHA0h
qP9kXAAU10S3gig+Na6DeZFBE1y9jCt4vmSq2ssBO24kOAHrg5GrqEsnfoSnu8Nb
sQIDAQABoEkwRwYJKoZIhvcNAQkOMTowODApBgNVHREEIjAggglsb2NhbGhvc3SC
E2NlcnRpc3N1ZXIub25hcC5vcmcwCwYDVR0PBAQDAgWgMA0GCSqGSIb3DQEBCwUA
A4IBAQAWkOeJHnmtlSvlb7HbBeSGY4E9M338sKtwV4ZSvH+n5rgwamkvjhUwhycs
UR0XgeAyD86kK6kkvVewdIanHYp1k7CuDZkU6piy8t4RhosyqUWQNWtemGYdNZCL
cgZ1Jbj4NdIZo2EKBIEbTrm9VFt1zidYRFNGNJp8RQQds6r4qATq1NKr6ptrLuIc
dzfOm1ZPtSn8u4H4+z1re6q18JeM0VPXBiXBtEXwQRXIEnsjCzYxdjy+QwbEmlpB
o2hMIamWNIbskYnNkaky8eQzjJ8uIesESeanWJlrMUbzicOwQeYMPmj+Mkn1nqlK
YFwml5XnVXXpGLHGWCswpN3CDyXi
-----END CERTIFICATE REQUEST-----`)

const csrWithSkippedProperties = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDgjCCAmoCAQAwgaQxCzAJBgNVBAYTAlBMMRMwEQYDVQQIEwpEb2xueVNsYXNr
MRAwDgYDVQQHEwdXcm9jbGF3MREwDwYDVQQJEwhMb3RuaWN6YTEPMA0GA1UEERMG
MTItMzQ1MQ0wCwYDVQQKEwRPTkFQMQ0wCwYDVQQLEwRvbmFwMRwwGgYDVQQDExNj
ZXJ0aXNzdWVyLm9uYXAub3JnMQ4wDAYDVQQFEwUxMjM0NTCCASIwDQYJKoZIhvcN
AQEBBQADggEPADCCAQoCggEBAPdrWRYpdGY6A9YEQ8mnQdOW7wzdaNHJ83ZrMPZd
V7jBOMvQbTw6Oe/Q4vD+Dla7FmGqlAajNIgKRiUUQLKVmASELhCYhtW7Mn91qe6l
xuyPyOEi9o8mArJosFAfPPF0nm9FQPi2qHgyi6C52QR7cKsgNPflpKVsEx9Y+Zns
YBqkaX16BukvcHUANgsvZ3rLUVeiOsCi2ysVcsm+4XMvF6ejoqKJ9k7Ti0VrQtqh
e1nKlaa4uP3dreeUXBMLfKUS7QrNavpiX6wVaohVp6p/AYQ2HZurMv86Q2E5D5SC
ReEpVuWx+r4MI8dAHbYe09ntkRGIe8mVyxHHEWLNfZiwKGsCAwEAAaCBlzCBlAYJ
KoZIhvcNAQkOMYGGMIGDMFUGA1UdEQROMEyCCWxvY2FsaG9zdIITY2VydGlzc3Vl
ci5vbmFwLm9yZ4ENb25hcEBvbmFwLm9yZ4cEfwAAAYYVb25hcDovL2NsdXN0ZXIu
bG9jYWwvMAsGA1UdDwQEAwICBDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUH
AwIwDQYJKoZIhvcNAQELBQADggEBAHDMw3+fVOrbVnMI2g/IP40vt1eenkoriTHX
dnjRRFio75nCNRJdLOJ9FU3wIgdDZwGaiXdn5NDQxCe0BWcbElDJSYR/xOi7V0AM
2L3CrRAOhr2MjwX7CaOuYWcVtrbtIMf26NLKRXYPlGgc6YeofalDnezMJ/IuRQhj
bcm17a8owa5dH9u/rmTmlrIT7PV4JHkZIogctIcSqod6xdr1mbi8G9DMFAqV+o7W
9kV7XDKhTqYoBIsXwfehNMu3lo72VuklIyVNiEVz4mVzpeZy2DgjRjCLt106yDHZ
f3nco6O4y2EyexBVKq6QRFfZDUab6YcoEVvPAio01RmFrHgnxHs=
-----END CERTIFICATE REQUEST-----`)
