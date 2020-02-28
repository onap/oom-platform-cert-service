/*
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
    private String caUrl;
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

    public X500Name getX500Name() {
        if (name == null) {
            X500NameBuilder nameBuilder = new X500NameBuilder();
            nameBuilder.addRDN(BCStyle.CN, cn);
            nameBuilder.addRDN(BCStyle.E, email);
            if (mechID != null) {
                if (environment == null) {
                    nameBuilder.addRDN(BCStyle.OU, mechID);
                } else {
                    nameBuilder.addRDN(BCStyle.OU, mechID + ':' + environment);
                }
            }
            for (RDN rdn : rdns) {
                nameBuilder.addRDN(rdn.getAoi(), rdn.getValue());
            }
            name = nameBuilder.build();
        }
        return name;
    }

    public X500Name getIssuerX500Name() {
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

    public void addSan(String v) {
        sanList.add(v);
    }

    public List<String> getSans() {
        return sanList;
    }

    public KeyPair getKeyPairOrGenerateIfNull() {
        if (keyPair == null) {
            keyPair = Factory.generateKeyPair();
        }
        return keyPair;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setEnvironment(String env) {
        environment = env;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getMechID() {
        return mechID;
    }

    public void setMechID(String mechID) {
        this.mechID = mechID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setIssuerCn(String issuerCn) {
        this.issuerCn = issuerCn;
    }

    public String getCaUrl() {
        return caUrl;
    }

    public void setCaUrl(String caUrl) {
        this.caUrl = caUrl;
    }

    public String getSenderKid() {
        return senderKid;
    }

    public void setSenderKid(String senderKid) {
        this.senderKid = senderKid;
    }

    public String getIssuerCn() {
        return issuerCn;
    }

    public String getIssuerEmail() {
        return issuerEmail;
    }

    public void setIssuerEmail(String issuerEmail) {
        this.issuerEmail = issuerEmail;
    }

    public void setIssuerName(X500Name issuerName) {
        this.issuerName = issuerName;
    }

    public void setName(X500Name name) {
        this.name = name;
    }
}
