.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Configuration
=============

Standalone docker container
---------------------------

Certification Service Client image:

.. code-block:: 

  nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest 


1. Create file with environments as in example below.

.. code-block:: 

  #Client envs
  REQUEST_URL=http://aaf-cert-service:8080/v1/certificate/
  REQUEST_TIMEOUT=1000
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


2. Run docker container with environments file and docker network (API and client must be running in same network).

.. code-block:: bash

  AAFCERT_CLIENT_IMAGE=nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest
  DOCKER_ENV_FILE= <path to environment file>
  NETWORK_CERT_SERVICE= <docker network of cert service>
  DOCKER_VOLUME="<absolute path to local dir>:<output path>"

  docker run --env-file $DOCKER_ENV_FILE --network $NETWORK_CERT_SERVICE --volume $DOCKER_VOLUME $AAFCERT_CLIENT_IMAGE



Init Container for K8s
----------------------

Example deployment:

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

 