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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.certification.path.TruststoresPathsProvider;
import org.onap.oom.truststoremerger.certification.path.TruststoresPathsProviderException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergerConfigurationFactoryTest {

    private static final String BASE_TRUSTSTORE_PATH = "/opt/app/truststore_";
    private static final String TRUSTSTORE_EXTENSION = ".jks";
    private static final String PASSWORD_EXTENSION = ".pass";

    @Mock
    private TruststoresPathsProvider pathsProvider;
    private MergerConfigurationFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MergerConfigurationFactory(pathsProvider);
    }

    @Test
    void shouldReturnConfigurationWithCorrectPaths() throws TruststoresPathsProviderException, MergerConfigurationException {
        int numberOfPaths = 5;
        List<String> truststoresPaths = createListOfPathsWithExtension(numberOfPaths, TRUSTSTORE_EXTENSION);
        List<String> truststorePasswordPaths = createListOfPathsWithExtension(numberOfPaths, PASSWORD_EXTENSION);
        mockPaths(truststoresPaths, truststorePasswordPaths);

        MergerConfiguration configuration = factory.createConfiguration();

        assertThat(configuration.getTruststoreFilePaths()).containsAll(truststoresPaths);
        assertThat(configuration.getTruststoreFilePasswordPaths()).containsAll(truststorePasswordPaths);
    }

    @Test
    void shouldThrowExceptionWhenTruststoresLenghtDifferentThanTruststoresPasswordsLength() throws TruststoresPathsProviderException {
        int numberOfTruststores = 5;
        int numberOfTruststoresPasswords = 4;
        List<String> truststoresPaths = createListOfPathsWithExtension(numberOfTruststores, TRUSTSTORE_EXTENSION);
        List<String> truststorePasswordPaths = createListOfPathsWithExtension(numberOfTruststoresPasswords, PASSWORD_EXTENSION);
        mockPaths(truststoresPaths, truststorePasswordPaths);

        assertThatExceptionOfType(MergerConfigurationException.class)
                .isThrownBy(factory::createConfiguration);
    }

    private void mockPaths(List<String> truststores, List<String> truststoresPasswords) throws TruststoresPathsProviderException {
        mockTruststores(truststores);
        mockTruststoresPasswords(truststoresPasswords);
    }

    private void mockTruststores(List<String> truststores) throws TruststoresPathsProviderException {
        when(pathsProvider.getTruststores()).thenReturn(truststores);
    }

    private void mockTruststoresPasswords(List<String> truststoresPasswords) throws TruststoresPathsProviderException {
        when(pathsProvider.getTruststoresPasswords()).thenReturn(truststoresPasswords);
    }

    private List<String> createListOfPathsWithExtension(int numberOfPaths, String password_extension) {
        List<String> paths = new ArrayList<>();
        while (numberOfPaths-- > 0) {
            paths.add(BASE_TRUSTSTORE_PATH + numberOfPaths + password_extension);
        }
        return paths;
    }
}
