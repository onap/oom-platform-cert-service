package org.onap.oom.truststoremerger.certification.entry;

import java.security.cert.Certificate;
import java.util.List;

public class PEMMerger extends Merger{

    public PEMMerger(String filePath, String passwordPath) {
        super(filePath, passwordPath);
    }

    @Override
    public void addCertificates(List<Certificate> certEntries) {

    }

    @Override
    public void saveFile() {

    }
}
