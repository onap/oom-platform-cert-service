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


package org.onap.oom.certservice.postprocessor;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.onap.oom.certservice.postprocessor.api.ExitStatus.ALIAS_CONFLICT_EXCEPTION;
import static org.onap.oom.certservice.postprocessor.api.ExitStatus.SUCCESS;
import static org.onap.oom.certservice.postprocessor.api.ExitStatus.UNEXPECTED_EXCEPTION;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.oom.certservice.postprocessor.merger.exception.AliasConflictException;

@ExtendWith(MockitoExtension.class)
public class AppExecutorTest {

    @Mock
    Runnable logic;
    @Mock
    AppExitHandler exitHandler;
    @InjectMocks
    AppExecutor executor = new AppExecutor();

    @Test
    void shouldExitWithUnexpectedException() {
        doThrow(new NullPointerException()).when(logic).run();
        doNothing().when(exitHandler).exit(UNEXPECTED_EXCEPTION);

        executor.execute(logic);

        verify(exitHandler).exit(UNEXPECTED_EXCEPTION);
    }

    @Test
    void shouldExitWithKnownException() {
        doThrow(new AliasConflictException("")).when(logic).run();
        doNothing().when(exitHandler).exit(ALIAS_CONFLICT_EXCEPTION);

        executor.execute(logic);

        verify(exitHandler).exit(ALIAS_CONFLICT_EXCEPTION);
    }

    @Test
    void shouldExitWithSuccess() {
        doNothing().when(logic).run();
        doNothing().when(exitHandler).exit(SUCCESS);

        executor.execute(logic);

        verify(exitHandler).exit(SUCCESS);
    }
}
