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

package org.onap.aaf.certservice.client.certification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.onap.aaf.certservice.client.certification.conversion.ArtifactsCreator;
import org.onap.aaf.certservice.client.certification.conversion.ConvertedArtifactsCreator;
import org.onap.aaf.certservice.client.certification.conversion.PemArtifactsCreator;

import static org.assertj.core.api.Assertions.assertThat;


class ArtifactsCreatorProviderTest {

    private static final String P12 = "P12";
    private static final String JKS = "JKS";
    private static final String PEM = "PEM";
    private static final String TEST_PATH = "testPath";

    @ParameterizedTest
    @ValueSource(strings = {JKS, P12})
    void artifactsProviderShouldReturnConvertedCreator(String outputType) {

        // when
        ArtifactsCreator artifactsCreator =
                ArtifactsCreatorProvider.get(outputType, TEST_PATH);
        // then
        assertThat(artifactsCreator).isInstanceOf(ConvertedArtifactsCreator.class);
    }

    @Test
    void artifactsProviderShouldReturnPemCreator() {

        // when
        ArtifactsCreator artifactsCreator =
                ArtifactsCreatorProvider.get(PEM, TEST_PATH);
        // then
        assertThat(artifactsCreator).isInstanceOf(PemArtifactsCreator.class);
    }

    @ParameterizedTest
    @CsvSource({
            "JKS,       jks",
            "P12,       p12"})
    void getExtensionShouldProvideExtensionBasedOnArtifactType(String artifactType, String expectedExtension) {

        //when
        String actualExtension = ArtifactsCreatorProvider.valueOf(artifactType).getExtension();
        //then
        assertThat(actualExtension).isEqualTo(expectedExtension);
    }

}
