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
MIIDPTCCAiUCAQAwgYIxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh
MRYwFAYDVQQHEw1TYW4tRnJhbmNpc2NvMRkwFwYDVQQKExBMaW51eC1Gb3VuZGF0
aW9uMQ0wCwYDVQQLEwRPTkFQMRwwGgYDVQQDExNjZXJ0aXNzdWVyLm9uYXAub3Jn
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3RKJGx0bXUz2hyWPssq5
sZEOcC4ITkBWDx1b/g+abX1J2nTfKZK4JSRd1I/9Lz8LaiyuqacCzJ3vZ+IbvbwJ
HjTvzakbdQgbm7TSOH4zBXUp6lN92PT+RwM5PGcipi3vcC/eT4aMohEhHH1qkNCP
G7EC69hTnw3tyXX19euF/gAJwHeYNSHC6k2WwLYkYkkhRIndzv1vM8nBLb7C7JBE
aAO0fq1trVEoIYdz3tNWZWs+T+Vu8fATqm1rLzKOj3bQljRluFsegrRPR2oD9Th5
AE5nmw948higVTlXcdeoW0MiAn6pWyHzcTKhBizm+Yp4bejqx8oq+Joq7u5nue/p
KQIDAQABoHUwcwYJKoZIhvcNAQkOMWYwZDBVBgNVHREETjBMgglsb2NhbGhvc3SC
E2NlcnRpc3N1ZXIub25hcC5vcmeBDW9uYXBAb25hcC5vcmeHBH8AAAGGFW9uYXA6
Ly9jbHVzdGVyLmxvY2FsLzALBgNVHQ8EBAMCBaAwDQYJKoZIhvcNAQELBQADggEB
AI3LghPWW3P8zO5CiLIMYwbYbQt0nA0AA/iDzBh/HXE+owLXECffGhidC9oG8d1r
ZAL+fkjU1+hBSPyk8ZIeiPGi1NDL4h+65Cobv/D3O5PEDYui98FZSykrkcLCb3Qx
ga6ki+l3sQYP6sWyK6N1U7uX8t1g7IMbcpMO7rASNMDRWkYtlmJhit0Yd6YgvYuL
gqH+TRiUTvm6XKby5DaTK3lz7h78lqTxVcWHlxUGTvXAEF15cqbKGy9n/4y1LQUc
AhEFo/1MKDqMpG8FTw/EFMMzKQAXofeLGcWepXo1oDGHal8/3kYN+0c2cH4ZASp4
N8j9VQMXik3mDiJWFtM0oWI=
-----END CERTIFICATE REQUEST-----`)

const csrWithSkippedProperties = (`-----BEGIN CERTIFICATE REQUEST-----
MIIDaDCCAlACAQAwgboxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh
MRYwFAYDVQQHEw1TYW4tRnJhbmNpc2NvMRUwEwYDVQQJEwxzYW1wbGVTdHJlZXQx
DzANBgNVBBETBjEyLTM0NTEZMBcGA1UEChMQTGludXgtRm91bmRhdGlvbjENMAsG
A1UECxMET05BUDEcMBoGA1UEAxMTY2VydGlzc3Vlci5vbmFwLm9yZzEOMAwGA1UE
BRMFMTIzNDUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDOsn9B8AmR
ZGcJ/b2pp+oeA0jQgr7wnJeDCWzF7v5k6vBSeladlERVTCBaWO5SSSVDqrhidnvv
+FpxG6LeN+Y/KQn/Hk8pQSoht3yZaTgQV352nOysIr/tI4QUwahAXH8RwvyS4CHJ
vy1n/cJlvdiymC9z61dUx4DyHsMlZMRTldiFIs0/VKtgPPZ3hxMT1NJjg9dmyJhm
RCZOn/cj1laIW6ie/BQJuXINf8VT3bl4mYkfc7yvrj7V3aMWKuzbr8/yMo9fpHqd
gGQIqtmDOWOHkawkEAPqpeFLdryXcfFEFsy9iJHbr4N/5FKj4lbUaS4aVA04oigl
RUKM8bE5uQUHAgMBAAGgaDBmBgkqhkiG9w0BCQ4xWTBXMCkGA1UdEQQiMCCCCWxv
Y2FsaG9zdIITY2VydGlzc3Vlci5vbmFwLm9yZzALBgNVHQ8EBAMCAgQwHQYDVR0l
BBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4IBAQACQMf4
OcJHOD1AWmIXy5G0VYUs2qogrSX0BEWWuXRGt9vicUmesgf9YLHNlg5dOujJmEIP
HKkks1uy5dNXMlKAZ3i1TSaVhoFap5jZiXmfKfRXb5ImL3e5146hr+1dqRnPA7rR
4fjjz8B7HeO2TiWu2xgJnyuPHKOwalSOYhVljpEE2hjs064Vc2yovj2FcYdPbfc2
gFQqkUSBrviuzxhK63fWtMGStv/kSc3cEylDzV45LLIUCVFKzKaqXiG8MCQjhtFI
2ve5+9NJxPQ3SqBKUuRoTHYBC3YppY/QagepVlENeDomvku8iS2FSkkOxGspbhla
7uR7K5JP5H9jLQbj
-----END CERTIFICATE REQUEST-----`)
