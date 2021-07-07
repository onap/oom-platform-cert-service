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

package org.onap.oom.certservice.certification.conversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;
import org.onap.oom.certservice.certification.X509CertificateParser;
import org.onap.oom.certservice.certification.conversion.OldCertificateModelFactory;
import org.onap.oom.certservice.certification.conversion.PemStringToCertificateConverter;
import org.onap.oom.certservice.certification.conversion.StringBase64;
import org.onap.oom.certservice.certification.exception.CertificateDecryptionException;
import org.onap.oom.certservice.certification.model.OldCertificateModel;

class OldCertificateModelFactoryTest {

    private static final String ENCODED_CERTIFICATE_STRING =
        "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVpekNDQXZPZ0F3SUJBZ0lVR0VwMkdaNlk4"
            + "bnpEQTlDS2w1blVSSTdDVU44d0RRWUpLb1pJaHZjTkFRRUwKQlFBd1lURWpNQ0VHQ2dtU0pvbVQ4"
            + "aXhrQVFFTUUyTXRNR3BpWm5FNGNXRXhabTh3ZDJ0dGJua3hGVEFUQmdOVgpCQU1NREUxaGJtRm5a"
            + "VzFsYm5SRFFURWpNQ0VHQTFVRUNnd2FSVXBDUTBFZ1EyOXVkR0ZwYm1WeUlGRjFhV05yCmMzUmhj"
            + "blF3SGhjTk1qRXdOakk1TURZMU1ESTFXaGNOTWpNd05qSTVNRFkxTURJMFdqQjNNUkV3RHdZRFZR"
            + "UUQKREFodmJtRndMbTl5WnpFWk1CY0dBMVVFQ3d3UVRHbHVkWGd0Um05MWJtUmhkR2x2YmpFTk1B"
            + "c0dBMVVFQ2d3RQpUMDVCVURFV01CUUdBMVVFQnd3TlUyRnVMVVp5WVc1amFYTmpiekVUTUJFR0Ex"
            + "VUVDQXdLUTJGc2FXWnZjbTVwCllURUxNQWtHQTFVRUJoTUNWVk13Z2dFaU1BMEdDU3FHU0liM0RR"
            + "RUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRREIKenZieXJyRWlhb0JqOGttYTJRbUMrVkxtbXRXRld5"
            + "QUpnU3JZQTRreHV5cmpRQ1c0SnlGR3ZtemJZb1VGRkxPRgpoZnExOFZqVHMwY2JUeXNYOGNGU2Zr"
            + "VjJFS0dFUkJhWm5aUlFzbzZTSUpOR2EzeE1lNUZIalJFeTM0TnAwNElICmpTUTQyZUlCZ2NOaUlh"
            + "ZGE0amdFbklRUVlRSlNQUXRIa2ZPTUM2TyszUnBUL2VIdHZvNXVyUjE2TUZZMUs2c28KbldZaXRJ"
            + "NVRwRUtSb3phdjZ6cVUvb3RIZ241alJMcnlqMElaeTBpamxCZlNLVHhyRmZacjNLb01EdWRWRWZV"
            + "TQp0c3FFUjNwb2MxZ0ZrcW1DUkszOEJQTmlZN3Y1S0FUUkIrZldOK1A3NW13NDNkcng5ckltcCtK"
            + "dHBPVXNESzhyCklCZDhvNGFmTnZyL1dyeWdCQjhyQWdNQkFBR2pnYVF3Z2FFd0RBWURWUjBUQVFI"
            + "L0JBSXdBREFmQmdOVkhTTUUKR0RBV2dCUjlNMXFVblFNMENwSFV6MzRGNXNWVVVjSUR0akFZQmdO"
            + "VkhSRUVFVEFQZ2cxMFpYTjBMbTl1WVhBdQpiM0puTUNjR0ExVWRKUVFnTUI0R0NDc0dBUVVGQndN"
            + "Q0JnZ3JCZ0VGQlFjREJBWUlLd1lCQlFVSEF3RXdIUVlEClZSME9CQllFRkFmcWNVNnhHaThqemps"
            + "UnVLUUJ0cVJjWGkrdU1BNEdBMVVkRHdFQi93UUVBd0lGNERBTkJna3EKaGtpRzl3MEJBUXNGQUFP"
            + "Q0FZRUFBZHc3N3E3c2hLdFM4bERHY0ovWThkRWpqTlNsUnRVMTRFTUM1OWttS2VmdApSaTdkMG9D"
            + "UVh0ZFJDdXQzeW1pekxWcVFrbVg2U3JHc2hwV1VzTnpUZElUalE2SkIyS09haUlXSUY2ME5TbGVX"
            + "CjB2TG0zNkVtWTFFcksrektlN3R2R1daaFROVnpCWHRucStBTWZKYzQxdTJ1ZWxreDBMTmN6c1g5"
            + "YUNhakxIMXYKNHo0WHNVbm05cWlYcG5FbTYzMmVtdUp5ajZOdDBKV1Z1TlRKVFBSbnFWWmY2S0tS"
            + "ODN2OEp1VjBFZWZXZDVXVgpjRnNwTDBIM01LSlY3dWY3aGZsbG5JY1J0elhhNXBjdEJDYm9GU2hW"
            + "a1JOYUFNUHBKZjBEQkxReG03ZEFXajVBCmhHMXJ3bVRtek02TnB3R0hXL0kxU0ZNbXRRaUYwUEFD"
            + "ejFVbjZuRFcvUmYxaUhFb0dmOFlCTFAzMzJMSlNEdWcKUktuMGNNM1FUY3lVRXpDWnhTd0tKMm5n"
            + "QzllRzlDMmQzWWhCNlp4dGwrZ1VJYTNBd3dQYnFyN1lSOVFrRDJFbwpkNExxRUg5em55QmZpN2sy"
            + "WUN3UDZydGZTaTZDbHliWGU4ZUJjdU1FUzRUQVFmRks2RlZmNTh0R1FJeDA2STBPCjM0bmVtWndr"
            + "TG9PQnpaa2VwYVF2Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";

