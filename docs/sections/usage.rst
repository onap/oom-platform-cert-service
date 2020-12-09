.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

How to use functionality
=========================
Common information to docker and Kubernetes modes described below

Basic information
-----------------
CertService client needs the following configuration parameters to work properly:

1. Parameters for generating certification artifacts and connecting to CertService API to obtain certificate and trust anchors
  
  - REQUEST_URL *(default: https://oom-cert-service:8443/v1/certificate/)* - URL to CertService API
  - REQUEST_TIMEOUT *(default: 30000[ms])* - Timeout in milliseconds for REST API calls
  - OUTPUT_PATH *(required)* - Path where client will output generated certificate and trust anchor
  - CA_NAME *(required)* - Name of CA which will enroll certificate. Must be same as configured on server side. Used in REST API calls
  - OUTPUT_TYPE *(default: P12)* - Type of certificate which will be generated. Supported types: 
      
      - JKS - Java KeyStore (JKS)
      - P12 - Public Key Cryptography Standard #12 (PKCS#12)
      - PEM - Privacy-Enhanced Mail (PEM)


2. Parameters to generate Certificate Signing Request (CSR):
  
  - COMMON_NAME *(required)* - Common name for which certificate from CMPv2 server should be issued
  - ORGANIZATION *(required)* - Organization for which certificate from CMPv2 server should be issued
  - ORGANIZATION_UNIT *(optional)* - Organization unit for which certificate from CMPv2 server should be issued
  - LOCATION *(optional)* - Location for which certificate from CMPv2 server should be issued
  - STATE *(required)* - State for which certificate from CMPv2 server should be issued
  - COUNTRY *(required)* - Country for which certificate from CMPv2 server should be issued
  - SANS *(optional)(SANS's should be separated by a comma e.g. test.onap.org,onap.com)* - Subject Alternative Names (SANs) for which certificate from CMPv2 server should be issued. The following SANs types are supported: DNS names, IPs, URIs, emails.

3. Parameters to establish secure communication to CertService:

  - KEYSTORE_PATH *(required)*
  - KEYSTORE_PASSWORD *(required)*
  - TRUSTSTORE_PATH *(required)*
  - TRUSTSTORE_PASSWORD *(required)*

CertService client image can be found on Nexus repository :

.. code-block:: bash

  nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:$VERSION


As standalone docker container
------------------------------
You need certificate and trust anchors to connect to CertService API via HTTPS. Information how to generate truststore and keystore files you can find in project repository README `Gerrit GitWeb <https://gerrit.onap.org/r/gitweb?p=oom%2Fplatform%2Fcert-service.git;a=summary>`__

To run CertService client as standalone docker container execute following steps:

1. Create file '*$PWD/client.env*' with environment variables as in example below:

.. code-block:: bash

  #Client envs
  REQUEST_URL=<URL to CertService API>
  REQUEST_TIMEOUT=10000
  OUTPUT_PATH=/var/certs
  CA_NAME=RA
  OUTPUT_TYPE=P12

  #CSR config envs
  COMMON_NAME=onap.org
  ORGANIZATION=Linux-Foundation
  ORGANIZATION_UNIT=ONAP
  LOCATION=San-Francisco
  STATE=California
  COUNTRY=US
  SANS=test.onap.org,onap.com,onap@onap.org,127.0.0.1,onap://cluster.local/

  #TLS config envs
  KEYSTORE_PATH=/etc/onap/oom/certservice/certs/certServiceClient-keystore.jks
  KEYSTORE_PASSWORD=<password to certServiceClient-keystore.jks>
  TRUSTSTORE_PATH=/etc/onap/oom/certservice/certs/certServiceClient-truststore.jks
  TRUSTSTORE_PASSWORD=<password to certServiceClient-truststore.jks>

2. Run docker container as in following example (API and client must be running in same network):

.. code-block:: bash

 docker run \
    --rm \
    --name oomcert-client \
    --env-file <$PWD/client.env (same as in step1)> \
    --network <docker network of cert service> \
    --mount type=bind,src=<path to local host directory where certificate and trust anchor will be created>,dst=<OUTPUT_PATH (same as in step 1)> \
    --volume <local path to keystore in JKS format>:<KEYSTORE_PATH> \
    --volume <local path to truststore in JKS format>:<TRUSTSTORE_PATH> \
    nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:$VERSION



After successful creation of certifications, container exits with exit code 0, expected log looks like:

.. code-block:: bash

   INFO 1 [           main] o.o.o.c.c.c.f.ClientConfigurationFactory : Successful validation of Client configuration. Configuration data: REQUEST_URL: https://oom-cert-service:8443/v1/certificate/, REQUEST_TIMEOUT: 10000, OUTPUT_PATH: /var/certs, CA_NAME: RA, OUTPUT_TYPE: P12
   INFO 1 [           main] o.o.o.c.c.c.f.CsrConfigurationFactory    : Successful validation of CSR configuration. Configuration data: COMMON_NAME: onap.org, COUNTRY: US, STATE: California, ORGANIZATION: Linux-Foundation, ORGANIZATION_UNIT: ONAP, LOCATION: San-Francisco, SANS: [{SAN value: example.org, type: dNSName}, {SAN value: test.onap.org, type: dNSName}, {SAN value: onap@onap.org, type: rfc822Name}, {SAN value: 127.0.0.1, type: iPAddress}, {SAN value: onap://cluster.local/, type: uniformResourceIdentifier}]
   INFO 1 [           main] o.o.o.c.c.c.KeyPairFactory               : KeyPair generation started with algorithm: RSA and key size: 2048
   INFO 1 [           main] o.o.o.c.c.c.CsrFactory                   : Creation of CSR has been started with following parameters: COMMON_NAME: onap.org, COUNTRY: US, STATE: California, ORGANIZATION: Linux-Foundation, ORGANIZATION_UNIT: ONAP, LOCATION: San-Francisco, SANS: [{SAN value: example.org, type: dNSName}, {SAN value: test.onap.org, type: dNSName}, {SAN value: onap@onap.org, type: rfc822Name}, {SAN value: 127.0.0.1, type: iPAddress}, {SAN value: onap://cluster.local/, type: uniformResourceIdentifier}]
   INFO 1 [           main] o.o.o.c.c.c.CsrFactory                   : Creation of CSR has been completed successfully
   INFO 1 [           main] o.o.o.c.c.c.CsrFactory                   : Conversion of CSR to PEM has been started
   INFO 1 [           main] o.o.o.c.c.c.PrivateKeyToPemEncoder       : Attempt to encode private key to PEM
   INFO 1 [           main] o.o.o.c.c.h.HttpClient                   : Attempt to send request to API, on url: https://oom-cert-service:8443/v1/certificate/RA
   INFO 1 [           main] o.o.o.c.c.h.HttpClient                   : Received response from API
  DEBUG 1 [           main] o.o.o.c.c.c.c.ConvertedArtifactsCreator  : Attempt to create keystore files and saving data. File names: keystore.p12, keystore.pass
   INFO 1 [           main] o.o.o.c.c.c.c.PemConverter               : Conversion of PEM certificates to PKCS12 keystore
  DEBUG 1 [           main] o.o.o.c.c.c.w.CertFileWriter             : Attempt to save file keystore.p12 in path /var/certs
  DEBUG 1 [           main] o.o.o.c.c.c.w.CertFileWriter             : Attempt to save file keystore.pass in path /var/certs
  DEBUG 1 [           main] o.o.o.c.c.c.c.ConvertedArtifactsCreator  : Attempt to create truststore files and saving data. File names: truststore.p12, truststore.pass
   INFO 1 [           main] o.o.o.c.c.c.c.PemConverter               : Conversion of PEM certificates to PKCS12 truststore
  DEBUG 1 [           main] o.o.o.c.c.c.w.CertFileWriter             : Attempt to save file truststore.p12 in path /var/certs
  DEBUG 1 [           main] o.o.o.c.c.c.w.CertFileWriter             : Attempt to save file truststore.pass in path /var/certs
   INFO 1 [           main] o.o.o.c.c.AppExitHandler                 : Application exits with following exit code: 0 and message: Success




If container exits with non 0 exit code, you can find more information in logs, see :ref:`cert_logs` page.

As init container for Kubernetes
--------------------------------

In order to run CertService client as init container for ONAP component you need to:

    - define an init container and use CerService Client image
    - provide client configuration through ENV variables in the init container
    - define two volumes:

        - first for generated certificates - it will be mounted in the init container and in the component container
        - second with secret containing keys and certificates for secure communication between CertService Client and CertService - it will be mounted only in the init container
    - mount both volumes to the init container
    - mount first volume to the component container

You can use the following deployment example as a reference:

.. code-block:: yaml

    ...
  kind: Deployment
  metadata:
    ...
  spec:
  ...
    template:
    ...
      spec:
        containers:
          - image: sample.image
            name: sample.name 
            ...
            volumeMounts:
              - mountPath: /var/certs #CERTS CAN BE FOUND IN THIS DIRECTORY
                name: certs
            ...
        initContainers:
          - name: cert-service-client
            image: nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:latest
            imagePullPolicy: Always
            env:
              - name: REQUEST_URL
                value: https://oom-cert-service:8443/v1/certificate/
              - name: REQUEST_TIMEOUT
                value: "1000"
              - name: OUTPUT_PATH
                value: /var/certs
              - name: CA_NAME
                value: RA
              - name: OUTPUT_TYPE
                value: P12
              - name: COMMON_NAME
                value: onap.org
              - name: ORGANIZATION
                value: Linux-Foundation
              - name: ORGANIZATION_UNIT
                value: ONAP
              - name: LOCATION
                value: San-Francisco
              - name: STATE
                value: California
              - name: COUNTRY
                value: US
              - name: SANS
                value: test.onap.org,onap.com,onap@onap.org,127.0.0.1,onap://cluster.local/
              - name: KEYSTORE_PATH
                value: /etc/onap/oom/certservice/certs/certServiceClient-keystore.jks
              - name: KEYSTORE_PASSWORD
                value: secret
              - name: TRUSTSTORE_PATH
                value: /etc/onap/oom/certservice/certs/truststore.jks
              - name: TRUSTSTORE_PASSWORD
                value: secret
            volumeMounts:
              - mountPath: /var/certs
                name: certs
              - mountPath: /etc/onap/oom/certservice/certs/
                name: tls-volume
          ...
        volumes: 
        - name: certs
          emptyDir: {}
        - name tls-volume
          secret:
            secretName: oom-cert-service-client-tls-secret  # Value of global.oom.certService.client.secret.name
        ...

