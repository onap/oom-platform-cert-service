package org.onap.oom.truststoremerger.certification.entry;

public class TruststoreFactory {

    //return Entry object
    TruststoreFile getEntry(TruststoreFile truststoreFile) {

        // return JKS/PKS12/PEM Implementation of trustoreFile .
        //   .pem
        if (truststoreFile.getFilePath().endsWith(".pem")) {
            return new PemTruststoreFile(truststoreFile.getFilePath(), truststoreFile.getPassword());
        } else if (truststoreFile.getFilePath().endsWith(".jks")) {
            return null;
        }
        if (truststoreFile.getFilePath().endsWith(".pkcs12")) {
            return null;
        }

        String extension = truststoreFile.getFilePath();
        return switch (extension) {
            case:
        }

        return null;
    }

}
