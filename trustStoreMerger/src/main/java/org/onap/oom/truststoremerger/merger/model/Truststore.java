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

package org.onap.oom.truststoremerger.merger.model;

import java.io.File;
import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.merger.exception.CreateBackupException;
import org.onap.oom.truststoremerger.merger.model.certificate.CertificateWithAlias;
import org.onap.oom.truststoremerger.common.BackupCreator;

public abstract class Truststore {

    final File storeFile;

    public Truststore(File storeFile) {
        this.storeFile = storeFile;
    }

    public void createBackup() throws CreateBackupException {
        BackupCreator.createBackup(storeFile);
    }

    public abstract List<CertificateWithAlias> getCertificates() throws ExitableException;

    public abstract void addCertificates(List<CertificateWithAlias> certificates) throws ExitableException;

    public abstract void saveFile() throws ExitableException;
}
