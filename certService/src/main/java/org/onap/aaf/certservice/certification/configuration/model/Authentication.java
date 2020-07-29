/*
 * ============LICENSE_START=======================================================
 * PROJECT
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

package org.onap.oom.certservice.certification.configuration.model;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class Authentication {

    private static final int MAX_IAK_RV_LENGTH = 256;

    @NotNull
    @Length(min = 1, max = MAX_IAK_RV_LENGTH)
    private String iak;
    @NotNull
    @Length(min = 1, max = MAX_IAK_RV_LENGTH)
    private String rv;

    public String getIak() {
        return iak;
    }

    public void setIak(String iak) {
        this.iak = iak;
    }

    public String getRv() {
        return rv;
    }

    public void setRv(String rv) {
        this.rv = rv;
    }

    @Override
    public String toString() {
        return "Authentication{"
                + "  iak=*****"
                + ", rv=*****"
                + '}';
    }
}
