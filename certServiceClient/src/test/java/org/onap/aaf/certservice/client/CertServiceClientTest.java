/*============LICENSE_START=======================================================
 * aaf-certservice-client
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

package org.onap.aaf.certservice.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.onap.aaf.certservice.client.api.ExitStatus.CLIENT_CONFIGURATION_EXCEPTION;
import static org.onap.aaf.certservice.client.api.ExitStatus.SUCCESS;

@ExtendWith(MockitoExtension.class)
class CertServiceClientTest {
    @Spy
    AppExitHandler appExitHandler = new AppExitHandler();

    @Test
    public void shouldExitWithDefinedExitCode_onRunCallWhenNoEnvsPresent() {
        //  given
        doNothing().when(appExitHandler).exit(CLIENT_CONFIGURATION_EXCEPTION);
        doNothing().when(appExitHandler).exit(SUCCESS);
        CertServiceClient certServiceClient = new CertServiceClient(appExitHandler);
        //  when
        certServiceClient.run();
        //  then
        verify(appExitHandler).exit(CLIENT_CONFIGURATION_EXCEPTION);
        verify(appExitHandler).exit(SUCCESS);
    }
}
