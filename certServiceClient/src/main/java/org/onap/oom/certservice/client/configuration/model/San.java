/*
 * ============LICENSE_START=======================================================
 * oom-certservice-client
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

package org.onap.oom.certservice.client.configuration.model;

import java.util.Objects;

public final class San {

    private final String value;
    private final int type;

    public San(String value, int type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return "{sanValue: " + value + ", type: " + getReadableType(type) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        San san1 = (San) o;
        return type == san1.type &&
            Objects.equals(value, san1.value);
    }

    public int hashCode() {
        return Objects.hash(value, type);
    }

    private String getReadableType(int type) {
        String readableType = "undefined";
        switch (type) {
            case 1: readableType = "rfc822Name"; break;
            case 2: readableType = "dNSName"; break;
            case 6: readableType = "uniformResourceIdentifier"; break;
            case 7: readableType = "iPAddress"; break;
        }
        return readableType;
    }
}
