package org.onap.oom.truststoremerger.certification.entry;

import java.util.List;

public class PemTruststoreFile extends TruststoreFile {

    public PemTruststoreFile(String filePath, String password) {
        super(filePath,password);
    }

    @Override
    void addCertificates(List<CertWrapper> certificates) {

    }

    @Override
    List<CertWrapper> getCertificates() {
        return null;
    }
}
