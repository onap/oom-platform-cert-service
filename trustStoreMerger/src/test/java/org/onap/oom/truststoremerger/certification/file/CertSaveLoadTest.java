/*============LICENSE_START=======================================================
 * oom-truststore-merger
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.truststoremerger.certification.file;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.Test;


class CertSaveLoadTest {


    @Test
    void t2() {

    }

    @Test
    void keystoreOpenTest() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust1.jks"),
            "secret".toCharArray());
        System.out.println(Collections.list(ks.aliases()));

        Certificate certificate = ks.getCertificate("jks-alias");
        System.out.println();
        KeyStore ks2 = KeyStore.getInstance("JKS");
        ks2.load(new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust2.jks"),
            "secret".toCharArray());
        System.out.println(Collections.list(ks2.aliases()));
        ks2.setCertificateEntry("newentryalias", certificate);
        System.out.println();

        OutputStream out = new FileOutputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust2_merged.jks");
        ks2.store(out, "secret".toCharArray());


    }

    @Test
    void keystoreSameFileTest() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust1.jks"),
            "secret".toCharArray());
        System.out.println(Collections.list(ks.aliases()));

        Certificate certificate = ks.getCertificate("jks-alias2");
        ks.getCertificateChain("jks-alias2");

//        ks.getCertificateChain()
        System.out.println();
        KeyStore ks2 = KeyStore.getInstance("JKS");
        ks2.load(new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust2_merged.jks"),
            "secret".toCharArray());
        System.out.println(Collections.list(ks2.aliases()));

        //Input Alias, Certificate
        ks2.setCertificateEntry("newentryalias2", certificate);
        System.out.println();

        OutputStream out = new FileOutputStream("/home/twrobel/ONAP/research/oom-certservice/empty/trust2_merged.jks");
        ks2.store(out, "secret".toCharArray());
    }

    @Test
    void loadPkcs12() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/keystore.pkcs12"),
            "secret".toCharArray());
        System.out.println(Collections.list(ks.aliases()));

        ks.getCertificate("myalias");
    }

    @Test
    void loadPEM() throws CertificateException, IOException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory fact = CertificateFactory.getInstance("X.509", "BC");
//        CertificateFactory fact = CertificateFactory.getInstance("X.509","BC");
//        FileInputStream is = new FileInputStream ("/home/twrobel/ONAP/research/oom-certservice/client/truststore.pem");
        FileInputStream is = new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/merged_pem.pem");
//        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
        System.out.println();

        List<X509Certificate> certificateChain = new ArrayList<>();
        Collection collection = fact.generateCertificates(is);
        List<Certificate> cert12 = (List<Certificate>) collection.stream().collect(Collectors.toList());
        List<Certificate> cert13 = (List<Certificate>) new ArrayList<>(collection);

        Iterator it = collection.iterator();
        StringWriter sw = new StringWriter();

        PemWriter pemWriter1 = new PemWriter(sw);
        while (it.hasNext()) {
//            System.out.println("version: " + ((Certificate)it.next()).getType());
            PemObjectGenerator gen = new JcaMiscPEMGenerator(it.next());
            pemWriter1.writeObject(gen);
        }

//        System.out.println(Arrays.asList(collection.toArray()));

//        PEMWriter pemWriter;
//        pemWriter1
        pemWriter1.close();
        System.out.println(sw);


    }

    @Test
    void savePEM() throws CertificateException, IOException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        CertificateFactory fact = CertificateFactory.getInstance("X.509", "BC");
//        CertificateFactory fact = CertificateFactory.getInstance("X.509","BC");
//        FileInputStream is = new FileInputStream ("/home/twrobel/ONAP/research/oom-certservice/client/truststore.pem");
        FileInputStream is = new FileInputStream("/home/twrobel/ONAP/research/oom-certservice/empty/merged_pem.pem");
//        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
        System.out.println();

        List<X509Certificate> certificateChain = new ArrayList<>();
        Collection collection = fact.generateCertificates(is);

        Iterator it = collection.iterator();
        StringWriter sw = new StringWriter();

        PemWriter pemWriter1 = new PemWriter(sw);
        while (it.hasNext()) {
//            System.out.println("version: " + ((Certificate)it.next()).getType());
            PemObjectGenerator gen = new JcaMiscPEMGenerator(it.next());
            pemWriter1.writeObject(gen);
        }

//        System.out.println(Arrays.asList(collection.toArray()));

//        PEMWriter pemWriter;
//        pemWriter1
        pemWriter1.close();
        System.out.println(sw);


    }

    @Test
    void coppyFiles() {
//        Files.copy(null, null,null);
    }


}
