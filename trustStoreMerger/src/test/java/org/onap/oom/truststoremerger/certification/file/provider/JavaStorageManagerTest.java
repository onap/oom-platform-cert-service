package org.onap.oom.truststoremerger.certification.file.provider;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;

@ExtendWith(MockitoExtension.class)
public class JavaStorageManagerTest {

    private KeyStore keyStore;
    @Mock
    private KeyStoreSpi keyStoreSpi;

    @BeforeEach
    void setUp() {
        keyStore = new KeyStore(keyStoreSpi, null, "") {
        };
    }

    @Test
    void shouldThrowExceptionWhenCannotSaveFile() throws ExitableException {
        //given
        File jksFile = TestCertificateProvider.getSampleJksFile();
        String jksTruststorePassword = "";
        jksFile.setWritable(false);
        JavaStorageManager javaStorageManager = JavaStorageManager
            .createAndLoadFile(keyStore, jksFile, jksTruststorePassword);

        //when, then
        assertThatExceptionOfType(WriteTruststoreFileException.class)
            .isThrownBy(javaStorageManager::saveFile);
    }

}
