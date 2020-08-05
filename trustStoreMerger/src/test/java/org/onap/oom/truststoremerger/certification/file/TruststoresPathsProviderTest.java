package org.onap.oom.truststoremerger.certification.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.onap.oom.truststoremerger.certification.file.TruststoresPathsProvider.TRUSTSTORES_ENV;
import static org.onap.oom.truststoremerger.certification.file.TruststoresPathsProvider.TRUSTSTORES_PASSWORDS_ENV;

@ExtendWith(MockitoExtension.class)
class TruststoresPathsProviderTest {

    private static final String VALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.pem";
    private static final String VALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:";
    private static final String INVALID_TRUSTSTORES = "/opt/app/certificates/truststore.jks:/opt/app/certificates/truststore.invalid";
    private static final String INVALID_TRUSTSTORES_PASSWORDS = "/opt/app/certificates/truststore.pass:/.pass";

    @Mock
    private EnvProvider envProvider;
    private TruststoresPathsProvider truststoresPathsProvider;

    @BeforeEach
    void setUp() {
        truststoresPathsProvider = new TruststoresPathsProvider(envProvider, new PathValidator());
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresValid() throws TruststoresPathsProviderException {
        mockTruststoresEnv(VALID_TRUSTSTORES);

        assertThat(truststoresPathsProvider.getTruststores())
                .contains("/opt/app/certificates/truststore.jks",
                        "/opt/app/certificates/truststore.pem");
    }

    @Test
    void shouldReturnCorrectListWhenTruststoresPasswordsValid() throws TruststoresPathsProviderException {
        mockTruststoresPasswordsEnv(VALID_TRUSTSTORES_PASSWORDS);

        assertThat(truststoresPathsProvider.getTruststoresPasswords())
                .contains("/opt/app/certificates/truststore.pass",
                        "");
    }

    @Test
    void shouldThrowExceptionWhenTruststoresEmpty() {
        mockTruststoresEnv("");

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststores);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststoresPathsInvalid() {
        mockTruststoresEnv(INVALID_TRUSTSTORES);

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststores);
    }

    @Test
    void shouldThrowExceptionWhenOneOfTruststorePasswordPathsInvalid() {
        mockTruststoresPasswordsEnv(INVALID_TRUSTSTORES_PASSWORDS);

        assertThatExceptionOfType(TruststoresPathsProviderException.class)
                .isThrownBy(truststoresPathsProvider::getTruststoresPasswords);
    }

    private void mockTruststoresEnv(String truststores) {
        mockEnv(truststores, TRUSTSTORES_ENV);
    }

    private void mockTruststoresPasswordsEnv(String truststoresPasswords) {
        mockEnv(truststoresPasswords, TRUSTSTORES_PASSWORDS_ENV);
    }

    private void mockEnv(String envValue, String envName) {
        when(envProvider.getEnv(envName))
                .thenReturn(Optional.of(envValue));
    }
}
