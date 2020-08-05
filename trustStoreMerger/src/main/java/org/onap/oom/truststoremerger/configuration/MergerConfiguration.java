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

package org.onap.oom.truststoremerger.configuration;

import java.util.Collections;
import java.util.List;

public class MergerConfiguration {
    private final List<String> truststoreFilePaths;
    private final List<String> truststoreFilePasswordPaths;

    public MergerConfiguration(List<String> truststoreFilePaths,
                               List<String> truststoreFilePasswordPaths) {
        this.truststoreFilePaths = List.copyOf(truststoreFilePaths);
        this.truststoreFilePasswordPaths = List.copyOf(truststoreFilePasswordPaths);
    }

    public List<String> getTruststoreFilePaths() {
        return Collections.unmodifiableList(truststoreFilePaths);
    }


    public List<String> getTruststoreFilePasswordPaths() {
        return Collections.unmodifiableList(truststoreFilePasswordPaths);
    }

}
