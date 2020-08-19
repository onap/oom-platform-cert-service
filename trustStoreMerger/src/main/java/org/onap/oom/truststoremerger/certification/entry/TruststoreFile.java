package org.onap.oom.truststoremerger.certification.entry;

import java.util.List;

public abstract class TruststoreFile {

    private final String filePath;
    private final String password; // password

    public TruststoreFile(String filePath, String password) {
        this.filePath = filePath;
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPassword() {
        return password;
    }



    abstract void addCertificates(List<CertWrapper> certificates );


    abstract List<CertWrapper> getCertificates();
    // return wrapper - Certifact + alias
    // Wrapper PEM  + Wrapper JKS
    //Wraperze PEM -> (inject) sigleton name
    // Singleton PEM NAME
}
