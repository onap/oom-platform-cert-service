.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Configuration
=============


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

    - *caName* - name of the external CA server. It's used to match *CA_NAME* sent by client in order to match proper configuration.
    - *url* - URL to CMPv2 server
    - *issuerDN* - Distinguished Name of the CA that will sign the certificate
    - *caMode* - Issuer mode. Allowed values are *CLIENT* and *RA*
    - *authentication*

        - *iak* - Initial authentication key, used to authenticate request in CMPv2 server
        - *rv* - Reference value, used to authenticate request in CMPv2 server



This configuration is read on the application start. It can also be reloaded in runtime, by calling HTTPS endpoint.

Next sections explain how to configure Cert Service in local (docker-compose) and OOM Deployments.


Configuring in local(docker-compose) deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Before application start:
"""""""""""""""""""""""""

1. Edit *cmpServers.json* file in certservice/compose-resources
2. Start containers::

    make start-backend

When application is running:
""""""""""""""""""""""""""""

1. Find CertService docker container name.
2. Enter container::

    docker exec -it <certservice-container-name> bash

3. Edit *cmpServers.json* file::

    vim /etc/onap/aaf/certservice/cmpServers.json

4. Save the file. Note that this file is mounted as volume, so change will be persistent.
5. Reload configuration::

    curl -I https://localhost:8443/reload --cacert /etc/onap/aaf/certservice/certs/root.crt --cert-type p12 --cert /etc/onap/aaf/certservice/certs/certServiceServer-keystore.p12 --pass secret

6. Exit container::

    exit


Configuring in OOM deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Before OOM installation:
""""""""""""""""""""""""

Note! This must be executed before calling *make all* (from OOM Installation) or needs remaking aaf Charts.


1. Edit *cmpServers.json* file. If OOM *global.addTestingComponents* flag is set to:

    - *true* - edit *kubernetes/aaf/charts/aaf-cert-service/resources/test/cmpServers.json*
    - *false* - edit *kubernetes/aaf/charts/aaf-cert-service/resources/default/cmpServers.json*

2. Build and start OOM deployment

When CertService is deployed:
"""""""""""""""""""""""""""""

1. Encode your configuration to base64::

    echo "CONFIGURATION_TO_ENCODE" | base64

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
5. New configuration will be automatically mounted to CertService pod, but application configuration reload is needed.
6. To reload configuration enter CertService pod::

    kubectl exec -it <cert-service-pod-name> bash

7. Reload configuration::

    curl -I https://localhost:$HTTPS_PORT/reload --cacert $ROOT_CERT --cert-type p12 --cert $KEYSTORE_P12_PATH --pass $KEYSTORE_PASSWORD

8. Exit container::

    exit


Generating certificates for CertService and CertService Client
--------------------------------------------------------------
CertService and CertService client use mutual TLS for communication. Certificates are generated during CertService installation.

Docker mode:
^^^^^^^^^^^^

Certificates are mounted to containers by docker volumes:

    - CertService volumes are defined in certservice/docker-compose.yaml
    - CertService Client volumes are defined in certservice/Makefile

All certificates are stored in *certservice/certs* directory. To recreate certificates go to *certservice/certs* directory and execute::

    make clear all

This will clear existing certs and generate new ones.

ONAP OOM installation:
^^^^^^^^^^^^^^^^^^^^^^

Certificates are stored in secrets, which are mounted to pods as volumes. Both secrets are stored in *kubernetes/aaf/charts/aaf-cert-service/templates/secret.yaml*.
Secrets take certificates from *kubernetes/aaf/charts/aaf-cert-service/resources* directory. Certificates are generated automatically during building (using Make) OOM repository.

*kubernetes/aaf/charts/aaf-cert-service/Makefile* is similar to the one stored in certservice repository. It actually generates certificates.
This Makefile is executed by *kubernetes/aaf/Makefile*, which is automatically executed during OOM build.


Using external certificates for CertService and CertService Client
------------------------------------------------------------------

This section describes how to use custom, external certificates for CertService and CertService Client communication in OOM installation.

1. Set *tls.certificateExternalSecret* flag to true in *kubernetes/aaf/charts/aaf-cert-service/values.yaml*
2. Prepare secret for CertService. It must be provided before OOM installation. It must contain four files:

    - *certServiceServer-keystore.jks*  - keystore in jks format. Signed by some Root CA
    - *certServiceServer-keystore.p12* - same keystore in p12 format
    - *truststore.jks* - truststore in jks format, containing certificates of the Root CA that signed CertService Client certificate
    - *root.crt* - certificate of the RootCA that signed Client certificate in crt format

3. Name the secret properly - the name should match *tls.server.secret.name* value from *kubernetes/aaf/charts/aaf-cert-service/values.yaml* file

4. Prepare secret for CertService Client. It must be provided before OOM installation. It must contain two files:

    - *certServiceClient-keystore.jks*  - keystore in jks format. Signed by some Root CA
    - *truststore.jks* - truststore in jks format, containing certificates of the RootCA that signed CertService certificate

5. Name the secret properly - the name should match *global.aaf.certService.client.secret.name*

6. Provide keystore and truststore passwords for CertService. It can be done in two ways:

    - by inlining them into *kubernetes/aaf/charts/aaf-cert-service/values.yaml*:

        - override *credentials.tls.keystorePassword* value with keystore password
        - override *credentials.tls.truststorePassword* value with truststore password

    - or by providing them as secrets:

        - uncomment *credentials.tls.keystorePasswordExternalSecret* value and provide keystore password
        - uncomment *credentials.tls.truststorePasswordExternalSecret* value and provide truststore password

7. Override default keystore and truststore passwords for CertService Client in *kubernetes/onap/values.yaml* file:

    - override *global.aaf.certServiceClient.envVariables.keystorePassword* value with keystore password
    - override *global.aaf.certServiceClient.envVariables.truststorePassword* value with truststore password


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

