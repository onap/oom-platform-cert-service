package org.onap.oom.truststoremerger.certification.file;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

public class PemTruststore extends TruststoreFile {

    public PemTruststore(File truststoreFile) {
        super(truststoreFile);
    }

    @Override
    public List<Certificate> getCertificates() {
        return Collections.emptyList();
    }
}
