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


Configuring Cert Service
------------------------
Cert Service keeps configuration of  CMP Servers in file *cmpServers.json*.

Example cmpServers.json file:

.. code-block:: json

    {
      "cmpv2Servers": [
        {
          "caName": "Client",
          "url": "http://aafcert-ejbca:8080/ejbca/publicweb/cmp/cmp",
          "issuerDN": "CN=ManagementCA",
          "caMode": "CLIENT",
          "authentication": {
            "iak": "mypassword",
            "rv": "mypassword"
          }
        },
        {
          "caName": "RA",
          "url": "http://aafcert-ejbca:8080/ejbca/publicweb/cmp/cmpRA",
          "issuerDN": "CN=ManagementCA",
          "caMode": "RA",
          "authentication": {
            "iak": "mypassword",
            "rv": "mypassword"
          }
        }
      ]
    }

This contains list of CMP Servers, where each server has following properties:

    - *caName* - name of the external CA server
    - *url* - Url to CMPv2 server
    - *issuerDN* - Distinguished Name of the CA that will sign the certificate
    - *caMode* - Issuer mode
    - *authentication*

        - *iak* - Initial authentication key, used to authenticate request in CMPv2 server
        - *rv* - Reference values, used ti authenticate request in CMPv2 server



This configuration is read on the application start. It can also be reloaded in runtime, by calling HTTP endpoint.


Configuring in local(docker-compose) deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Static:
"""""""

1. Edit *cmpServers.json* file in certservice/compose-resources
2. Start containers::

    make start-backend

Dynamic:
""""""""

1. Find CertService docker container name.
2. Enter container::

    docker exec -it <certservice-container-name> bash

3. Edit *cmpServers.json* file::

    vim /etc/onap/aaf/certservice/cmpServers.json

4. Save
5. Reload configuration::

    curl -I https://localhost:8443/reload --cacert /etc/onap/aaf/certservice/certs/root.crt --cert-type p12 --cert /etc/onap/aaf/certservice/certs/certServiceServer-keystore.p12 --pass secret


Configuring in OOM deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Static:
"""""""

*Note! This must be executed before calling make all or needs remaking aaf Charts*

1. Edit *cmpServers.json* file

   - if it's test deployment - edit *kubernetes/aaf/charts/aaf-cert-service/resources/test/cmpServers.json*
   - if it's normal deployment - edit *kubernetes/aaf/charts/aaf-cert-service/resources/default/cmpServers.json*

2. Build and start OOM deployment

Dynamic:
""""""""

1. Encode your configuration to base64 (You can use for example online encoders or command line tool *base64*)
2. Edit secret::

    kubectl edit secret <cmp-servers-secret-name> # aaf-cert-service-secret by default

3. Replace value for *cmpServers.json* with your base64 encoded configuration. For example:

  .. code-block:: yaml

        apiVersion: v1
        data:
          cmpServers.json: <HERE_PLACE_YOUR_BASE64_ENCODED_CONFIG>
        kind: Secret
        metadata:
          creationTimestamp: "2020-04-21T16:30:29Z"
          name: aaf-cert-service-secret
          namespace: default
          resourceVersion: "33892990"
          selfLink: /api/v1/namespaces/default/secrets/aaf-cert-service-secret
          uid: 6a037526-83ed-11ea-b731-fa163e2144f6
        type: Opaque

4. Save and exit
5. New configuration will be automatically mounted to CertService pod, but reload is needed.
6. Enter CertService pod::

    kubectl exec -it <cert-service-pod-name> bash

7. Reload configuration::

    curl -I https://localhost:$HTTPS_PORT/reload --cacert $ROOT_CERT --cert-type p12 --cert $KEYSTORE_P12_PATH --pass $KEYSTORE_PASSWORD


Configuring EJBCA server for testing
------------------------------------

To instantiate an EJBCA server for testing purposes with an OOM deployment, cmpv2Enabled and cmpv2Testing have to be changed to true in oom/kubernetes/aaf/values.yaml.

cmpv2Enabled has to be true to enable aaf-cert-service to be instantiated and used with an external Certificate Authority to get certificates for secure communication.

If cmpv2Testing is enabled then an EJBCA test server will be instantiated in the OOM deployment as well, and will come pre-configured with a test CA to request a certificate from.

Currently the recommended mode is single-layer RA mode.


Default Values:

+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
|  Name               | Value                                                                                                                           |
+=====================+=================================================================================================================================+
| Request URL         | http://aaf-ejbca:8080/ejbca/publicweb/cmp/cmpRA                                                                                 |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| Response Type       | PKI Response                                                                                                                    |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| caMode              | RA                                                                                                                              |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| alias               | cmpRA                                                                                                                           |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+


If you wish to configure the EJBCA server, you can find Documentation for EJBCA here: https://doc.primekey.com/ejbca/

If you want to understand how CMP works on EJBCA in more detail, you can find Details here: https://download.primekey.com/docs/EJBCA-Enterprise/6_14_0/CMP.html

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
            volumeMounts
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

 