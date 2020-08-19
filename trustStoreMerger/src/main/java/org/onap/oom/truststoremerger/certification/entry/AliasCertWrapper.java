package org.onap.oom.truststoremerger.certification.entry;

import java.security.cert.Certificate;

public class AliasCertWrapper implements CertWrapper {

    private final Certificate certificate;
    private final String alias;

    public AliasCertWrapper(Certificate certificate, String alias) {
        this.certificate = certificate;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public Certificate getCertificate() {
        return this.certificate;
    }
}
