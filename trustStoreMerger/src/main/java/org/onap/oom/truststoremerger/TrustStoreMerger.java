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

package org.onap.oom.truststoremerger;

import java.util.List;
import org.onap.oom.truststoremerger.api.ExitStatus;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.TruststoreFilesListProvider;
import org.onap.oom.truststoremerger.certification.file.model.Truststore;
import org.onap.oom.truststoremerger.certification.file.model.TruststoreFactory;
import org.onap.oom.truststoremerger.certification.file.provider.FileManager;
import org.onap.oom.truststoremerger.certification.file.provider.PasswordReader;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.configuration.MergerConfigurationProvider;
import org.onap.oom.truststoremerger.configuration.model.MergerConfiguration;
import org.onap.oom.truststoremerger.configuration.path.DelimitedPathsReader;
import org.onap.oom.truststoremerger.configuration.path.DelimitedPathsReaderFactory;
import org.onap.oom.truststoremerger.configuration.path.env.EnvProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrustStoreMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustStoreMerger.class);
    private static final int FIRST_TRUSTSTORE_INDEX = 0;
    private static final int SECOND_TRUSTSTORE_INDEX = 1;

    private final AppExitHandler appExitHandler;

    TrustStoreMerger(AppExitHandler appExitHandler) {
        this.appExitHandler = appExitHandler;
    }

    void run() {
        try {
            mergeTruststores();
            appExitHandler.exit(ExitStatus.SUCCESS);
        } catch (ExitableException e) {
            LOGGER.error("Truststore Merger fails in execution: ", e);
            appExitHandler.exit(e.applicationExitStatus());
        } catch (Exception e) {
            LOGGER.error("Truststore Merger fails in execution: ", e);
            appExitHandler.exit(ExitStatus.UNEXPECTED_EXCEPTION);
        }
    }

    private void mergeTruststores() throws ExitableException {
        MergerConfiguration configuration = loadConfiguration();
        List<Truststore> truststoreFilesList = getTruststoreFiles(configuration);

        Truststore baseFile = truststoreFilesList.get(FIRST_TRUSTSTORE_INDEX);
        baseFile.createBackup();

        for (int i = SECOND_TRUSTSTORE_INDEX; i < truststoreFilesList.size(); i++) {
            Truststore truststores = truststoreFilesList.get(i);
            List<CertificateWithAlias> certificateWrappers = truststores.getCertificates();
            baseFile.addCertificate(certificateWrappers);
        }

        baseFile.saveFile();
    }

    private MergerConfiguration loadConfiguration() throws ExitableException {
        DelimitedPathsReaderFactory readerFactory = new DelimitedPathsReaderFactory(new EnvProvider());
        DelimitedPathsReader certificatesPathsReader = readerFactory.createCertificatePathsReader();
        DelimitedPathsReader passwordsPathsReader = readerFactory.createPasswordPathsReader();
        DelimitedPathsReader copierPathsReader = readerFactory.createKeystoreCopierPathsReader();
        MergerConfigurationProvider factory = new MergerConfigurationProvider(certificatesPathsReader,
            passwordsPathsReader,
            copierPathsReader);
        return factory.createConfiguration();
    }

    private List<Truststore> getTruststoreFiles(MergerConfiguration configuration) throws ExitableException {
        TruststoreFactory truststoreFileFactory = new TruststoreFactory(new FileManager(),
            new PasswordReader());
        TruststoreFilesListProvider truststoreFilesListProvider = new TruststoreFilesListProvider(
            truststoreFileFactory);
        return truststoreFilesListProvider
            .getTruststoreFilesList(
                configuration.getTruststoreFilePaths(),
                configuration.getTruststoreFilePasswordPaths()
            );
    }
}
