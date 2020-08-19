package org.onap.oom.truststoremerger.certification.entry;

import java.security.cert.Certificate;
import java.util.List;

public abstract class Merger {

    String filePath;
    String passwordPath;

    public Merger(String filePath, String passwordPath) {
        this.filePath = filePath;
        this.passwordPath = passwordPath;
    }

    public abstract void addCertificates(List<Certificate> certEntries);
    public abstract void saveFile();
}
