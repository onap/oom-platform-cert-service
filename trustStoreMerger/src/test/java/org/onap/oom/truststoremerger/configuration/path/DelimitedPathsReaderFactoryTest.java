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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.onap.oom.truststoremerger.configuration.path.env.EnvProvider;

class DelimitedPathsReaderFactoryTest {

    @Test
    void shouldReturnObjectOfDelimitedPathsReaderType() {
        // given
        DelimitedPathsReaderFactory readerFactory = new DelimitedPathsReaderFactory(new EnvProvider());
        //when, then
        assertThat(readerFactory.createPasswordPathsReader()).isInstanceOf(DelimitedPathsReader.class);
        assertThat(readerFactory.createCertificatePathsReader()).isInstanceOf(DelimitedPathsReader.class);
        assertThat(readerFactory.createKeystoreCopierPathsReader()).isInstanceOf(DelimitedPathsReader.class);
    }

}