    private static final String ENCODED_CERTIFICATE_CHAIN_STRING =
        "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUVpekNDQXZPZ0F3SUJBZ0lVR0VwMkdaNlk4"
            + "bnpEQTlDS2w1blVSSTdDVU44d0RRWUpLb1pJaHZjTkFRRUwKQlFBd1lURWpNQ0VHQ2dtU0pvbVQ4"
            + "aXhrQVFFTUUyTXRNR3BpWm5FNGNXRXhabTh3ZDJ0dGJua3hGVEFUQmdOVgpCQU1NREUxaGJtRm5a"
            + "VzFsYm5SRFFURWpNQ0VHQTFVRUNnd2FSVXBDUTBFZ1EyOXVkR0ZwYm1WeUlGRjFhV05yCmMzUmhj"
            + "blF3SGhjTk1qRXdOakk1TURZMU1ESTFXaGNOTWpNd05qSTVNRFkxTURJMFdqQjNNUkV3RHdZRFZR"
            + "UUQKREFodmJtRndMbTl5WnpFWk1CY0dBMVVFQ3d3UVRHbHVkWGd0Um05MWJtUmhkR2x2YmpFTk1B"
            + "c0dBMVVFQ2d3RQpUMDVCVURFV01CUUdBMVVFQnd3TlUyRnVMVVp5WVc1amFYTmpiekVUTUJFR0Ex"
            + "VUVDQXdLUTJGc2FXWnZjbTVwCllURUxNQWtHQTFVRUJoTUNWVk13Z2dFaU1BMEdDU3FHU0liM0RR"
            + "RUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRREIKenZieXJyRWlhb0JqOGttYTJRbUMrVkxtbXRXRld5"
            + "QUpnU3JZQTRreHV5cmpRQ1c0SnlGR3ZtemJZb1VGRkxPRgpoZnExOFZqVHMwY2JUeXNYOGNGU2Zr"
            + "VjJFS0dFUkJhWm5aUlFzbzZTSUpOR2EzeE1lNUZIalJFeTM0TnAwNElICmpTUTQyZUlCZ2NOaUlh"
            + "ZGE0amdFbklRUVlRSlNQUXRIa2ZPTUM2TyszUnBUL2VIdHZvNXVyUjE2TUZZMUs2c28KbldZaXRJ"
            + "NVRwRUtSb3phdjZ6cVUvb3RIZ241alJMcnlqMElaeTBpamxCZlNLVHhyRmZacjNLb01EdWRWRWZV"
            + "TQp0c3FFUjNwb2MxZ0ZrcW1DUkszOEJQTmlZN3Y1S0FUUkIrZldOK1A3NW13NDNkcng5ckltcCtK"
            + "dHBPVXNESzhyCklCZDhvNGFmTnZyL1dyeWdCQjhyQWdNQkFBR2pnYVF3Z2FFd0RBWURWUjBUQVFI"
            + "L0JBSXdBREFmQmdOVkhTTUUKR0RBV2dCUjlNMXFVblFNMENwSFV6MzRGNXNWVVVjSUR0akFZQmdO"
            + "VkhSRUVFVEFQZ2cxMFpYTjBMbTl1WVhBdQpiM0puTUNjR0ExVWRKUVFnTUI0R0NDc0dBUVVGQndN"
            + "Q0JnZ3JCZ0VGQlFjREJBWUlLd1lCQlFVSEF3RXdIUVlEClZSME9CQllFRkFmcWNVNnhHaThqemps"
            + "UnVLUUJ0cVJjWGkrdU1BNEdBMVVkRHdFQi93UUVBd0lGNERBTkJna3EKaGtpRzl3MEJBUXNGQUFP"
            + "Q0FZRUFBZHc3N3E3c2hLdFM4bERHY0ovWThkRWpqTlNsUnRVMTRFTUM1OWttS2VmdApSaTdkMG9D"
            + "UVh0ZFJDdXQzeW1pekxWcVFrbVg2U3JHc2hwV1VzTnpUZElUalE2SkIyS09haUlXSUY2ME5TbGVX"
            + "CjB2TG0zNkVtWTFFcksrektlN3R2R1daaFROVnpCWHRucStBTWZKYzQxdTJ1ZWxreDBMTmN6c1g5"
            + "YUNhakxIMXYKNHo0WHNVbm05cWlYcG5FbTYzMmVtdUp5ajZOdDBKV1Z1TlRKVFBSbnFWWmY2S0tS"
            + "ODN2OEp1VjBFZWZXZDVXVgpjRnNwTDBIM01LSlY3dWY3aGZsbG5JY1J0elhhNXBjdEJDYm9GU2hW"
            + "a1JOYUFNUHBKZjBEQkxReG03ZEFXajVBCmhHMXJ3bVRtek02TnB3R0hXL0kxU0ZNbXRRaUYwUEFD"
            + "ejFVbjZuRFcvUmYxaUhFb0dmOFlCTFAzMzJMSlNEdWcKUktuMGNNM1FUY3lVRXpDWnhTd0tKMm5n"
            + "QzllRzlDMmQzWWhCNlp4dGwrZ1VJYTNBd3dQYnFyN1lSOVFrRDJFbwpkNExxRUg5em55QmZpN2sy"
            + "WUN3UDZydGZTaTZDbHliWGU4ZUJjdU1FUzRUQVFmRks2RlZmNTh0R1FJeDA2STBPCjM0bmVtWndr"
            + "TG9PQnpaa2VwYVF2Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0KLS0tLS1CRUdJTiBDRVJUSUZJ"
            + "Q0FURS0tLS0tCk1JSUVzekNDQXh1Z0F3SUJBZ0lVTk1lYzl0ZlJEU3FPQlJGaTFkWDd2RCsyVG9z"
            + "d0RRWUpLb1pJaHZjTkFRRUwKQlFBd1lURWpNQ0VHQ2dtU0pvbVQ4aXhrQVFFTUUyTXRNR3BpWm5F"
            + "NGNXRXhabTh3ZDJ0dGJua3hGVEFUQmdOVgpCQU1NREUxaGJtRm5aVzFsYm5SRFFURWpNQ0VHQTFV"
            + "RUNnd2FSVXBDUTBFZ1EyOXVkR0ZwYm1WeUlGRjFhV05yCmMzUmhjblF3SGhjTk1qRXdOakk1TURZ"
            + "ME9UQTFXaGNOTXpFd05qSTVNRFkwT1RBMFdqQmhNU013SVFZS0NaSW0KaVpQeUxHUUJBUXdUWXkw"
            + "d2FtSm1jVGh4WVRGbWJ6QjNhMjF1ZVRFVk1CTUdBMVVFQXd3TVRXRnVZV2RsYldWdQpkRU5CTVNN"
            + "d0lRWURWUVFLREJwRlNrSkRRU0JEYjI1MFlXbHVaWElnVVhWcFkydHpkR0Z5ZERDQ0FhSXdEUVlK"
            + "CktvWklodmNOQVFFQkJRQURnZ0dQQURDQ0FZb0NnZ0dCQUtvRjduSzQzUDBaRi9tWUVCbU5GWENw"
            + "MmZma2srcFoKS1hCdFRFY1UwZVZmQU5WMzRFNHZEcnFSc0k0U3RFdmt5NGxiWW1PUEx5SnVabnRk"
            + "c1dyUC9rbi9kUENHWEEzQQovUE44WmNrUENBQVlwa0JuOFNsMVdxaW1UeTA3TStPM0UxOER0TnBO"
            + "Sy9RNWVkWW0rRnFXU3BVWHY5NEMzbjZVCmVXKytWaWNlWWc0ZUFDMVBka01LZGV3VEdnWGYzK1lI"
            + "Q1dySW9JeWozcDNCOUp4RDdXVVUzTEZ3UkdHWnljVU0KYnRla2FUZXhodDZ1N2JMeld0Ylg1ZHhw"
            + "bTBuR1dZa05KVm1mN0pJOHErMGpZdUNDcXdjYXpTNHIyeko5K1BqVwoyclZ0TkdUdVE0RmQyMm1r"
            + "c1RMK0FRbktJaW9jNEJadVcwTkM0emNPZ2Ztc2dRdnBsR2s1ZGlmY0RZeHU5c3hkClFGampMKzJ1"
            + "UURaK0RMTWlkYXNHRGxEb3FSQXBnV0lnajFxZllOMVNNbXJFZkdKWjFud05WcTlmemswV2VHNEgK"
            + "WmVBYjlnU3BxSk9LNlp6d0F0TTV5NlFxOXo4RC84U2hjS2JiN01waExiSnVGVFRyL1lWaE1QRVZE"
            + "SHA5RGZMbApYbzhGbkJCRVplUE9FYmdPZk5XbzlhTkg4RlczcHc1Znd3SURBUUFCbzJNd1lUQVBC"
            + "Z05WSFJNQkFmOEVCVEFECkFRSC9NQjhHQTFVZEl3UVlNQmFBRkgweldwU2RBelFLa2RUUGZnWG14"
            + "VlJSd2dPMk1CMEdBMVVkRGdRV0JCUjkKTTFxVW5RTTBDcEhVejM0RjVzVlVVY0lEdGpBT0JnTlZI"
            + "UThCQWY4RUJBTUNBWVl3RFFZSktvWklodmNOQVFFTApCUUFEZ2dHQkFLZUFSdHBweExodThZbms5"
            + "RHJFWUFmZHVwb0pESWU0K0RHYlVyQ212LzlIcVozRWxSaHpYWWdqClJCSlgzSjR1eHdNcEZQZFdO"
            + "Qk9nMWdzV2FQYkdYSGg4K1U0Y29HLzVjVm5abmYzRG1PelliVW5pYXU4eTFnclAKaWNyNTZLdDcr"
            + "Q2tRMFIrWWxUdXBGM3M5WjlIZW5UaHA3YmQ5N3hZNXBFK2l3cG1hNjNmcHJhSFVBb01qQXZEYQpU"
            + "S3ZRUDRZS3pJY1ZuamtEdzlqUUxyNGNxYTdaeU1EUVlNTTd5YUJZNkx0bDYxbU9xQ2l4Q2JMWStL"
            + "MmlMV0lNCnRoT2grV3R0cXZRejFPc2x4VkV1cWZtak4zMHBhUy9xSWY4MjhLZHp4UEN6S0RnTVVP"
            + "ZjdvTEZUa0JCcGVoWVkKYVd5SUFNd2hEa1grRXYzRXg4ZnRzRmJSUUdWOWMvcDJPUXd2TEdTOXdB"
            + "cVUyM1pEckhaTTFla0J5RE5FZUVpUgpTSGRWOEtNSTJuQ0J0NlhUeFhTejkwME5EV0l4aXJET0Rp"
            + "WmU5SGJJUm5hWlIzMEEyVCtTeElxYUpsNXZVQzYyCnlLbjNicnFUM1UyMlNlbmVpZ2w3bW81bWhL"
            + "bE8wdHErc2lJK1Y0T3lORkhadnJHQUNaUTNxYUFUZlozYlN3RVcKYkg1QjRlbHRodz09Ci0tLS0t"
            + "RU5EIENFUlRJRklDQVRFLS0tLS0K";

