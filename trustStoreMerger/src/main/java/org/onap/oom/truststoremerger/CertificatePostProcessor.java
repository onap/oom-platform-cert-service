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

package org.onap.oom.truststoremerger;

import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.common.FileTools;
import org.onap.oom.truststoremerger.configuration.AppConfigurationLoader;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.copier.KeystoreCopier;
import org.onap.oom.truststoremerger.merger.TruststoreMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CertificatePostProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CertificatePostProcessor.class);

    private AppConfigurationLoader config = new AppConfigurationLoader();
    private TruststoreMerger merger = new TruststoreMerger();
    private KeystoreCopier copier = new KeystoreCopier(new FileTools());

    public void run() throws ExitableException {
        LOG.debug("Loading configuration...");
        AppConfiguration configuration = config.loadConfiguration();
        LOG.debug("Starting TruststoreMerger...");
        merger.mergeTruststores(configuration);
        LOG.debug("Starting KeystoreCopier...");
        copier.copyKeystores(configuration);
        LOG.debug("Certificate post processing finished successfully.");
    }
}
