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

package org.onap.oom.truststoremerger.certification.model.entry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PemAliasGeneratorTest {

    private final static String PREFIX_ALIAS_NAME = "pem-trusted-certificate-";
    static final int GENERATED_ALIASES_NUMBER = 100;

    @Test
    void aliasHasPemPrefix() {
        //given
        PemAliasGenerator pemAliasGenerator = PemAliasGenerator.getInstance();
        //when
        String alias = pemAliasGenerator.getAlias();
        //then
        assertThat(alias.contains(PREFIX_ALIAS_NAME)).isTrue();
    }

    @Test
    void generatedAliasesHaveUniqNames() {
        //given
        PemAliasGenerator pemAliasGenerator = PemAliasGenerator.getInstance();
        Set<String> aliases = new HashSet<>();

        //when
        for (int i = 0; i < GENERATED_ALIASES_NUMBER; i++) {
            aliases.add(pemAliasGenerator.getAlias());
        }

        //then
        assertThat(aliases).hasSize(GENERATED_ALIASES_NUMBER);
    }

}
