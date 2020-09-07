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

package org.onap.oom.truststoremerger.configuration.path;

import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidCertificatesPaths;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidPasswordPaths;
import static org.onap.oom.truststoremerger.configuration.path.validation.ValidationFunctions.doesItContainValidPathsToCopy;

import org.onap.oom.truststoremerger.configuration.path.env.EnvProvider;

public class DelimitedPathsReaderFactory {

    private DelimitedPathsReaderFactory() {
    }

    public static DelimitedPathsReader createPasswordPathsReader() {
        return new DelimitedPathsReader(new EnvProvider(), doesItContainValidPasswordPaths());
    }

    public static DelimitedPathsReader createCertificatePathsReader() {
        return new DelimitedPathsReader(new EnvProvider(), doesItContainValidCertificatesPaths());
    }

    public static DelimitedPathsReader createKeystoreCopierPathsReader() {
        return new DelimitedPathsReader(new EnvProvider(), doesItContainValidPathsToCopy());
    }
}
