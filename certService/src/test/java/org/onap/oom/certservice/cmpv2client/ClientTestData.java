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

package org.onap.oom.certservice.cmpv2client;

import org.onap.oom.certservice.certification.model.CertificateUpdateModel;
import org.onap.oom.certservice.certification.model.CertificateUpdateModel.CertificateUpdateModelBuilder;

public final class ClientTestData {

    static final String KUR_CORRECT_SERVER_RESPONSE_ENCODED = "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";
    static final String CR_CORRECT_SERVER_RESPONSE_ENCODED = "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";

    private static final String TEST_CA = "TestCA";
    private static final String WRONG_OLD_CERT = "wrong old cert";
    private static final String WRONG_OLD_PRIVATE_KEY = "wrong old private key";

    private static final String TEST_ENCODED_CSR = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQzV6Q0NBYzhDQVFBd2R6RUxNQWtHQTFVRUJoTUNWVk14RXpBUkJnTlZCQWdNQ2tOaGJHbG1iM0p1YVdFeApGakFVQmdOVkJBY01EVk5oYmkxR2NtRnVZMmx6WTI4eERUQUxCZ05WQkFzTUJFOU9RVkF4R1RBWEJnTlZCQW9NCkVFeHBiblY0TFVadmRXNWtZWFJwYjI0eEVUQVBCZ05WQkFNTUNHOXVZWEF1YjNKbk1JSUJJakFOQmdrcWhraUcKOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXdVZmxSZ3k4d2tRajFib1hucGY5Q2lmakN6Y1lWSnJnTlZnVQplVnlOWjltczZIYVBuRDQ3M1M0b09kN3JXTnY3WEloWk9FU3lUM1FsVlB6eFBBNjdYMFR3dERqc3JUUjBxZGhtClBQS1crdHozM1pCRGttVnhLa0hmVmsrVUVmbi9rOHE3L0RGRmExWnV3NFdzT3R0ZDV0MHNIWU01eDFBY0dKVGgKdW9hcUh1WHpXK3BhcHlqYmpZbkJjUHB2bEsxbTdtRmVIWXNtMktBNk9yRHdxaE1wSGVSQkNldjNwMWlhSUdvUQpTTVAwTmhLMVFmaDFjazZ5Zmhid1lRUGZJaklMTWx6Z2J4QkszOXI0M2xSU0NsalkzdCtHSllPM1NKQURtS0YvCkFxMjJTbHg4ZWdCSDFmdHpzWGdOTnl3Y25tNUpGOFZXd0ZTamsydndPeDJjVEpIeDZRSURBUUFCb0Nzd0tRWUoKS29aSWh2Y05BUWtPTVJ3d0dqQVlCZ05WSFJFRUVUQVBnZzEwWlhOMExtOXVZWEF1YjNKbk1BMEdDU3FHU0liMwpEUUVCQ3dVQUE0SUJBUUFSVGo4T2FJUnM4WVI1QmFrRDhFYTRPOEdZZUJmd3pCdWRoU0x4UVIvM01xWHpQMGYzCkpHRVN6TCtVeWduQjE4dG9GdG9qdk5sR25TYVFOcnI4K2lwQkpiLzRVUTdydFJwNThaa0p1Nk9lZGNwSGd1emIKRUw1NnBGNlBnRk5tcFlGbnZ4MDc1UzhxZ2w0eWtzK09DK0hSK1dwaVZuOGQyMlEraVMwL3NZb0VRWkRRTVUvaQplWDRMVDlqSjNWM2lKTFh6OUZmRlhzY1VxeDZ6RGt5VUZJQms0aUZHWE9RLzQ1MmRla0ZPaGlKQ0x1VlRHdTVpCk5NODdZRVptWnVLbXNtd2x4WDU2UjRrd0Z6azZLcWFyMlhNTU5MV0U3VUZ4SDROUlFIeUUxOEVTNU5adDMwMnAKYzl4YkRyekEvbVNSdDVlTTZTYlVqNStlU0tyUDQxdlk4S3hPCi0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=";
    private static final String TEST_ENCODED_PRIVATE_KEY = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktnd2dnU2tBZ0VBQW9JQkFRRFNOS0pCUU5XRmF0ajUKUmRhTmQyMGdnTnBOVUVaYzgxU1Y1d0hLeHEwUy9rT0llTG8rekI5c3lkUUJLWm9JRXJPL3JIekwzb0VCNW5YNQp4NVBkeTViYUdYTzRQZDVCUStrSXRHckFnNzVrRldmT3VHNU9GWUxpWldMUEcra3FBRXJKNTdzQlB5TzNKNjVxCkEzN0gxNnBiZVhRL2VzR2RNaVZsenM4dE9UOEtaT1lDQUpGQXg1ZEk4OEowaEt4ZThONFdrZEJLSStXVndKMUsKR05pZmgwc2Jjam9rOG1Gbll4Nzcwd3BuZU5nbkZUbU9MWXFIUTRuSklUODdza1BYSUt1RHNMRUJDL01kVGQ0QwpPTTZJekZYZmQxNzNDc1p5UnR3V0F2ZXNDcWJGYkVhcDBiWHlCRFN1R0w3NlhGdzRTdjZiZkoyano2SjIyRU9SCmVPOGpVdWhKQWdNQkFBRUNnZ0VBUWJyZHBjUHRRSnZwbndEY2x6M3A3TWo5K2tFSXo1WHpORENaR2R4SVVIRWIKa3ZnVlhQK2RML3BvaGJpSmhzNjZVRXhTZGJsczQ3ZzUyZEl6aFo1YzNIUXJBRWl3VC80NVIxU0xNUW5CSmpDZgpWai9MbGpVWnlVdGt1MWlCNzNWSjdacTltaVV4T050NnFZSFFTaE5CSFB0OGcwRVNlK0lyV1l0eXN6UjhadllXCjlqWm9xb0pOTW5ySVkyNmdtdFRCRURpTmVmaEhBMGVoVHkwYzNBQ1lDTUY3aWlNenplMWhkUjZvTDhuTEZscmQKVGJZRGdCUzBueEpvRVpxQnZBZWViZFVBaXc1UCtqZ1NXcXhnUkhpWGk2Rk0xWXVnMGF5Mm9GNEl1alV0ek5kNwplbnNqeTVTTGFGcVp5dy81bkdlWDJMTXYvbFovQUtWYlZ6NnNBa3RVdFFLQmdRRHBBT3BBUVorNWRheGxyQk5oClFoYy9ndnRPekJpRTA0YU5EdDVLMllEVU80dHdFRmYxTVdXSkNrV3Z1czNOSUphdkJ1K25GYzdEREphUEFxbk4KZnQrUGw3NTJ4UUlJRk1GdUt5QTdKL1hSZzFjVUIzNEFrZWtZeTZvRlYwa2FlWmZvYXBRbGdDWnFWVkd5L2FCdAprSHBndDJnckpZZG82OE11bFQ0ZWplbGE4d0tCZ1FEbTg3UWE4YzFYRTNuTGFQcGJIeTU5N0N5S0ZKTzBRdC9tCm1RT1FNaEJCOTJGU0JpRE05ZHFkbUU2d3JVU2NFYVo3aDlaZ1kwQUdxVVFobzE3d3oyL1BxaGhaUFRiOU0rVTgKWUVaWTdnWnNoYkJ1MDgvTkJLTDNGTitGd216VG8xN1d1SlNyQWFWV3dra1RMOWVSbkI2cUFTeHBMaDFKQ0J4cQpQSE9Kd1FmRzB3S0JnUUNSTHlUSGpSeDliemxRMFB2eWFrQWFMdjl3aGZQeEwreHpFSVNxbHdTVE9kY1VxTnBsCnliVyt3a3ZSeDlCY3RLV3Z3ZDZxZWdndnVUUkhRQjJXRWl3elNSWkE0MWowdUJvZkQzZ3g1Q0Jqd0RjT0grei8KWmV1Y3E2cnhVUVlZSFJQdW1ocGRrNUJjU1hWeTFsNlVacVlhaGEyKzFNK2ZMT2lkcWhqZTZRWXl5UUtCZ1FDbwpTclhYWEpRUSs3UW9zVnFkdzk4UkMyUjVTZjFId2VOK0djb3E3UkJEd1l3OVJSSHB5TTJCUVZjMkQweUxuYUQvCkswRGdBL0xINTlncDJ1NTM4L0M2Rm15ZnVxZXpZbm1Nd1dzQnFwRXJ5MCtCc3Y4ZG1sOVdSUE9NZU56c2E0UFUKVzdTWjJCMHZWMndBZTBCT2JzRTVpSmxnRzZaamJYR25TRjI0NTl4TzJRS0JnRER1cXJBcThQMXpXU28wcWZ0QgpkTS9Xc3p6U3VZRHdjemhvajNKek5VK3lvQ3g4ejNzY0NML240eXFUT1RiWVhLTXFHbUVrSW01eXJ1SWlJeHBRCmNJM1pDUlVZbHZDY0FaeCtiVU1QSXNkek1TeGJMaHNqSU5Oc3F4dDJlMlQvd2dJWXpWenVERExpZ1drN1lDZkEKNDJ4YXVldHQ0M21qM25wYUFvcURIVG92Ci0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K";
    private static final String TEST_ENCODED_OLD_PRIVATE_KEY = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2d0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktrd2dnU2xBZ0VBQW9JQkFRRHVTZ0FtVWovWEo5NlQKOGkzREZxWE1QOUpaa3RkeEtYZUV2NDZhcGFVTmJyNGNoSi9EM2pLdTFBRFdWVEx4YlNyOEpuT0dDSUFzenZjaQozQmxLb1VUaHVvaUl0NXBnKy9kaFc2SDRnazdtdTRVdzAxdEFVQjZqM2ZubVhJTDZaMTlmS2F0bXlRTmVMSzRQCnlNNTFMTG03ZjgyNjZsR0NrektmdWx0NVAvc21Ya0lxL0FGN1d6Q3gzclNXOW0yZkdKaEFGcjAyL09pV1JvOWIKd0VHSjFkUnJhaTc5ZGtpMEN4eFlDMXYvb3FXVTJLTmpSZDF5RXNXb0ZxTC9pd1NoWCtwazl5cmtSMzQ0aEZLZwpRVFl1bGg3Z0NKQmwyOFhGM2kraDNiVHZJL3VMRk5PLzdvZ29PTjlDM04wdTRiTzhlNUNnUERHZWI5eG9VQklrClVnM0hOSkczQWdNQkFBRUNnZ0VCQU1qOTFEaCtvZWlxY1h5YkJ1eUtPdGtZY0NZcnpOdGZuYmQwR0NYcldGZ0gKTkFZNys4S3J0bFp1N2pIYmRYZmNuQ2hKaXFIZ283U244aDhPUmFzRWNtUndBV0JJZGNnZVgrQlgrVHZ6TmZnNgo3YkpzWklqUHk3aHVzSzRWRkVtQVRocW52REtibE9Lbmp6NHpJNm9FU3JtVHFJVmp4ZEw4cy9PMHJobU0xUnZiClB0QXRCeDNMTEtBUFZWTlN6a05JeHFIY1cwaFJ1bFlqOU81S3hmZDJJdDlrKzVzY0RpQU5qcU9YTVRIMmxrdzcKcE41ZFRtNU1EQXcrMGpaUVNpUFRia0hFQWdPZElxNmxuYStSN1lBRkFNc0ZVKzlmbnNEZVR2M2VHT2h6ZVhCSApJRlRkTStIOVU3Tm8xOW91d0NHU05JTEJESnE1V2JVd3VLQWRTUDBVaGZFQ2dZRUEvNElFa1g2djJVSUpqbldwCjBnbzBTSFJFamQ1Vmk0T2dnQW00L0hDbWx6Q3ljZzljRUhXeXFsd3RNN24xVkw5WDdJaytxMFc2UjBYRXlJbHMKTWlPSEF0cFhxbDB0RnRNQVd5SnBLY05vaktyRjlMR3g2VDVKQk5HTFo1b2JCaXRLNTZtTVZta0RTTzljc3hRYgpURXhQY1JGZmpQdkpJTlBRNDRZYWNRR0padThDZ1lFQTdyOStJN2svdzg2YVNWWkJObTh4NURrZVd4anRBSmRnCndFeXpEWklxYXEwU3IvQ0dxZnRSTFFrNnF4RDQxVWh2d1QrVHVMd2xuWWJMWmRzQmJVVW11cHNBZWpMbnY0RGIKR1RiTHpiNEM1c2FONW13dEJPeU5vaGNoUUVHVUF0RzdzN3BCYStsOE9MSmVzS1FsMWJBc0JXRC9leG01bzZyYwo0T0NYQkNpaHdia0NnWUFjU2ZEbml2YzlQcXFBTTFiU0FuODNabWdRclFVYnBUOG43ZXVsUjNPcVdhSG9MdnNxCmQxMklyeHZ5Rml5cmJXUDJ0RnRUNnl4c3A3VFozeDB6ait0cXpYSFhVdW1qRlVsOHpacUhIVE4rSDRvN1JWRkYKV2JnTDZJZGV1UmswM2FZMWIvZ3h1UDY4SElSTzczTDJSNXlrRUNCY0k2UnBGZ3FTcGs1WEpLeHAwUUtCZ1FEWAp5VmhYTFg1R21odTFJVEs3NG5Dem1EU3BuYlBJandteGhTRm9xSzJSMFhCTWVSY2QxN3FjKy9SODNWQXFaZGdzClVDeFNFaXZsWHduRHU5aGtUTlllWHk1bFJGRldNejdVWVVSL1pyZjBvWTFyc0daWVJ2NFVmTmRlM21iS3pZbmIKZmdMWGFDY1FqNWNxREpMdHV0ZHUzU2JNdW9taE5qT0JSVHo1VTBnd2NRS0JnUURyRy9wZDN6aUw1dFZFSk1KOApUQmdodko1NTZNMjFXaXFRNG1WUGcwbGNub1RXVmdCV0hqS2k2MzNhZVVRRlYrQTN2d3ljS1h4YVdsUzBYWmZVCmRobTJaUnVwYzhaeVA2ZGNaR3VLTWxHR21kSGtGaExIcUNhZXViS25ZUEkyVTFrZklNVGlWR0Jubmk2Y3dkRGIKUzczSG12YVpwU0xsbDlhbXkvZEx1N0QxdGc9PQotLS0tLUVORCBQUklWQVRFIEtFWS0tLS0tCg==";
    private static final String TEST_ENCODED_OLD_CERT = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVmVENDQXVXZ0F3SUJBZ0lVRncwd1VZc2wxUkF2bHB1bXpqcFRrNjZzNGlzd0RRWUpLb1pJaHZjTkFRRUwKQlFBd1V6RVZNQk1HQ2dtU0pvbVQ4aXhrQVFFTUJURXlNelExTVJVd0V3WURWUVFEREF4TllXNWhaMlZ0Wlc1MApRMEV4SXpBaEJnTlZCQW9NR2tWS1FrTkJJRU52Ym5SaGFXNWxjaUJSZFdsamEzTjBZWEowTUI0WERUSXhNRGN3Ck1qQTNNVE0xTlZvWERUSXpNRGN3TWpBM01EWTFNMW93ZHpFUk1BOEdBMVVFQXd3SWIyNWhjQzV2Y21jeEdUQVgKQmdOVkJBc01FRXhwYm5WNExVWnZkVzVrWVhScGIyNHhEVEFMQmdOVkJBb01CRTlPUVZBeEZqQVVCZ05WQkFjTQpEVk5oYmkxR2NtRnVZMmx6WTI4eEV6QVJCZ05WQkFnTUNrTmhiR2xtYjNKdWFXRXhDekFKQmdOVkJBWVRBbFZUCk1JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBN2tvQUpsSS8xeWZlay9JdHd4YWwKekQvU1daTFhjU2wzaEwrT21xV2xEVzYrSElTZnc5NHlydFFBMWxVeThXMHEvQ1p6aGdpQUxNNzNJdHdaU3FGRQo0YnFJaUxlYVlQdjNZVnVoK0lKTzVydUZNTk5iUUZBZW85MzU1bHlDK21kZlh5bXJac2tEWGl5dUQ4ak9kU3k1CnUzL051dXBSZ3BNeW43cGJlVC83Smw1Q0t2d0JlMXN3c2Q2MGx2WnRueGlZUUJhOU52em9sa2FQVzhCQmlkWFUKYTJvdS9YWkl0QXNjV0F0Yi82S2xsTmlqWTBYZGNoTEZxQmFpLzRzRW9WL3FaUGNxNUVkK09JUlNvRUUyTHBZZQo0QWlRWmR2RnhkNHZvZDIwN3lQN2l4VFR2KzZJS0RqZlF0emRMdUd6dkh1UW9Ed3hubS9jYUZBU0pGSU54elNSCnR3SURBUUFCbzRHa01JR2hNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVzWW1NQytnWTE4WWMKdDVMejJheDlTRjhzaHlNd0dBWURWUjBSQkJFd0Q0SU5kR1Z6ZEM1dmJtRndMbTl5WnpBbkJnTlZIU1VFSURBZQpCZ2dyQmdFRkJRY0RBZ1lJS3dZQkJRVUhBd1FHQ0NzR0FRVUZCd01CTUIwR0ExVWREZ1FXQkJTdDVzYTFCc1VNCjJrTHFpdXNGWkxCWGlFMStJREFPQmdOVkhROEJBZjhFQkFNQ0JlQXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnR0IKQUkrYW94LzJPUFdQZC9IanB0b1VhVlRhNkFnZldMMHoyR3NkOS9oVDBPVTdGSWloTEwrUGx4Y3Z2VWVQWGZlRQpHaEtJb3FJb3pERndMeWs1OUVWNVMxSEdsYVI3QnlUZkVJcVl5T0I5YkNPMmlPWjdIcW9vNmxoN2hoUTZXUENRCmtpY3VRMXJRWDJFSHlOVW05aFovU2dWamFYQlpZY0l0cFNsN1lWSVFGUElXY2VYYmtFaG8rSG5HczExTDI0V0QKUWhCNkpWWWJzME9JQzVaNDNablBKaHdHYlVyOCs5Q2IwR0J1dzNITUVMN05mNmFQSWVjOG8ydXphVHU3WXlOegpjYlN4WmMyUzRpeVpSbjdkTldQSmtFUVFGd1dPNlBOYzRwd0xSeC9zR3pHdlY0cFZGdlRuOGE3c0FiVXpwcnZBCmRDNFdjYnJhNE1wTFFKczZqNWJoUFJ1QlRsSXBnZEVhdkVSM1J5bUVGUTBSRHdER2thSndNbkdVVTlqSWdFN3AKR282RG5aV2RJZEFoRlN2Q3RybERLL1NGYmVKM3RTNlMrSUxXUDgydWU3Q0UwSnYrUEVoUnc5aWVreE5hRElNbwpzeXcvM2tnSUNnckV3RjJzUHY5UnVLQ212V3NrMkdKaHRRcXZSK3FFRE5FQW15aXJodFh6L214QUZ0dy9ualpVCjBBPT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQoK";


