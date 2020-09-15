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

package org.onap.oom.truststoremerger.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.KEYSTORE_DESTINATION_PATHS;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.KEYSTORE_SOURCE_PATHS;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.TRUSTSTORES_PASSWORDS_PATHS;
import static org.onap.oom.truststoremerger.configuration.model.EnvVariable.TRUSTSTORES_PATHS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.configuration.exception.CertificatesPathsValidationException;
import org.onap.oom.truststoremerger.configuration.exception.ConfigurationException;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.configuration.path.DelimitedPathsSplitter;
import org.onap.oom.truststoremerger.configuration.path.env.EnvReader;

@ExtendWith(MockitoExtension.class)
class AppConfigurationProviderTest {

    private static final String BASE_TRUSTSTORE_PATH = "/opt/app/truststore_";
    private static final String JKS_EXTENSION = ".jks";
    private static final String PASS_EXTENSION = ".pass";
    private static final String SAMPLE_TRUSTSTORES_PATHS = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String SAMPLE_TRUSTSTORES_PASSWORDS_PATHS = "/opt/app/certificates/truststore.pass:/trust.pass";

    @Mock
    private DelimitedPathsSplitter pathsSplitter;
    @Mock
    private EnvReader envReader;
    private AppConfigurationProvider provider;

    @BeforeEach
    void setUp() {
        provider = new AppConfigurationProvider(pathsSplitter, envReader);
    }

    @Test
    void shouldThrowExceptionWhenMandatoryEnvNotPresent() {
        // given
        when(envReader.getEnv(TRUSTSTORES_PATHS.name())).thenReturn(Optional.empty());
        // when, then
        assertThatExceptionOfType(ConfigurationException.class).isThrownBy(() -> provider.createConfiguration())
            .withMessageContaining(TRUSTSTORES_PATHS + " mandatory environment variable is not defined");
    }

    @Test
    void shouldThrowExceptionWhenTrustorePathsSizesDoNotMatch() {
        // given
        List<String> truststores = createListOfPathsWithExtension(2, JKS_EXTENSION);
        List<String> truststoresPasswords = createListOfPathsWithExtension(1, PASS_EXTENSION);

        mockTruststorePaths(truststores, truststoresPasswords);
        // when, then
        assertThatExceptionOfType(ConfigurationException.class)
            .isThrownBy(() -> provider.createConfiguration())
            .withMessageContaining("Size of " + TRUSTSTORES_PATHS
                + " does not match size of " + TRUSTSTORES_PASSWORDS_PATHS + " environment variables");
    }

    @Test
    void shouldReturnEmptyListWhenOptionalEnvNotPresent() {
        // given
        List<String> truststores = createListOfPathsWithExtension(2, JKS_EXTENSION);
        List<String> truststoresPasswords = createListOfPathsWithExtension(2, PASS_EXTENSION);
        mockTruststorePaths(truststores, truststoresPasswords);
        mockKeystorePaths(Optional.empty(), Optional.empty());
        // when
        AppConfiguration paths = provider.createConfiguration();
        // then
        assertThat(paths.getDestinationKeystorePaths()).isEmpty();
        assertThat(paths.getSourceKeystorePaths()).isEmpty();
    }

    private void mockTruststorePaths(List<String> truststores, List<String> truststoresPasswords) {
        mockTruststores(truststores);
        mockTruststoresPasswords(truststoresPasswords);
    }

    private void mockKeystorePaths(Optional<String> sourceKeystoresPairPaths, Optional<String> destKeystoresPairPaths) {
        mockKeystoreCopierSourcePaths(sourceKeystoresPairPaths);
        mockKeystoreCopierDestinationPaths(destKeystoresPairPaths);
    }

    private void mockTruststores(List<String> truststores) throws CertificatesPathsValidationException {
        when(envReader.getEnv(TRUSTSTORES_PATHS.name())).thenReturn(Optional.of(SAMPLE_TRUSTSTORES_PATHS));
        when(pathsSplitter.getValidatedPaths(TRUSTSTORES_PATHS, Optional.of(SAMPLE_TRUSTSTORES_PATHS)))
            .thenReturn(truststores);
    }

    private void mockTruststoresPasswords(List<String> truststoresPasswords)
        throws CertificatesPathsValidationException {
        Optional<String> passwordsPaths = Optional.of(SAMPLE_TRUSTSTORES_PASSWORDS_PATHS);
        when(envReader.getEnv(TRUSTSTORES_PASSWORDS_PATHS.name())).thenReturn(passwordsPaths);
        when(pathsSplitter.getValidatedPaths(TRUSTSTORES_PASSWORDS_PATHS, passwordsPaths))
            .thenReturn(truststoresPasswords);
    }

    private void mockKeystoreCopierSourcePaths(Optional<String> paths) {
        when(envReader.getEnv(KEYSTORE_SOURCE_PATHS.name())).thenReturn(paths);
    }

    private void mockKeystoreCopierDestinationPaths(Optional<String> paths) {
        when(envReader.getEnv(KEYSTORE_DESTINATION_PATHS.name())).thenReturn(paths);
    }

    private List<String> createListOfPathsWithExtension(int numberOfPaths, String passwordExtension) {
        List<String> paths = new ArrayList<>();
        while (numberOfPaths-- > 0) {
            paths.add(BASE_TRUSTSTORE_PATH + numberOfPaths + passwordExtension);
        }
        return paths;
    }

}
