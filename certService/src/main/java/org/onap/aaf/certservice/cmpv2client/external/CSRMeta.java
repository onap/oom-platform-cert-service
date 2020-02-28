/**
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
 * ===========================================================================
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
 * ============LICENSE_END====================================================
 *
 */
package org.onap.aaf.certservice.cmpv2client.external;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Certificate;

public class CSRMeta {

    private String cn;
    private String mechID;
    private String environment;
    private String email;
    private String challenge;
    private String issuerCn;
    private String issuerEmail;
    private String password;
    private String CaUrl;
    private List<RDN> rdns;
    private ArrayList<String> sanList = new ArrayList<>();
    private KeyPair keyPair;
    private X500Name name;
    private X500Name issuerName;
    private Certificate certificate;
    private String senderKid;

    public CSRMeta(List<RDN> rdns) {
        this.rdns = rdns;
    }

    public X500Name x500Name() {
        if (name == null) {
            X500NameBuilder xnb = new X500NameBuilder();
            xnb.addRDN(BCStyle.CN, cn);
            xnb.addRDN(BCStyle.E, email);
            if (mechID != null) {
                if (environment == null) {
                    xnb.addRDN(BCStyle.OU, mechID);
                } else {
                    xnb.addRDN(BCStyle.OU, mechID + ':' + environment);
                }
            }
            for (RDN rdn : rdns) {
                xnb.addRDN(rdn.getAoi(), rdn.getValue());
            }
            name = xnb.build();
        }
        return name;
    }

    public X500Name issuerx500Name() {
        if (issuerName == null) {
            X500NameBuilder xnb = new X500NameBuilder();
            xnb.addRDN(BCStyle.CN, issuerCn);
            if (issuerEmail != null) {
                xnb.addRDN(BCStyle.E, issuerEmail);
            }
            issuerName = xnb.build();
        }
        return issuerName;
    }

    public CSRMeta san(String v) {
        sanList.add(v);
        return this;
    }

    public List<String> sans() {
        return sanList;
    }

    public KeyPair keypair() {
        if (keyPair == null) {
            keyPair = Factory.generateKeyPair();
        }
        return keyPair;
    }

    public KeyPair keyPair() {
        return keyPair;
    }

    public void keyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    /** @return the cn */
    public String cn() {
        return cn;
    }

    /** @param cn the cn to set */
    public void cn(String cn) {
        this.cn = cn;
    }

    /** Environment of Service MechID is good for */
    public void environment(String env) {
        environment = env;
    }

    /** @return */
    public String environment() {
        return environment;
    }

    /** @return the mechID */
    public String mechID() {
        return mechID;
    }

    /** @param mechID the mechID to set */
    public void mechID(String mechID) {
        this.mechID = mechID;
    }

    /** @return the email */
    public String email() {
        return email;
    }

    /** @param email the email to set */
    public void email(String email) {
        this.email = email;
    }

    /** @return the challenge */
    public String challenge() {
        return challenge;
    }

    /** @param challenge the challenge to set */
    public void challenge(String challenge) {
        this.challenge = challenge;
    }

    public void password(String password) {
        this.password = password;
    }

    public String password() {
        return password;
    }

    public void certificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate certificate() {
        return certificate;
    }

    public void issuerCn(String issuerCn) {
        this.issuerCn = issuerCn;
    }

    public String caUrl() {
        return CaUrl;
    }

    public void caUrl(String caUrl) {
        CaUrl = caUrl;
    }

    public String senderKid() {
        return senderKid;
    }

    public void senderKid(String senderKid) {
        this.senderKid = senderKid;
    }

    public String issuerCn() {
        return issuerCn;
    }

    public String issuerEmail() {
        return issuerEmail;
    }

    public void issuerEmail(String issuerEmail) {
        this.issuerEmail = issuerEmail;
    }

    public void setIssuerName(X500Name issuerName) {
        this.issuerName = issuerName;
    }

    public void setName(X500Name name) {
        this.name = name;
    }
}
