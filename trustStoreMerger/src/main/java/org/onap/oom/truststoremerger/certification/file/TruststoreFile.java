package org.onap.oom.truststoremerger.certification.file;

import java.io.File;
import java.security.cert.Certificate;
import java.util.List;

public abstract class TruststoreFile {
    private File truststoreFile;

    TruststoreFile(File truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    public abstract List<Certificate> getCertificates();

    public File getTruststoreFile() {
        return truststoreFile;
    };
}
