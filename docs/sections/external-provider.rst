.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

K8s external provider
==============================

General information
------------------------------

Cert Service K8s external provider ia a part of certificate distribution infrastructure in ONAP.
The main functionality of the provider is to forward Certificate Signing Requests (CSRs) created by cert-mananger (https://cert-manager.io) to CertServiceAPI.

Additional information can found on a dedicated page:  https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration.


CMPv2 Issuer
------------------------------

In order to be able to request a certificate via K8s external provider a *CMPv2Issuer* CRD (Customer Resource Definition) instance has to be created.

It is important to note that the attribute *kind* has to be set to **CMPv2Issuer**, all other attributes can be set as needed.

NOTE: a default instance of CMPv2Issuer is created when installing ONAP via OOM deployment (values can also be adjusted as needed)

Here an example of a *CMPv2Issuer*:

.. code-block:: yaml

  apiVersion: certmanager.onap.org/v1
  kind: CMPv2Issuer
  metadata:
    name: cmpv2-issuer
    namespace: onap
  spec:
    url: https://oom-cert-service:8443
    healthEndpoint: actuator/health
    certEndpoint: v1/certificate
    caName: RA
    certSecretRef:
      name: cmpv2-issuer-secret
      certRef: cmpv2Issuer-cert.pem
      keyRef: cmpv2Issuer-key.pem
      cacertRef: cacert.pem

Certificate enrolling
------------------------------

In order to request a certificate a K8s *Certificate* CRD (Custom Resource Definition) has to be created.

It is important that int the section issuerRef following attributes have correct values:
  - group: **certmanager.onap.org**
  - kind: **CMPv2Issuer**

After *Certificate* CRD has been placed cert manager will send a *CSR* (Certificate Sign Request) to CA (Certificate Authority) via K8s external provider.
Signed certificate as well as trust anchor (CA root certificate) will stored in the K8s *secret* specified in *Certificate* CRD (see secretName attribute).

Here an example of a *Certificate*:

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
    # DNS SAN
    dnsNames:
      - localhost
      - certissuer.onap.org
    # The reference to the CMPv2 issuer
    issuerRef:
      group: certmanager.onap.org
      kind: CMPv2Issuer
      name: cmpv2-issuer




