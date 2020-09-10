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

package org.onap.oom.truststoremerger.merger;

import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.merger.model.Truststore;
import org.onap.oom.truststoremerger.merger.model.certificate.CertificateWithAlias;

public class TruststoreMerger {

    private static final int FIRST_TRUSTSTORE_INDEX = 0;
    private static final int SECOND_TRUSTSTORE_INDEX = 1;

    public void mergeTruststores(AppConfiguration configuration) throws ExitableException {
        List<Truststore> truststoreFilesList = getTruststoreFiles(configuration);

        Truststore baseFile = truststoreFilesList.get(FIRST_TRUSTSTORE_INDEX);
        baseFile.createBackup();

        for (int i = SECOND_TRUSTSTORE_INDEX; i < truststoreFilesList.size(); i++) {
            Truststore truststore = truststoreFilesList.get(i);
            List<CertificateWithAlias> certificateWrappers = truststore.getCertificates();
            baseFile.addCertificates(certificateWrappers);
        }

        baseFile.saveFile();
    }

    private static List<Truststore> getTruststoreFiles(AppConfiguration configuration) throws ExitableException {
        return TruststoreFilesProvider
            .getTruststoreFiles(
                configuration.getTruststoreFilePaths(),
                configuration.getTruststoreFilePasswordPaths()
            );
    }
}
