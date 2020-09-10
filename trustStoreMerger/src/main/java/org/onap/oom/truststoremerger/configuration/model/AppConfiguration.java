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

package org.onap.oom.truststoremerger.configuration.model;

import java.util.Collections;
import java.util.List;

public final class AppConfiguration {
    private final List<String> truststoreFilePaths;
    private final List<String> truststoreFilePasswordPaths;
    private final List<String> sourceKeystorePaths;
    private final List<String> destinationKeystorePaths;

    public AppConfiguration(List<String> truststoreFilePaths,
        List<String> truststoreFilePasswordPaths, List<String> sourceKeystorePaths,
        List<String> destinationKeystorePaths) {
        this.truststoreFilePaths = List.copyOf(truststoreFilePaths);
        this.truststoreFilePasswordPaths = List.copyOf(truststoreFilePasswordPaths);
        this.sourceKeystorePaths = List.copyOf(sourceKeystorePaths);
        this.destinationKeystorePaths = List.copyOf(destinationKeystorePaths);
    }

    public List<String> getTruststoreFilePaths() {
        return Collections.unmodifiableList(truststoreFilePaths);
    }

    public List<String> getTruststoreFilePasswordPaths() {
        return Collections.unmodifiableList(truststoreFilePasswordPaths);
    }


    public List<String> getDestinationKeystorePaths() {
        return Collections.unmodifiableList(destinationKeystorePaths);
    }

    public List<String> getSourceKeystorePaths() {
        return Collections.unmodifiableList(sourceKeystorePaths);
    }
}
