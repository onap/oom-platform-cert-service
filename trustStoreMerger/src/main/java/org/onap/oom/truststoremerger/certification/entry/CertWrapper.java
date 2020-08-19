package org.onap.oom.truststoremerger.certification.entry;

import java.security.cert.Certificate;

public interface CertWrapper {
    String getAlias();
    Certificate getCertificate();
}
