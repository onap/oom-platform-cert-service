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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.entry.AliasCertificateWrapper;
import org.onap.oom.truststoremerger.certification.entry.CertificateWrapper;
import org.onap.oom.truststoremerger.certification.file.exception.AliasConflictException;
import org.onap.oom.truststoremerger.certification.file.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.file.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.file.exception.WriteTruststoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitableKeystoreInstance {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExitableKeystoreInstance.class);
    private final KeyStore keyStore;

    ExitableKeystoreInstance(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public List<CertificateWrapper> getTruststoreCertificates(File file, String password) throws ExitableException {
        LOGGER.debug("Reading Certificates from file: {}", file.getPath());
        loadFile(file, password);
        List<String> aliases = getTruststoreAliasesList();
        return getWrapperedCertificates(aliases);
    }

    public void addCertificates(List<CertificateWrapper> certificateWrappers, File file, String password)
        throws ExitableException {
        LOGGER.debug("Adding certificates to file: {}", file.getPath());
        loadFile(file, password);
        for (CertificateWrapper certificate : certificateWrappers) {
            addCertificate(certificate);
        }
        saveFile(file, password);
    }

    private void loadFile(File file, String password) throws LoadTruststoreException {
        try {
            keyStore.load(new FileInputStream(file), password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot load file: {}", file.getPath());
            throw new LoadTruststoreException(e);
        }
    }

    private void saveFile(File file, String password) throws WriteTruststoreFileException {
        LOGGER.info("saving file");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            keyStore.store(outputStream, password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot write truststore file");
            throw new WriteTruststoreFileException(e);
        }
    }

    private void addCertificate(CertificateWrapper certificate)
        throws TruststoreDataOperationException, AliasConflictException {
        if (hasAliasConflict(certificate)) {
            LOGGER.error("Alias conflict detected");
            throw new AliasConflictException("Alias conflict detected. Alias conflicted: {}" + certificate.getAlias());
        }
        try {
            keyStore.setCertificateEntry(certificate.getAlias(), certificate.getCertificate());
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot merge certificate with alias: {}", certificate.getAlias());
            throw new TruststoreDataOperationException(e);
        }
    }

    private boolean hasAliasConflict(CertificateWrapper certificate) throws TruststoreDataOperationException {
        try {
            return keyStore.containsAlias(certificate.getAlias());
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot investigate alias conflict");
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<CertificateWrapper> getWrapperedCertificates(List<String> aliases)
        throws MissingTruststoreException, TruststoreDataOperationException {
        if (aliases.isEmpty()) {
            throw new MissingTruststoreException("Missing truststore aliases");
        }
        List<CertificateWrapper> certificateWrappered = new ArrayList<>();

        for (String alias : aliases) {
            certificateWrappered.add(createWrapperedCertificate(alias));
        }
        return certificateWrappered;
    }

    private AliasCertificateWrapper createWrapperedCertificate(String alias) throws TruststoreDataOperationException {
        try {
            return new AliasCertificateWrapper(keyStore.getCertificate(alias), alias);
        } catch (KeyStoreException e) {
            LOGGER.warn("Cannot get certificate with alias: {} ", alias);
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<String> getTruststoreAliasesList() throws TruststoreDataOperationException {
        try {
            List<String> aliases = new ArrayList<>(Collections.list(keyStore.aliases()));
            return getFilteredAlias(aliases);
        } catch (KeyStoreException e) {
            LOGGER.warn("Cannot read truststore aliases");
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<String> getFilteredAlias(List<String> aliases) throws KeyStoreException {
        List<String> filteredAlias = new ArrayList<>();
        for (String alias : aliases) {
            if (keyStore.isCertificateEntry(alias)) {
                filteredAlias.add(alias);
            }
        }
        return filteredAlias;
    }

}
