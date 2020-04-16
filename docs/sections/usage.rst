.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

How to use functionality
========================

Basic information
-----------------
Certification Client needs the following configuration parameters to work properly:

1. Parameters for connection to certification service API and generate trustore and keystore
  
  - REQUEST_URL *(default: https://aaf-cert-service:8443/v1/certificate/)*
  - REQUEST_TIMEOUT *(default: 30000)*
  - OUTPUT_PATH *(required)*
  - CA_NAME *(required)* 


2. Parameters for generate CSR file:
  
  - COMMON_NAME *(required)*
  - ORGANIZATION *(required)*
  - ORGANIZATION_UNIT *(optional)*
  - LOCATION *(optional)*
  - STATE *(required)*
  - COUNTRY *(required)*
  - SANS *(optional)(SANS's should be separated by a colon)*

3. Parameters for secure connection: 

  - KEYSTORE_PATH *(required)*
  - KEYSTORE_PASSWORD *(required)*
  - TRUSTSTORE_PATH *(required)*
  - TRUSTSTORE_PASSWORD *(required)*

Certification Service Client image can be find on Nexus repository :

.. code-block:: bash

  nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest 


As standalone docker container
------------------------------
You need certification files to connect to certification service API via https. Information how to generate truststore and keystore files you can find in project repository README `Gerrit GitWeb <https://gerrit.onap.org/r/gitweb?p=aaf%2Fcertservice.git;a=summary>`__

To run Certification Client as standalone docker container execute following steps: 

1. Create file with environments as in example below:

.. code-block:: bash

  #Client envs
  REQUEST_URL=<url to certification service API>
  REQUEST_TIMEOUT=10000
  OUTPUT_PATH=/var/certs
  CA_NAME=RA
  #Csr config envs
  COMMON_NAME=onap.org
  ORGANIZATION=Linux-Foundation
  ORGANIZATION_UNIT=ONAP
  LOCATION=San-Francisco
  STATE=California
  COUNTRY=US
  SANS=test.onap.org:onap.com
  #Tls config envs
  KEYSTORE_PATH=/etc/onap/aaf/certservice/certs/certServiceClient-keystore.jks
  KEYSTORE_PASSWORD=<password to keystore.jks>
  TRUSTSTORE_PATH=/etc/onap/aaf/certservice/certs/certServiceClient-truststore.jks
  TRUSTSTORE_PASSWORD=<password to certServiceClient-truststore.jks>

2. Run docker container as in following example (API and client must be running in same network):

.. code-block:: bash

 docker run \
    --rm \
    --name aafcert-client \
    --env-file <path to environments file> \
    --network <docker network of cert service> \
    --mount type=bind,src=<path to local directory>,dst=<OUTPUT_PATH> \
    --volume <local path to keystore.jks>:<KEYSTORE_PATH> \
    --volume <local path to trustore.jks>:<TRUSTSTORE_PATH> \
    nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest 



After successful creation of certifications, container exits with exit code 0, expected logs looks like:

.. code-block:: bash

  INFO 1 [           main] o.o.a.c.c.c.f.ClientConfigurationFactory : Successful validation of Client configuration. Configuration data: REQUEST_URL: https://aaf-cert-service:8443/v1/certificate/, REQUEST_TIMEOUT: 10000, OUTPUT_PATH: /var/certs, CA_NAME: RA
  INFO 1 [           main] o.o.a.c.c.c.f.CsrConfigurationFactory    : Successful validation of CSR configuration. Configuration data: COMMON_NAME: onap.org, COUNTRY: US, STATE: California, ORGANIZATION: Linux-Foundation, ORGANIZATION_UNIT: ONAP, LOCATION: San-Francisco, SANS: test.onap.org:onap.org
  INFO 1 [           main] o.o.a.c.c.c.KeyPairFactory               : KeyPair generation started with algorithm: RSA and key size: 2048
  INFO 1 [           main] o.o.a.c.c.c.CsrFactory                   : Creation of CSR has been started with following parameters: COMMON_NAME: onap.org, COUNTRY: US, STATE: California, ORGANIZATION: Linux-Foundation, ORGANIZATION_UNIT: ONAP, LOCATION: San-Francisco, SANS: test.onap.org:onap.org
  INFO 1 [           main] o.o.a.c.c.c.CsrFactory                   : Creation of CSR has been completed successfully
  INFO 1 [           main] o.o.a.c.c.c.CsrFactory                   : Conversion of CSR to PEM has been started
  INFO 1 [           main] o.o.a.c.c.c.PrivateKeyToPemEncoder       : Attempt to encode private key to PEM
  INFO 1 [           main] o.o.a.c.c.h.HttpClient                   : Attempt to send request to API, on url: https://aaf-cert-service:8443/v1/certificate/RA 
  INFO 1 [           main] o.o.a.c.c.h.HttpClient                   : Received response from API
  INFO 1 [           main] o.o.a.c.c.c.c.PemToPKCS12Converter       : Conversion of PEM certificates to PKCS12 keystore
  DEBUG 1 [           main] o.o.a.c.c.c.c.PKCS12FilesCreator         : Attempt to create PKCS12 keystore files and saving data. Keystore path: /var/certs/keystore.jks
  INFO 1 [           main] o.o.a.c.c.c.c.PemToPKCS12Converter       : Conversion of PEM certificates to PKCS12 truststore
  DEBUG 1 [           main] o.o.a.c.c.c.c.PKCS12FilesCreator         : Attempt to create PKCS12 truststore files and saving data. Truststore path: /var/certs/truststore.jks
  INFO 1 [           main] o.o.a.c.c.AppExitHandler                 : Application exits with following exit code: 0 and message: Success


If container exits with non 0 exit code, you can find more information in logs, see :ref:`cert_logs` page.

As init container for Kubernetes
--------------------------------

To run Certification Client as init container for ONAP component, add following configuration to deploymnet:

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
            image: nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest
            imagePullPolicy: Always
            env:
              - name: REQUEST_URL
                value: http://aaf-cert-service:8080/v1/certificate/
              - name: REQUEST_TIMEOUT
                value: "1000"
              - name: OUTPUT_PATH
                value: /var/certs
              - name: CA_NAME
                value: RA
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
                value: test.onap.org:onap.com
            volumeMounts:
              - mountPath: /var/certs
                name: certs
          ...
        volumes: 
          -emptyDir: {}
           name: certs
        ...

 