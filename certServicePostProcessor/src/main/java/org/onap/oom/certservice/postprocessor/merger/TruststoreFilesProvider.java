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

package org.onap.oom.certservice.postprocessor.merger;

import java.util.ArrayList;
import java.util.List;
import org.onap.oom.certservice.postprocessor.merger.model.Truststore;
import org.onap.oom.certservice.postprocessor.merger.model.TruststoreFactory;

public class TruststoreFilesProvider {


    private TruststoreFilesProvider() {
    }

    public static List<Truststore> getTruststoreFiles(List<String> truststoreFilePaths,
                                                      List<String> truststoreFilePasswordPaths) {
        List<Truststore> truststoreFiles = new ArrayList<>();
        for (int i = 0; i < truststoreFilePaths.size(); i++) {
            String truststorePath = truststoreFilePaths.get(i);
            String passwordPath = truststoreFilePasswordPaths.get(i);

            Truststore truststore = TruststoreFactory.create(truststorePath, passwordPath);
            truststoreFiles.add(truststore);
        }

        return truststoreFiles;
    }
}
