/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
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

package cmpv2provisioner

import (
	"bytes"
	"context"
	cmapi "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"io/ioutil"
	apimach "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"log"
	"onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	"testing"
	"time"
)

func Test_shouldCreateCorrectCertServiceCA(t *testing.T){
	issuer, key := createIssuerAndKey("cmpv2-issuer", "issuer/url", "onapwro")
	provisioner, err := New(&issuer, key)

	if(err != nil) {
		t.Log("Could not create provisioner.", err)
		t.FailNow()
	}

	if(string(provisioner.key) != string(key)){
		t.Log("Unexpected provisioner key.")
		t.FailNow()
	}

	if(provisioner.name != issuer.Name){
		t.Log("Unexpected provisioner name.")
		t.FailNow()
	}

	if(provisioner.url != issuer.Spec.URL){
		t.Log("Unexpected provisioner url.")
		t.FailNow()
	}
}

func Test_shouldSuccessfullyLoadPreviouslyStoredProvisioner(t *testing.T){
	issuer, key := createIssuerAndKey("cmpv2-issuer", "issuer/url", "onapwro")
	provisioner, err := New(&issuer, key)

	if(err != nil) {
		t.Log("Could not create provisioner.", err)
		t.FailNow()
	}

	issuerNamespaceName := createIssuerNamespaceName("onap", "cmpv2")

	Store(issuerNamespaceName, provisioner)
	provisioner, ok := Load(issuerNamespaceName)

	if(!ok) {
		t.Log("Provisioner could not be loaded.")
		t.FailNow()
	}

	if(string(provisioner.key) != string(key)){
		t.Log("Unexpected provisioner key.")
		t.FailNow()
	}

	if(provisioner.name != issuer.Name){
		t.Log("Unexpected provisioner name.")
		t.FailNow()
	}

	if(provisioner.url != issuer.Spec.URL){
		t.Log("Unexpected provisioner url.")
		t.FailNow()
	}
}

func Test_shouldReturnCorrectSignedPemsWhenParametersAreCorrect(t *testing.T) {
	issuer, key := createIssuerAndKey("cmpv2-issuer", "issuer/url", "onapwro")

	provisioner, err := New(&issuer, key)
	issuerNamespaceName := createIssuerNamespaceName("onap", "cmpv2")
	Store(issuerNamespaceName, provisioner)

	provisioner, ok := Load(issuerNamespaceName)

	if(!ok){
		t.Log("Provisioner could not be loaded")
		t.FailNow()
	}

	ctx := context.Background()
	request := createCertificateRequest()

	signedPEM, trustedCAs, err := provisioner.Sign(ctx, request)

	if(err != nil){
		t.Log(err)
		t.FailNow()
	}

	if(!areSlicesEqual(signedPEM, readFile("expected_signed.pem"))){
		t.Log("Signed pem is different than expected.")
		t.FailNow()
	}
	if(!areSlicesEqual(trustedCAs, readFile("expected_trusted.pem"))){
		t.Log("Trusted CAs pem is different than expected.")
		t.FailNow()
	}
}

func createIssuerNamespaceName(namespace string, name string) types.NamespacedName{
	return types.NamespacedName{
		Namespace: namespace,
		Name:      name,
	}
}

func createIssuerAndKey(name string, url string, key string) (cmpv2api.CMPv2Issuer, []byte) {
	issuer := cmpv2api.CMPv2Issuer{}
	issuer.Name = name
	issuer.Spec.URL = url
	return issuer, []byte(key)
}

func readFile(filename string) []byte{
	certRequest, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}
	return certRequest
}

func createCertificateRequest() *cmapi.CertificateRequest {
	duration := new(apimach.Duration)
	d, _ := time.ParseDuration("1h")
	duration.Duration = d

	request := new(cmapi.CertificateRequest)
	request.Spec.Duration = duration

	request.Spec.IssuerRef.Name = "cmpv2-issuer"
	request.Spec.IssuerRef.Kind = "CMPv2Issuer"
	request.Spec.IssuerRef.Group = "certmanager.onap.org"

	request.Spec.Request = readFile("test_certificate_request.pem")

	request.Spec.IsCA = true

	cond := new(cmapi.CertificateRequestCondition)
	cond.Type = "Ready"
	request.Status.Conditions = []cmapi.CertificateRequestCondition{*cond}

	request.Status.Certificate = readFile("test_certificate.pem")

	return request
}

func areSlicesEqual(slice1 []byte, slice2 []byte) bool{
	return bytes.Compare(slice1, slice2) == 0
}
