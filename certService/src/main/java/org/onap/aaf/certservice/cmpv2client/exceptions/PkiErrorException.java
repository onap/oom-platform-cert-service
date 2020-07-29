/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.certservice.cmpv2client.exceptions;

public class PkiErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance with detail message.
     */
    public PkiErrorException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with detail Throwable cause.
     */
    public PkiErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance with detail message and Throwable cause.
     */
    public PkiErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
