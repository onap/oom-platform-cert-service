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
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.KEYSTORE_DESTINATION_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.KEYSTORE_SOURCE_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PASSWORDS_PATHS_ENV;
import static org.onap.oom.truststoremerger.configuration.ConfigurationEnvs.TRUSTSTORES_PATHS_ENV;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.configuration.exception.MergerConfigurationException;
import org.onap.oom.truststoremerger.configuration.exception.TruststoresPathsProviderException;
import org.onap.oom.truststoremerger.configuration.model.MergerConfiguration;
import org.onap.oom.truststoremerger.configuration.path.DelimitedPathsReader;

@ExtendWith(MockitoExtension.class)
class MergerConfigurationProviderTest {

    private static final String BASE_TRUSTSTORE_PATH = "/opt/app/truststore_";
    private static final String KEYSTORE_PATH = "/opt/app/keystore_";
    private static final String ANOTHER_KEYSTORE_PATH = "/opt/app/external/keystore_";
    private static final String JKS_EXTENSION = ".jks";
    private static final String PEM_EXTENSION = ".pem";
    private static final String PASS_EXTENSION = ".pass";

    @Mock
    private DelimitedPathsReader certificatesPathsProvider;
    @Mock
    private DelimitedPathsReader passwordsPathsProvider;
    @Mock
    private DelimitedPathsReader copierPathsReader;
    private MergerConfigurationProvider factory;

    @BeforeEach
    void setUp() {
        factory = new MergerConfigurationProvider(certificatesPathsProvider, passwordsPathsProvider, copierPathsReader);
    }

    @Test
    void shouldReturnConfigurationWithCorrectPaths()
        throws TruststoresPathsProviderException, MergerConfigurationException {
        int numberOfPaths = 5;
        List<String> truststoresPaths = createListOfPathsWithExtension(numberOfPaths, JKS_EXTENSION);
        List<String> truststorePasswordPaths = createListOfPathsWithExtension(numberOfPaths, PASS_EXTENSION);
        mockTruststorePaths(truststoresPaths, truststorePasswordPaths);

        List<String> sourceKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(KEYSTORE_PATH,
            numberOfPaths, PEM_EXTENSION);
        List<String> destKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(ANOTHER_KEYSTORE_PATH,
            numberOfPaths, PEM_EXTENSION);
        mockKeystorePaths(sourceKeystoresPairPaths, destKeystoresPairPaths);

        MergerConfiguration configuration = factory.createConfiguration();

        assertThat(configuration.getTruststoreFilePaths()).containsAll(truststoresPaths);
        assertThat(configuration.getTruststoreFilePasswordPaths()).containsAll(truststorePasswordPaths);
        assertThat(configuration.getSourceKeystorePaths()).containsAll(sourceKeystoresPairPaths);
        assertThat(configuration.getDestinationKeystorePaths()).containsAll(destKeystoresPairPaths);
    }

    @Test
    void shouldThrowExceptionWhenTruststoresLenghtDifferentThanTruststoresPasswordsLength()
        throws TruststoresPathsProviderException {
        int numberOfCertificates = 5;
        int numberOfTruststoresPasswords = 4;
        List<String> truststoresPaths = createListOfPathsWithExtension(numberOfCertificates, JKS_EXTENSION);
        List<String> truststorePasswordPaths = createListOfPathsWithExtension(numberOfTruststoresPasswords, PASS_EXTENSION);
        mockTruststorePaths(truststoresPaths, truststorePasswordPaths);

        List<String> sourceKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(KEYSTORE_PATH,
            numberOfCertificates, PEM_EXTENSION);
        List<String> destKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(ANOTHER_KEYSTORE_PATH,
            numberOfCertificates, PEM_EXTENSION);
        mockKeystorePaths(sourceKeystoresPairPaths, destKeystoresPairPaths);

        assertThatExceptionOfType(MergerConfigurationException.class)
            .isThrownBy(factory::createConfiguration);
    }

    @Test
    void shouldThrowExceptionWhenSourceLenghtDifferentThanDestinationLength()
        throws TruststoresPathsProviderException {
        int numberOfCertificates = 5;
        int anotherNumberOfCertificates = 1;
        List<String> truststoresPaths = createListOfPathsWithExtension(numberOfCertificates, JKS_EXTENSION);
        List<String> truststorePasswordPaths = createListOfPathsWithExtension(numberOfCertificates, PASS_EXTENSION);
        mockTruststorePaths(truststoresPaths, truststorePasswordPaths);

        List<String> sourceKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(KEYSTORE_PATH,
            numberOfCertificates, PEM_EXTENSION);
        List<String> destKeystoresPairPaths = createListOfKeystorePairsPathsWithExtension(ANOTHER_KEYSTORE_PATH,
            anotherNumberOfCertificates, PEM_EXTENSION);
        mockKeystorePaths(sourceKeystoresPairPaths, destKeystoresPairPaths);

        assertThatExceptionOfType(MergerConfigurationException.class)
            .isThrownBy(factory::createConfiguration);
    }

    private void mockTruststorePaths(List<String> truststores, List<String> truststoresPasswords)
        throws TruststoresPathsProviderException {
        mockTruststores(truststores);
        mockTruststoresPasswords(truststoresPasswords);
    }

    private void mockKeystorePaths(List<String> sourceKeystoresPairPaths, List<String> destKeystoresPairPaths)
        throws TruststoresPathsProviderException {
        mockKeystoreCopierSourcePaths(sourceKeystoresPairPaths);
        mockKeystoreCopierDestinationPaths(destKeystoresPairPaths);
    }

    private void mockTruststores(List<String> truststores) throws TruststoresPathsProviderException {
        when(certificatesPathsProvider.get(TRUSTSTORES_PATHS_ENV)).thenReturn(truststores);
    }

    private void mockTruststoresPasswords(List<String> truststoresPasswords) throws TruststoresPathsProviderException {
        when(passwordsPathsProvider.get(TRUSTSTORES_PASSWORDS_PATHS_ENV)).thenReturn(truststoresPasswords);
    }

    private void mockKeystoreCopierSourcePaths(List<String> paths) throws TruststoresPathsProviderException {
        when(copierPathsReader.get(KEYSTORE_SOURCE_PATHS_ENV)).thenReturn(paths);
    }

    private void mockKeystoreCopierDestinationPaths(List<String> paths) throws TruststoresPathsProviderException {
        when(copierPathsReader.get(KEYSTORE_DESTINATION_PATHS_ENV)).thenReturn(paths);
    }

    private List<String> createListOfPathsWithExtension(int numberOfPaths, String password_extension) {
        List<String> paths = new ArrayList<>();
        while (numberOfPaths-- > 0) {
            paths.add(BASE_TRUSTSTORE_PATH + numberOfPaths + password_extension);
        }
        return paths;
    }

    private List<String> createListOfKeystorePairsPathsWithExtension(String path, int numberOfPaths,
        String certExtension) {
        List<String> paths = new ArrayList<>();
        String passExtension = certExtension.equalsIgnoreCase(".pem") ? certExtension : ".pass";
        while (numberOfPaths-- > 0) {
            paths.add(path + numberOfPaths + certExtension);
            paths.add(MergerConfigurationProviderTest.ANOTHER_KEYSTORE_PATH + numberOfPaths + passExtension);
        }
        return paths;
    }


}
