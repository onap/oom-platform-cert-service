/*============LICENSE_START=======================================================
 * oom-truststore-merger
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

package org.onap.oom.truststoremerger.configuration.path.validation;


import static org.assertj.core.api.Assertions.assertThat;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidCertificatesPaths;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidPasswordPaths;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationFunctionsTest {

    @Test
    void shouldValidateWithSuccessCorrectCertificatesPaths() {
        // given
        List<String> certPaths = Arrays.asList("/opt/app/certificates/truststore.p12");
        // when
        boolean result = doesItContainValidCertificatesPaths().test(certPaths);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateWithFailureCertificatesPathsWithOneEmptyPath() {
        // given
        List<String> certPaths = Arrays.asList("/opt/app/certificates/truststore.p12", "");
        // when
        boolean result = doesItContainValidCertificatesPaths().test(certPaths);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldValidateWithFailureCertificatesPathsWithOnePathWhichHasIncorrectExtension() {
        // given
        List<String> certPaths = Arrays.asList("/opt/app/certificates/truststore.txt", "/opt/cert.p12");
        // when
        boolean result = doesItContainValidCertificatesPaths().test(certPaths);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldValidateWithSuccessCertificatesPasswordPaths() {
        // given
        List<String> passwordPaths = Arrays.asList("/opt/app/certificates/truststore.pass", "");
        // when
        boolean result = doesItContainValidPasswordPaths().test(passwordPaths);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateWithSuccessCertificatePasswordsPathsWhichContainsEmptyPathsInTheMiddle() {
        // given
        List<String> passwordPaths = Arrays.asList("/opt/app/certificates/truststore.pass", "", "/etc/truststore.pass");
        // when
        boolean result = doesItContainValidPasswordPaths().test(passwordPaths);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateWithFailureCertificatesPasswordsPathsWithIncorrectExtension() {
        // given
        List<String> passwordPaths = Arrays.asList("/pass.txt");
        // when
        boolean result = doesItContainValidPasswordPaths().test(passwordPaths);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldValidateWithFailureCertificatesPasswordPathsWithMissingPrecedingSlash() {
        // given
        List<String> passwordPaths = Arrays.asList("jks.pass");
        // when
        boolean result = doesItContainValidPasswordPaths().test(passwordPaths);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldValidateWithSuccessSourcePathsToCopyFiles() {
        // given
        List<String> sourcePaths = Arrays.asList("/opt/dcae/cacert/external/keystore.p12",
            "/opt/dcae/cacert/external/keystore.pass");
        // when
        boolean result = ValidationFunctions.doesItContainValidPathsToCopy().test(sourcePaths);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateWithSuccessDestinationPathsToCopyFiles() {
        // given
        List<String> sourcePaths = Arrays.asList("/opt/dcae/cacert/cert.p12","/opt/dcae/cacert/p12.pass");
        // when
        boolean result = ValidationFunctions.doesItContainValidPathsToCopy().test(sourcePaths);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateWithFailureDestinationPathsWithIncorrectExtension() {
        // given
        List<String> sourcePaths = Arrays.asList("/opt/dcae/cacert/cert.txt","/opt/dcae/cacert/p12.other");
        // when
        boolean result = ValidationFunctions.doesItContainValidPathsToCopy().test(sourcePaths);
        // then
        assertThat(result).isFalse();
    }

}
