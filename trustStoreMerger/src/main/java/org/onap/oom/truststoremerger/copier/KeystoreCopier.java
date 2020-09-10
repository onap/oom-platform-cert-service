package org.onap.oom.truststoremerger.copier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.onap.oom.truststoremerger.configuration.model.AppConfiguration;
import org.onap.oom.truststoremerger.copier.exception.KeystoreFileCopyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeystoreCopier {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeystoreCopier.class);

    public void copyKeystores(AppConfiguration configuration) {

        final List<String> sources = configuration.getSourceKeystorePaths();
        final List<String> destinations = configuration.getDestinationKeystorePaths();

        for (int i = 0; i < sources.size(); i++) {
            copy(sources.get(i), destinations.get(i));
        }
    }

    private void copy(String sourcePath, String destinationPath) {
        try {
            final File source = new File(sourcePath);
            final File destination = new File(destinationPath);

            if (source.exists()) {
                if (destination.exists()) {
                    final File backup = new File(destinationPath + ".bak");
                    FileUtils.copyFile(destination, backup);
                    LOGGER.debug("Backup file created '{}'.", backup.getAbsolutePath());
                }
                FileUtils.copyFile(source, destination);
                LOGGER.debug("Keystore copied from '{}' to '{}'.", source.getAbsolutePath(), destination.getAbsolutePath());
            } else {
                LOGGER.warn("Keystore file does not exist '{}' -->  skipping copy operation.", source.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new KeystoreFileCopyException(e);
        }
    }

}
