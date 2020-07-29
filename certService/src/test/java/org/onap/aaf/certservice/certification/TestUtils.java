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

package org.onap.oom.certservice.certification;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.onap.oom.certservice.certification.exception.DecryptionException;
import org.onap.oom.certservice.certification.exception.KeyDecryptionException;
import org.onap.oom.certservice.certification.model.CsrModel;

import java.io.IOException;
import java.io.StringWriter;

import static org.onap.oom.certservice.certification.TestData.TEST_CSR;
import static org.onap.oom.certservice.certification.TestData.TEST_PK;


public final class TestUtils {

    private TestUtils() {
    }

    public static String pemObjectToString(PemObject pemObject) throws KeyDecryptionException {
        try (StringWriter output = new StringWriter()) {
            PemWriter pemWriter = new PemWriter(output);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            return output.getBuffer().toString();

        } catch (IOException e) {
            throw new KeyDecryptionException("Writing PAM Object to string failed", e);
        }
    }

    public static CsrModel createCsrModel() throws DecryptionException {
        CsrModelFactory csrModelFactory = new CsrModelFactory();
        String encoderCsr = new String(Base64.encode(TEST_CSR.getBytes()));
        String encoderPK = new String(Base64.encode(TEST_PK.getBytes()));
        return csrModelFactory
                .createCsrModel(new CsrModelFactory.StringBase64(encoderCsr), new CsrModelFactory.StringBase64(encoderPK));
    }
}
