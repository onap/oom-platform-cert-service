/*============LICENSE_START=======================================================
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

package org.onap.oom.certservice.client.certification;

import org.onap.oom.certservice.client.certification.conversion.ArtifactsCreator;
import org.onap.oom.certservice.client.certification.conversion.ConvertedArtifactsCreatorFactory;
import org.onap.oom.certservice.client.certification.conversion.PemArtifactsCreator;
import org.onap.oom.certservice.client.certification.writer.CertFileWriter;

public enum ArtifactsCreatorProvider {
    P12("PKCS12") {
        @Override
        ArtifactsCreator create(String destPath) {
            return ConvertedArtifactsCreatorFactory.createConverter(destPath, getExtension(), getKeyStoreType());
        }
    },
    JKS("JKS") {
        @Override
        ArtifactsCreator create(String destPath) {
            return ConvertedArtifactsCreatorFactory.createConverter(destPath, getExtension(), getKeyStoreType());
        }
    },
    PEM("PEM") {
        @Override
        ArtifactsCreator create(String destPath) {
            return new PemArtifactsCreator(CertFileWriter.createWithDir(destPath), new PrivateKeyToPemEncoder());
        }
    };

    private final String keyStoreType;

    ArtifactsCreatorProvider(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public static ArtifactsCreator get(String outputType, String destPath) {
        return valueOf(outputType).create(destPath);
    }

    String getKeyStoreType() {
        return keyStoreType;
    }

    String getExtension() {
        return this.toString().toLowerCase();
    }

    abstract ArtifactsCreator create(String destPath);
}