    public static final CertificateUpdateModel TEST_CERTIFICATE_UPDATE_MODEL_WITH_WRONG_OLD_CERT =
        new CertificateUpdateModelBuilder()
        .setEncodedCsr(TEST_ENCODED_CSR)
        .setEncodedPrivateKey(TEST_ENCODED_PRIVATE_KEY)
        .setEncodedOldCert(WRONG_OLD_CERT)
        .setEncodedOldPrivateKey(TEST_ENCODED_OLD_PRIVATE_KEY)
        .setCaName(TEST_CA)
        .build();

    public static final CertificateUpdateModel TEST_CERTIFICATE_UPDATE_MODEL_WITH_WRONG_PRIVATE_KEY =
        new CertificateUpdateModelBuilder()
        .setEncodedCsr(TEST_ENCODED_CSR)
        .setEncodedPrivateKey(TEST_ENCODED_PRIVATE_KEY)
        .setEncodedOldCert(TEST_ENCODED_OLD_CERT)
        .setEncodedOldPrivateKey(WRONG_OLD_PRIVATE_KEY)
        .setCaName(TEST_CA)
        .build();

    static final CertificateUpdateModel TEST_CERTIFICATE_UPDATE_MODEL = new CertificateUpdateModelBuilder()
        .setEncodedCsr(TEST_ENCODED_CSR)
        .setEncodedPrivateKey(TEST_ENCODED_PRIVATE_KEY)
        .setEncodedOldCert(TEST_ENCODED_OLD_CERT)
        .setEncodedOldPrivateKey(TEST_ENCODED_OLD_PRIVATE_KEY)
        .setCaName(TEST_CA)
        .build();

    private ClientTestData() {
    }

}
