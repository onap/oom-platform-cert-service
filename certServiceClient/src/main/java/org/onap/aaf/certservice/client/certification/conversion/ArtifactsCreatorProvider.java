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
package org.onap.aaf.certservice.client.certification.conversion;

import org.onap.aaf.certservice.client.certification.exception.CertOutputTypeNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public enum ArtifactsCreatorProvider {

    P12 {
        @Override
        ArtifactsCreator create(String outputPath) {
            return new PKCS12ArtifactsCreator(
                    new PKCS12FilesCreator(outputPath),
                    new RandomPasswordGenerator(),
                    new PemToPKCS12Converter());
        }
    },
    JKS {
        @Override
        ArtifactsCreator create(String outputPath) {
            return null;
        }
    },
    PEM {
        @Override
        ArtifactsCreator create(String outputPath) {
            return null;
        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactsCreatorProvider.class);

    public static ArtifactsCreator getCreator(String outputType, String outputPath)
            throws CertOutputTypeNotSupportedException {
        try {
            LOGGER.info("Artifact creation type selected: {}", outputType);
            return valueOf(outputType).create(outputPath);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Artifact creation type: {} is not supported. Supported types: {}",
                    outputType, Arrays.toString(values()));
            throw new CertOutputTypeNotSupportedException(e);
        }
    }

    abstract ArtifactsCreator create(String outputPath);
}