    private static final String ENCODED_PRIVATE_KEY =
        "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQk"
            + "tnd2dnU2tBZ0VBQW9JQkFRRFNOS0pCUU5XRmF0ajUKUmRhTmQyMGdnTnBOVUVaYzgxU1Y1d0hLeH"
            + "EwUy9rT0llTG8rekI5c3lkUUJLWm9JRXJPL3JIekwzb0VCNW5YNQp4NVBkeTViYUdYTzRQZDVCUS"
            + "trSXRHckFnNzVrRldmT3VHNU9GWUxpWldMUEcra3FBRXJKNTdzQlB5TzNKNjVxCkEzN0gxNnBiZV"
            + "hRL2VzR2RNaVZsenM4dE9UOEtaT1lDQUpGQXg1ZEk4OEowaEt4ZThONFdrZEJLSStXVndKMUsKR0"
            + "5pZmgwc2Jjam9rOG1Gbll4Nzcwd3BuZU5nbkZUbU9MWXFIUTRuSklUODdza1BYSUt1RHNMRUJDL0"
            + "1kVGQ0QwpPTTZJekZYZmQxNzNDc1p5UnR3V0F2ZXNDcWJGYkVhcDBiWHlCRFN1R0w3NlhGdzRTdj"
            + "ZiZkoyano2SjIyRU9SCmVPOGpVdWhKQWdNQkFBRUNnZ0VBUWJyZHBjUHRRSnZwbndEY2x6M3A3TW"
            + "o5K2tFSXo1WHpORENaR2R4SVVIRWIKa3ZnVlhQK2RML3BvaGJpSmhzNjZVRXhTZGJsczQ3ZzUyZE"
            + "l6aFo1YzNIUXJBRWl3VC80NVIxU0xNUW5CSmpDZgpWai9MbGpVWnlVdGt1MWlCNzNWSjdacTltaV"
            + "V4T050NnFZSFFTaE5CSFB0OGcwRVNlK0lyV1l0eXN6UjhadllXCjlqWm9xb0pOTW5ySVkyNmdtdF"
            + "RCRURpTmVmaEhBMGVoVHkwYzNBQ1lDTUY3aWlNenplMWhkUjZvTDhuTEZscmQKVGJZRGdCUzBueE"
            + "pvRVpxQnZBZWViZFVBaXc1UCtqZ1NXcXhnUkhpWGk2Rk0xWXVnMGF5Mm9GNEl1alV0ek5kNwplbn"
            + "NqeTVTTGFGcVp5dy81bkdlWDJMTXYvbFovQUtWYlZ6NnNBa3RVdFFLQmdRRHBBT3BBUVorNWRheG"
            + "xyQk5oClFoYy9ndnRPekJpRTA0YU5EdDVLMllEVU80dHdFRmYxTVdXSkNrV3Z1czNOSUphdkJ1K2"
            + "5GYzdEREphUEFxbk4KZnQrUGw3NTJ4UUlJRk1GdUt5QTdKL1hSZzFjVUIzNEFrZWtZeTZvRlYwa2"
            + "FlWmZvYXBRbGdDWnFWVkd5L2FCdAprSHBndDJnckpZZG82OE11bFQ0ZWplbGE4d0tCZ1FEbTg3UW"
            + "E4YzFYRTNuTGFQcGJIeTU5N0N5S0ZKTzBRdC9tCm1RT1FNaEJCOTJGU0JpRE05ZHFkbUU2d3JVU2"
            + "NFYVo3aDlaZ1kwQUdxVVFobzE3d3oyL1BxaGhaUFRiOU0rVTgKWUVaWTdnWnNoYkJ1MDgvTkJLTD"
            + "NGTitGd216VG8xN1d1SlNyQWFWV3dra1RMOWVSbkI2cUFTeHBMaDFKQ0J4cQpQSE9Kd1FmRzB3S0"
            + "JnUUNSTHlUSGpSeDliemxRMFB2eWFrQWFMdjl3aGZQeEwreHpFSVNxbHdTVE9kY1VxTnBsCnliVy"
            + "t3a3ZSeDlCY3RLV3Z3ZDZxZWdndnVUUkhRQjJXRWl3elNSWkE0MWowdUJvZkQzZ3g1Q0Jqd0RjT0"
            + "grei8KWmV1Y3E2cnhVUVlZSFJQdW1ocGRrNUJjU1hWeTFsNlVacVlhaGEyKzFNK2ZMT2lkcWhqZT"
            + "ZRWXl5UUtCZ1FDbwpTclhYWEpRUSs3UW9zVnFkdzk4UkMyUjVTZjFId2VOK0djb3E3UkJEd1l3OV"
            + "JSSHB5TTJCUVZjMkQweUxuYUQvCkswRGdBL0xINTlncDJ1NTM4L0M2Rm15ZnVxZXpZbm1Nd1dzQn"
            + "FwRXJ5MCtCc3Y4ZG1sOVdSUE9NZU56c2E0UFUKVzdTWjJCMHZWMndBZTBCT2JzRTVpSmxnRzZaam"
            + "JYR25TRjI0NTl4TzJRS0JnRER1cXJBcThQMXpXU28wcWZ0QgpkTS9Xc3p6U3VZRHdjemhvajNKek"
            + "5VK3lvQ3g4ejNzY0NML240eXFUT1RiWVhLTXFHbUVrSW01eXJ1SWlJeHBRCmNJM1pDUlVZbHZDY0"
            + "FaeCtiVU1QSXNkek1TeGJMaHNqSU5Oc3F4dDJlMlQvd2dJWXpWenVERExpZ1drN1lDZkEKNDJ4YX"
            + "VldHQ0M21qM25wYUFvcURIVG92Ci0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K";


