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

package org.onap.oom.truststoremerger.certification.file.provider;

import org.onap.oom.truststoremerger.certification.file.TruststoreFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TruststoreFilesListProvider {

    private final TruststoreFileFactory truststoreFileFactory;

    public TruststoreFilesListProvider(TruststoreFileFactory truststoreFileFactory) {
        this.truststoreFileFactory = truststoreFileFactory;
    }

    public List<TruststoreFile> getTruststoreFilesList(List<String> truststoreFilePaths, List<String> truststoreFilePasswordPaths) throws PasswordReaderException, TruststoreFileFactoryException {
        List<TruststoreFile> truststoreFilesList = new ArrayList<>();
        Iterator<String> truststorePathsIterator = truststoreFilePaths.iterator();
        Iterator<String> passwordPathsIterator = truststoreFilePasswordPaths.iterator();

        while (truststorePathsIterator.hasNext() && passwordPathsIterator.hasNext()) {
            String truststorePath = truststorePathsIterator.next();
            String passwordPath = passwordPathsIterator.next();

            TruststoreFile truststoreFile = truststoreFileFactory.create(truststorePath, passwordPath);
            truststoreFilesList.add(truststoreFile);
        }

        return truststoreFilesList;
    }
}
