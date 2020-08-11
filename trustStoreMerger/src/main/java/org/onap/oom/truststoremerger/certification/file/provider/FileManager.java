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

package org.onap.oom.truststoremerger.certification.file.provider;

import java.io.File;

public class FileManager {
    private static final int NOT_FOUND_INDEX=-1;

    String getExtension(File file) {
        int extStartIndex = file.getName().lastIndexOf(".");
        if (extStartIndex == NOT_FOUND_INDEX) {
            return "";
        }
        return file.getName().substring(extStartIndex);
    }

    boolean checkIfFileExists(File file){
        return file.exists();
    }

}
