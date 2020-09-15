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

import org.onap.oom.certservice.postprocessor.api.ExitStatus;
import org.onap.oom.certservice.postprocessor.api.ExitableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppExecutor.class);

    private AppExitHandler exitHandler;

    AppExecutor() {
        this(new AppExitHandler());
    }

    AppExecutor(AppExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }


    public void execute(Runnable logic) {
        try {
            logic.run();
            exitHandler.exit(ExitStatus.SUCCESS);
        } catch (ExitableException e) {
            LOGGER.error("Application failed: ", e);
            exitHandler.exit(e.applicationExitStatus());
        } catch (Exception e) {
            LOGGER.error("Application failed (unexpected error): ", e);
            exitHandler.exit(ExitStatus.UNEXPECTED_EXCEPTION);
        }
    }
}
