/*
 * ============LICENSE_START=======================================================
 * Cert Service
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

package org.onap.aaf.certservice.certification;

import org.onap.aaf.certservice.certification.adapter.Cmpv2ClientAdapter;
import org.onap.aaf.certservice.certification.configuration.model.Cmpv2Server;
import org.onap.aaf.certservice.certification.exception.Cmpv2ClientAdapterException;
import org.onap.aaf.certservice.certification.model.CertificationModel;
import org.onap.aaf.certservice.certification.model.CsrModel;
import org.onap.aaf.certservice.cmpv2client.exceptions.CmpClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificationProvider {

    private final Cmpv2ClientAdapter cmpv2ClientAdapter;

    @Autowired
    public  CertificationProvider(Cmpv2ClientAdapter cmpv2ClientAdapter) {
        this.cmpv2ClientAdapter = cmpv2ClientAdapter;
    }

    CertificationModel signCsr(CsrModel csrModel, Cmpv2Server server)
            throws CmpClientException, Cmpv2ClientAdapterException {
        return cmpv2ClientAdapter.callCmpClient(csrModel, server);
    }

}
