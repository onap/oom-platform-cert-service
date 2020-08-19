package org.onap.oom.truststoremerger.certification.entry;

import java.security.cert.Certificate;

public class PemCertWrapper implements CertWrapper {

    private final Certificate certificate;
    private final PemAliasGenerator generator = PemAliasGenerator.getInstance();


    public PemCertWrapper(Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public String getAlias() {
        return generator.getAlias();
    }

    @Override
    public Certificate getCertificate() {
        return this.certificate;
    }
}
