package org.onap.oom.truststoremerger.certification.file.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaStorageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaStorageManager.class);

    private final KeyStore keyStore;
    private final File storeFile;
    private final String password;

    private JavaStorageManager(KeyStore keyStore, File storeFile, String password) {
        this.keyStore = keyStore;
        this.storeFile = storeFile;
        this.password = password;
    }

    public static JavaStorageManager createAndLoadFile(KeyStore keyStore, File storeFile, String password)
        throws LoadTruststoreException {
        JavaStorageManager javaStorageManager = new JavaStorageManager(keyStore, storeFile, password);
        javaStorageManager.loadFile();
        return javaStorageManager;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public String getFilePath() {
        return storeFile.getPath();
    }

    public void saveFile() throws WriteTruststoreFileException {
        try (FileOutputStream outputStream = new FileOutputStream(storeFile)) {
            keyStore.store(outputStream, password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot write truststore file");
            throw new WriteTruststoreFileException(e);
        }
    }

    private void loadFile() throws LoadTruststoreException {
        try {
            keyStore.load(new FileInputStream(storeFile), password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot load file: {}", storeFile.getPath());
            throw new LoadTruststoreException(e);
        }
    }
}
