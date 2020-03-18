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

package org.onap.aaf.certservice.cmpv2client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Generic Builder Class for creating CMP Message.
 */
public final class CmpMessageBuilder<T> {

    private final Supplier<T> instantiator;
    private final List<Consumer<T>> instanceModifiers = new ArrayList<>();

    public CmpMessageBuilder(Supplier<T> instantiator) {
        this.instantiator = instantiator;
    }

    public static <T> CmpMessageBuilder<T> of(Supplier<T> instantiator) {
        return new CmpMessageBuilder<>(instantiator);
    }

    public <U> CmpMessageBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> valueConsumer = instance -> consumer.accept(instance, value);
        instanceModifiers.add(valueConsumer);
        return this;
    }

    public T build() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        instanceModifiers.clear();
        return value;
    }
}
