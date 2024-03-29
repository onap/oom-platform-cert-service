.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA

.. _cmpv2_cert_provider:

How to use functionality
=========================
Common information how to use CMPv2 certificate provider described below

General information
------------------------------

CMPv2 certificate provider is a part of certificate distribution infrastructure in ONAP.
The main functionality of the provider is to forward Certificate Signing Requests (CSRs) created by cert-mananger (https://cert-manager.io) to CertServiceAPI.

Additional information can be found on a dedicated page:  https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration.

By default CMPv2 provider is enabled.

CMPv2 Issuer
------------------------------

In order to be able to request a certificate via CMPv2 provider a *CMPv2Issuer* CRD (Customer Resource Definition) instance has to be created.

It is important to note that the attribute *kind* has to be set to **CMPv2Issuer**, all other attributes can be set as needed.

**NOTE: a default instance of CMPv2Issuer is created when installing ONAP via OOM deployment.**

Here is a definition of a *CMPv2Issuer* provided with ONAP installation:

.. code-block:: yaml

  apiVersion: certmanager.onap.org/v1
  kind: CMPv2Issuer
  metadata:
    name: cmpv2-issuer-onap
    namespace: onap
  spec:
    url: https://oom-cert-service:8443
    healthEndpoint: actuator/health
    certEndpoint: v1/certificate
    updateEndpoint: v1/certificate-update
    caName: RA
    certSecretRef:
      name: cmpv2-issuer-secret
      certRef: cmpv2Issuer-cert.pem
      keyRef: cmpv2Issuer-key.pem
      cacertRef: cacert.pem


Certificate enrolling
------------------------------

In order to request a certificate a K8s *Certificate* CRD (Custom Resource Definition) has to be created.

It is important that in the section issuerRef following attributes have those values:

- group: certmanager.onap.org

- kind: CMPv2Issuer

After *Certificate* CRD has been placed cert manager will send a *CSR* (Certificate Sign Request) to CA (Certificate Authority) via CMPv2 provider.
Signed certificate as well as trust anchor (CA root certificate) will be stored in the K8s *secret* specified in *Certificate* CRD (see secretName attribute).

By default certificates will be stored in PEM format. It is possible to get certificates also in JKS and P12 format - see example below - more information can be found on official cert manager page.

The following SANs types are supported: DNS names, IPs, URIs, emails.

Here is an example of a *Certificate*:

.. code-block:: yaml

  apiVersion: cert-manager.io/v1
  kind: Certificate
  metadata:
    name: certificate_name
    namespace: onap
  spec:
    # The secret name to store the signed certificate
    secretName: secret_name
    # Common Name
    commonName: certissuer.onap.org
    subject:
      organizations:
        - Linux-Foundation
      countries:
        - US
      localities:
        - San-Francisco
      provinces:
        - California
      organizationalUnits:
        - ONAP
    # SANs
    dnsNames:
      - localhost
      - certissuer.onap.org
    ipAddresses:
      - "127.0.0.1"
    uris:
      - onap://cluster.local/
    emailAddresses:
      - onap@onap.org
    # The reference to the CMPv2 issuer
    issuerRef:
      group: certmanager.onap.org
      kind: CMPv2Issuer
      name: cmpv2-issuer-onap
    # Section keystores is optional and defines in which format certificates will be stored
    # If this section is omitted than only PEM format will be present in the secret
    keystores:
        jks:
          create: true
          passwordSecretRef: # Password used to encrypt the keystore
            name: certservice-key
            key: key
        pkcs12:
          create: true
          passwordSecretRef: # Password used to encrypt the keystore
            name: certservice-key
            key: key


Here is an example of generated *secret* containing certificates:

.. code-block:: yaml

    Name:         secret_name
    Namespace:    onap
    Labels:       <none>
    Annotations:  cert-manager.io/alt-names: localhost,certissuer.onap.org
                  cert-manager.io/certificate-name: certificate_name
                  cert-manager.io/common-name: certissuer.onap.org
                  cert-manager.io/ip-sans:
                  cert-manager.io/issuer-group: certmanager.onap.org
                  cert-manager.io/issuer-kind: CMPv2Issuer
                  cert-manager.io/issuer-name: cmpv2-issuer-onap
                  cert-manager.io/uri-sans:

    Type:  kubernetes.io/tls

    Data
    ====
    tls.crt:         1675 bytes  <-- Certificate (PEM)
    tls.key:         1679 bytes  <-- Private Key (PEM)
    truststore.jks:  1265 bytes  <-- Trusted anchors (JKS)
    ca.crt:          1692 bytes  <-- Trusted anchors (PEM)
    keystore.jks:    3786 bytes  <-- Certificate and Private Key (JKS)
    keystore.p12:    4047 bytes  <-- Certificate and Private Key (P12)

.. _how_to_use_certificate_update:

Certificate update
------------------------------

When the certificate already exists, but its date is close to expire or certificate data should be changed, then the certificate update scenario can be executed.
It is performed automatically by cert-manager close to the expiration date or can be triggered manually.
This use case requires the update endpoint configured for *CMPv2Issuer* CRD:

.. code-block:: yaml

  ...
  certEndpoint: v1/certificate
  updateEndpoint: v1/certificate-update
  caName: RA
  ...

If *updateEndpoint* field is not present or empty, then *certEndpoint* will be used (regular initial request instead of update) to get the certificate and this event will be logged.
This behavior comes from releases prior to 2.4.0, when the certificate update feature was not implemented. To be able to perform the certificate update scenario,
make sure the updateEndpoint is present in *CMPv2Issuer* CRD.

There are two possible types of requests when a certificate needs to be updated: Key Update Request (KUR) and Certification Request (CR).
Certification Service internally compares the old and new certificates fields. When they are equal, KUR request is sent.
If there is a difference, the type of request is CR.

There is a difference between CR and KUR in terms of the request authentication. Certificate Request uses IAK/RV mechanism, while KUR uses signature protection.
The old certificate and the old private key are required to be sent in the headers of the update request.
