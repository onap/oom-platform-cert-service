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

package org.onap.oom.truststoremerger.certification.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.onap.oom.truststoremerger.certification.file.exception.KeystoreInstanceException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.provider.CertificateStoreController;
import org.onap.oom.truststoremerger.certification.file.provider.CertificateStoreControllerFactory;
import org.onap.oom.truststoremerger.certification.file.provider.PemManipulator;

public class TestCertificateProvider {

    public static final String SAMPLE_P12_TRUSTSTORE_FILE_PATH = "src/test/resources/truststore-p12.p12";
    public static final String SAMPLE_P12_TRUSTSTORE_PASSWORD = "88y9v5D8H3SG6bZWRVHDfOAo";
    public static final String TMP_P12_TRUSTSTORE_FILE_PATH = "src/test/resources/tmp-truststore-p12.p12";

    public static final String SAMPLE_P12_KEYSTORE_FILE_PATH = "src/test/resources/keystore.p12";
    public static final String SAMPLE_P12_KEYSTORE_PASSWORD = "Foh49MJNYI7S_pEzE9gvUDSu";

    public static final String SAMPLE_JKS_TRUSTSTORE_FILE_PATH = "src/test/resources/truststore-jks.jks";
    public static final String SAMPLE_JKS_TRUSTSTORE_UNIQUE_ALIAS_FILE_PATH = "src/test/resources/truststore-jks-uniq.jks";
    public static final String SAMPLE_JKS_TRUSTSTORE_PASSWORD = "EOyuFbuYDyq_EhpboM72RHua";
    public static final String TMP_JKS_TRUSTSTORE_FILE_PATH = "src/test/resources/tmp-truststore-jks.jks";

    public static final String SAMPLE_PEM_TRUSTSTORE_FILE_PATH = "src/test/resources/truststore.pem";
    public static final String EMPTY_PEM_TRUSTSTORE_FILE_PATH = "src/test/resources/empty-truststore.pem";
    public static final String TMP_PEM_TRUSTSTORE_FILE_PATH = "src/test/resources/tmp-truststore.pem";

    private static final CertificateStoreControllerFactory certificateStoreControllerFactory = new CertificateStoreControllerFactory();

    public static JavaTruststore getSampleP12Truststore() throws LoadTruststoreException, KeystoreInstanceException {
        return createP12TruststoreInstance(SAMPLE_P12_TRUSTSTORE_FILE_PATH, SAMPLE_P12_TRUSTSTORE_PASSWORD);
    }

    public static JavaTruststore getSampleP12Keystore() throws LoadTruststoreException, KeystoreInstanceException {
        return createP12TruststoreInstance(SAMPLE_P12_KEYSTORE_FILE_PATH, SAMPLE_P12_KEYSTORE_PASSWORD);
    }

    public static JavaTruststore createTmpP12TruststoreFile()
        throws IOException, LoadTruststoreException, KeystoreInstanceException {
        copyFile(SAMPLE_P12_TRUSTSTORE_FILE_PATH, TMP_P12_TRUSTSTORE_FILE_PATH);
        return createP12TruststoreInstance(TMP_P12_TRUSTSTORE_FILE_PATH, SAMPLE_P12_TRUSTSTORE_PASSWORD);
    }

    public static JavaTruststore getTmpP12TruststoreFile() throws LoadTruststoreException, KeystoreInstanceException {
        return createP12TruststoreInstance(TMP_P12_TRUSTSTORE_FILE_PATH, SAMPLE_P12_TRUSTSTORE_PASSWORD);
    }

    private static JavaTruststore createP12TruststoreInstance(String filePath, String password)
        throws LoadTruststoreException, KeystoreInstanceException {
        File certFile = getFile(filePath);
        CertificateStoreController storeController = certificateStoreControllerFactory
            .createLoadedPkcs12CertificateStoreController(certFile, password);
        return new JavaTruststore(certFile, password, storeController);
    }

    public static PemTruststore getSamplePemTruststoreFile() {
        return new PemTruststore(getFile(SAMPLE_PEM_TRUSTSTORE_FILE_PATH), new PemManipulator());
    }

    public static PemTruststore getEmptyPemTruststoreFile() {
        return new PemTruststore(getFile(EMPTY_PEM_TRUSTSTORE_FILE_PATH), new PemManipulator());
    }

    public static PemTruststore createEmptyTmpPemTruststoreFile() throws IOException {
        copyFile(EMPTY_PEM_TRUSTSTORE_FILE_PATH, TMP_PEM_TRUSTSTORE_FILE_PATH);
        return new PemTruststore(getFile(TMP_PEM_TRUSTSTORE_FILE_PATH), new PemManipulator());
    }

    public static PemTruststore createTmpPemTruststoreFile() throws IOException {
        copyFile(SAMPLE_PEM_TRUSTSTORE_FILE_PATH, TMP_PEM_TRUSTSTORE_FILE_PATH);
        return new PemTruststore(getFile(TMP_PEM_TRUSTSTORE_FILE_PATH), new PemManipulator());
    }

    public static PemTruststore getTmpPemTruststoreFile() {
        return new PemTruststore(getFile(TMP_PEM_TRUSTSTORE_FILE_PATH), new PemManipulator());
    }

    public static String getExpectedPemCertificateAsString() throws IOException {
        Path samplePemFilePath = Paths.get(SAMPLE_PEM_TRUSTSTORE_FILE_PATH);
        return Files.readString(samplePemFilePath);
    }

    public static JavaTruststore getSampleJksTruststoreFile() throws LoadTruststoreException, KeystoreInstanceException {
        return createJKSTruststoreInstance(SAMPLE_JKS_TRUSTSTORE_FILE_PATH, SAMPLE_JKS_TRUSTSTORE_PASSWORD);
    }

    public static JavaTruststore getSampleJksTruststoreFileWithUniqueAlias()
        throws LoadTruststoreException, KeystoreInstanceException {
        return createJKSTruststoreInstance(SAMPLE_JKS_TRUSTSTORE_UNIQUE_ALIAS_FILE_PATH,
            SAMPLE_JKS_TRUSTSTORE_PASSWORD);
    }

    public static JavaTruststore createTmpJksTruststoreFileWithUniqAlias()
        throws IOException, LoadTruststoreException, KeystoreInstanceException {
        copyFile(SAMPLE_JKS_TRUSTSTORE_UNIQUE_ALIAS_FILE_PATH, TMP_JKS_TRUSTSTORE_FILE_PATH);
        return createJKSTruststoreInstance(TMP_JKS_TRUSTSTORE_FILE_PATH, SAMPLE_JKS_TRUSTSTORE_PASSWORD);
    }

    public static void removeTemporaryFiles() throws IOException {
        Files.deleteIfExists(Paths.get(TMP_PEM_TRUSTSTORE_FILE_PATH));
        Files.deleteIfExists(Paths.get(TMP_JKS_TRUSTSTORE_FILE_PATH));
        Files.deleteIfExists(Paths.get(TMP_P12_TRUSTSTORE_FILE_PATH));
    }

    private static JavaTruststore createJKSTruststoreInstance(String filePath, String password)
        throws LoadTruststoreException, KeystoreInstanceException {
        File certFile = getFile(filePath);
        CertificateStoreController storeController = certificateStoreControllerFactory
            .createLoadedJksCertificateStoreController(certFile, password);
        return new JavaTruststore(certFile, password, storeController);
    }

    private static void copyFile(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    private static File getFile(String path) {
        return new File(path);
    }
}
