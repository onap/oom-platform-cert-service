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

package org.onap.oom.certservice.postprocessor.merger.model;

import java.io.File;
import java.util.List;
import org.onap.oom.certservice.postprocessor.common.FileTools;
import org.onap.oom.certservice.postprocessor.merger.model.certificate.CertificateWithAlias;

public abstract class Truststore {

    final File storeFile;

    private final FileTools fileTools;

    protected Truststore(File storeFile, FileTools fileTools) {
        this.storeFile = storeFile;
        this.fileTools = fileTools;
    }

    public void createBackup() {
        fileTools.createBackup(storeFile);
    }

    public abstract List<CertificateWithAlias> getCertificates();

    public abstract void addCertificates(List<CertificateWithAlias> certificates);

    public abstract void saveFile();
}
