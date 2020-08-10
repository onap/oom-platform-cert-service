package org.onap.oom.truststoremerger.certification.file;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;

public class JksTruststore extends TruststoreFileWithPassword {

    public JksTruststore(File truststoreFile, String password) {
        super(truststoreFile, password);
    }

    @Override
    public List<Certificate> getCertificates() {
        return Collections.emptyList();
    }
}
