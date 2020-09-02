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

import org.onap.oom.truststoremerger.api.ExitStatus;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.certification.file.provider.entry.CertificateWithAlias;
import org.onap.oom.truststoremerger.certification.file.model.Truststore;
import org.onap.oom.truststoremerger.certification.file.provider.FileManager;
import org.onap.oom.truststoremerger.certification.file.provider.PasswordReader;
import org.onap.oom.truststoremerger.certification.file.TruststoreFileFactory;
import org.onap.oom.truststoremerger.certification.file.TruststoreFilesListProvider;
import org.onap.oom.truststoremerger.configuration.path.TruststoresPathsProviderFactory;
import org.onap.oom.truststoremerger.configuration.path.TruststoresPathsProvider;
import org.onap.oom.truststoremerger.configuration.model.MergerConfiguration;
import org.onap.oom.truststoremerger.configuration.MergerConfigurationProvider;

import java.util.List;
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
        }
    }

    private void mergeTruststores() throws ExitableException {
        MergerConfiguration configuration = loadConfiguration();
        List<Truststore> truststoreFilesList = getTruststoreFilesList(configuration);

        Truststore baseFile = truststoreFilesList.get(FIRST_TRUSTSTORE_INDEX);
        baseFile.createBackup();

        for (int i = SECOND_TRUSTSTORE_INDEX; i < truststoreFilesList.size(); i++) {
            List<CertificateWithAlias> certificateWrappers = truststoreFilesList.get(i).getCertificates();
            baseFile.addCertificate(certificateWrappers);
        }

        baseFile.saveFile();
    }

    private MergerConfiguration loadConfiguration() throws ExitableException {
        TruststoresPathsProvider truststoresPathsProvider = TruststoresPathsProviderFactory.create();
        MergerConfigurationProvider factory = new MergerConfigurationProvider(truststoresPathsProvider);
        return factory.createConfiguration();
    }

    private List<Truststore> getTruststoreFilesList(MergerConfiguration configuration) throws ExitableException {
        TruststoreFileFactory truststoreFileFactory = new TruststoreFileFactory(new FileManager(),
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
