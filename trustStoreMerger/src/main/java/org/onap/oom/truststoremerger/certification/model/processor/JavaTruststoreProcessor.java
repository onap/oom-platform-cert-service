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

package org.onap.oom.truststoremerger.certification.model.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.exception.AliasConflictException;
import org.onap.oom.truststoremerger.certification.exception.LoadTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.MissingTruststoreException;
import org.onap.oom.truststoremerger.certification.exception.TruststoreDataOperationException;
import org.onap.oom.truststoremerger.certification.exception.WriteTruststoreFileException;
import org.onap.oom.truststoremerger.certification.model.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.model.entry.CertificateWithAliasFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTruststoreProcessor extends TruststoreProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTruststoreProcessor.class);

    private final CertificateWithAliasFactory factory = new CertificateWithAliasFactory();
    private final KeyStore keyStore;
    private final String password;


    public JavaTruststoreProcessor(KeyStore keyStore, File storeFile, String password) {
        super(storeFile);
        this.keyStore = keyStore;
        this.password = password;
    }

    public List<CertificateWithAlias> getNotEmptyCertificateList() throws ExitableException {
        List<String> aliases = getTruststoreAliasesList();
        if (aliases.isEmpty()) {
            throw new MissingTruststoreException("Missing certificate aliases in file: " + getStoreFile().getPath());
        }
        return getWrappedCertificates(aliases);
    }

    public void addCertificates(List<CertificateWithAlias> certificatesWithAliases)
        throws ExitableException {
        if (getTruststoreAliasesList().isEmpty()) {
            throw new MissingTruststoreException("Missing certificate aliases in file: " + getStoreFile().getPath());
        }
        for (CertificateWithAlias certificate : certificatesWithAliases) {
            addCertificate(certificate);
        }
    }

    public void saveFile() throws WriteTruststoreFileException {
        try (FileOutputStream outputStream = new FileOutputStream(getStoreFile())) {
            keyStore.store(outputStream, this.password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot write truststore file");
            throw new WriteTruststoreFileException(e);
        }
    }

    public void loadFile() throws LoadTruststoreException {
        try {
            keyStore.load(new FileInputStream(getStoreFile()), this.password.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Cannot load file: {}", getStoreFile().getPath());
            throw new LoadTruststoreException(e);
        }
    }

    private void addCertificate(CertificateWithAlias certificate)
        throws TruststoreDataOperationException, AliasConflictException {
        if (hasAliasConflict(certificate)) {
            LOGGER.error("Alias conflict detected");
            throw new AliasConflictException("Alias conflict detected. Alias conflicted: " + certificate.getAlias());
        }
        try {
            keyStore.setCertificateEntry(certificate.getAlias(), certificate.getCertificate());
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot merge certificate with alias: {}", certificate.getAlias());
            throw new TruststoreDataOperationException(e);
        }
    }

    private boolean hasAliasConflict(CertificateWithAlias certificate) throws TruststoreDataOperationException {
        try {
            return keyStore.containsAlias(certificate.getAlias());
        } catch (KeyStoreException e) {
            LOGGER.error("Cannot check alias conflict");
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<CertificateWithAlias> getWrappedCertificates(List<String> aliases)
        throws TruststoreDataOperationException {

        List<CertificateWithAlias> certificateWrapped = new ArrayList<>();

        for (String alias : aliases) {
            certificateWrapped.add(createWrappedCertificate(alias));
        }
        return certificateWrapped;
    }

    private CertificateWithAlias createWrappedCertificate(String alias) throws TruststoreDataOperationException {
        try {
            return factory.createCertificateWithAlias(keyStore.getCertificate(alias), alias);
        } catch (KeyStoreException e) {
            LOGGER.warn("Cannot get certificate with alias: {} ", alias);
            throw new TruststoreDataOperationException(e);
        }
    }

    private List<String> getTruststoreAliasesList() throws TruststoreDataOperationException {
        try {
            List<String> aliases = Collections.list(keyStore.aliases());
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
