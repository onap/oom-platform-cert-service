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

package org.onap.oom.truststoremerger.copier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.onap.oom.truststoremerger.api.CertificateConstants;
import org.onap.oom.truststoremerger.common.BackupCreator;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.copier.exception.KeystoreFileCopyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeystoreCopier {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreCopier.class);

    private BackupCreator backuper;

    public KeystoreCopier(BackupCreator backuper) {
        this.backuper = backuper;
    }

    public void copyKeystores(AppConfiguration configuration) {

        final List<String> sources = configuration.getSourceKeystorePaths();
        final List<String> destinations = configuration.getDestinationKeystorePaths();

        for (int i = 0; i < sources.size(); i++) {
            copy(sources.get(i), destinations.get(i));
        }
    }

    private void copy(String sourcePath, String destinationPath) {
        try {
            final File source = new File(sourcePath);
            final File destination = new File(destinationPath);

            if (source.exists()) {
                if (destination.exists()) {
                    backuper.createBackup(destination);
                }
                FileUtils.copyFile(source, destination);
                LOG.debug("Keystore copied from '{}' to '{}'.", source.getAbsolutePath(),
                    destination.getAbsolutePath());
            } else {
                LOG
                    .warn("Keystore file does not exist '{}' -->  skipping copy operation.", source.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new KeystoreFileCopyException(e);
        }
    }

}