    private static final String SUBJECT = "CN=onap.org,OU=Linux-Foundation,O=ONAP,L=San-Francisco,ST=California,C=US";
    private static final GeneralName GENERAL_NAME = new GeneralName(GeneralName.dNSName, "test.onap.org");

    private final OldCertificateModelFactory factory =
        new OldCertificateModelFactory(new PemStringToCertificateConverter(), new X509CertificateParser());

    @Test
    void shouldCorrectlyParseX509CertificateFromCertificate()
        throws CertificateDecryptionException {
        //given
        StringBase64 base64EncodedCertificate = new StringBase64(ENCODED_CERTIFICATE_STRING);
        //when
        final OldCertificateModel certificateModel = factory.createCertificateModel(base64EncodedCertificate, ENCODED_PRIVATE_KEY);
        //then
        assertThat(certificateModel.getOldCertificate()).isNotNull();
        assertThat(certificateModel.getSubjectData()).isEqualTo(new X500Name(SUBJECT));
        assertThat(certificateModel.getSans()).containsExactly(GENERAL_NAME);
        assertThat(certificateModel.getOldPrivateKey()).isNotNull();
    }

    @Test
    void shouldCorrectlyParseX509CertificateFromCertificateChain()
        throws CertificateDecryptionException {
        //given
        StringBase64 base64EncodedCertificate = new StringBase64(ENCODED_CERTIFICATE_CHAIN_STRING);
        //when
        final OldCertificateModel certificateModel = factory.createCertificateModel(base64EncodedCertificate, ENCODED_PRIVATE_KEY);
        //then
        assertThat(certificateModel.getOldCertificate()).isNotNull();
        assertThat(certificateModel.getSubjectData()).isEqualTo(new X500Name(SUBJECT));
        assertThat(certificateModel.getSans()).containsExactly(GENERAL_NAME);
        assertThat(certificateModel.getOldPrivateKey()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenCertificateChainHasNoCertificates() {
        //given
        StringBase64 base64EncodedCertificate = new StringBase64("");
        //when, then
        assertThatThrownBy(() -> factory.createCertificateModel(base64EncodedCertificate, ENCODED_PRIVATE_KEY))
            .isInstanceOf(CertificateDecryptionException.class);
    }
}
