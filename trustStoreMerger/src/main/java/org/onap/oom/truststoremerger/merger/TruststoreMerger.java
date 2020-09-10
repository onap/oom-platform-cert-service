package org.onap.oom.truststoremerger.merger;

import java.util.List;
import org.onap.oom.truststoremerger.api.ExitableException;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.merger.model.Truststore;
import org.onap.oom.truststoremerger.merger.model.certificate.CertificateWithAlias;

public class TruststoreMerger {

    private static final int FIRST_TRUSTSTORE_INDEX = 0;
    private static final int SECOND_TRUSTSTORE_INDEX = 1;

    public void mergeTruststores(AppConfiguration configuration) throws ExitableException {
        List<Truststore> truststoreFilesList = getTruststoreFiles(configuration);

        Truststore baseFile = truststoreFilesList.get(FIRST_TRUSTSTORE_INDEX);
        baseFile.createBackup();

        for (int i = SECOND_TRUSTSTORE_INDEX; i < truststoreFilesList.size(); i++) {
            Truststore truststore = truststoreFilesList.get(i);
            List<CertificateWithAlias> certificateWrappers = truststore.getCertificates();
            baseFile.addCertificates(certificateWrappers);
        }

        baseFile.saveFile();
    }

    private static List<Truststore> getTruststoreFiles(AppConfiguration configuration) throws ExitableException {
        return TruststoreFilesProvider
            .getTruststoreFiles(
                configuration.getTruststoreFilePaths(),
                configuration.getTruststoreFilePasswordPaths()
            );
    }
}
