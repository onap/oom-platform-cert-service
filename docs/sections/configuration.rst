.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA

Configuration
==============


Configuring Cert Service
------------------------
Cert Service keeps configuration of  CMP Servers in file *cmpServers.json*.

Example cmpServers.json file:

.. code-block:: json

    {
      "cmpv2Servers": [
        {
          "caName": "Client",
          "url": "http://oomcert-ejbca:8080/ejbca/publicweb/cmp/cmp",
          "issuerDN": "CN=ManagementCA",
          "caMode": "CLIENT",
          "authentication": {
            "iak": "mypassword",
            "rv": "mypassword"
          }
        },
        {
          "caName": "RA",
          "url": "http://oomcert-ejbca:8080/ejbca/publicweb/cmp/cmpRA",
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

    - *caName* - name of the external CA server. It's used to match *CA_NAME* sent by CertService client in order to match proper configuration.
    - *url* - URL to CMPv2 server
    - *issuerDN* - Distinguished Name of the CA that will sign the certificate
    - *caMode* - Issuer mode. Allowed values are *CLIENT* and *RA*
    - *authentication*

        - *iak* - Initial authentication key, used to authenticate request in CMPv2 server
        - *rv* - Reference value, used to authenticate request in CMPv2 server



This configuration is read on the application start. It can also be reloaded in runtime, by calling HTTPS endpoint.

Next sections explain how to configure Cert Service in local (docker-compose) and OOM Deployments.


Configuring in local (docker-compose) deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

    e.g.
    docker exec -it oomcert-service bash

3. Edit *cmpServers.json* file::

    vim /etc/onap/oom/certservice/cmpServers.json

4. Save the file. Note that this file is mounted as volume, so change will be persistent.
5. Reload configuration::

    curl -I https://localhost:8443/reload --cacert /etc/onap/oom/certservice/certs/root.crt --cert-type p12 --cert /etc/onap/oom/certservice/certs/certServiceServer-keystore.p12 --pass $KEYSTORE_PASSWORD

6. Exit container::

    exit


Configuring in OOM deployment:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Before OOM installation:
""""""""""""""""""""""""

Note! This must be executed before calling *make all* (from OOM Installation) or needs remaking OOM charts.


1. Edit *cmpServers.json* file. If OOM *global.addTestingComponents* flag is set to:

    - *true* - edit *kubernetes/platform/components/oom-cert-service/resources/test/cmpServers.json*
    - *false* - edit *kubernetes/platform/components/oom-cert-service/resources/default/cmpServers.json

2. Build and start OOM deployment

When CertService is deployed:
"""""""""""""""""""""""""""""

1. Create file with configuration

2. Encode your configuration to base64::

    cat <configuration_file> | base64

3. Edit secret::

    kubectl -n onap edit secret <cmp-servers-secret-name>

    e.g.
    kubectl -n onap edit secret oom-cert-service-secret

4. Replace value for *cmpServers.json* with your base64 encoded configuration. For example:

  .. code-block:: yaml

        apiVersion: v1
        data:
          cmpServers.json: <HERE_PLACE_YOUR_BASE64_ENCODED_CONFIG>
        kind: Secret
        metadata:
          creationTimestamp: "2020-04-21T16:30:29Z"
          name: oom-cert-service-secret
          namespace: default
          resourceVersion: "33892990"
          selfLink: /api/v1/namespaces/default/secrets/oom-cert-service-secret
          uid: 6a037526-83ed-11ea-b731-fa163e2144f6
        type: Opaque

5. Save and exit
6. New configuration will be automatically mounted to CertService pod, but application configuration reload is needed.
7. To reload configuration enter CertService pod::

    kubectl -n onap exec -it <cert-service-pod-name> bash

    e.g.
    kubectl -n onap exec -it $(kubectl -n onap get pods | grep cert-service | awk '{print $1}') bash

8. Reload configuration::

    curl -I https://localhost:$HTTPS_PORT/reload --cacert $ROOT_CERT --cert-type p12 --cert $KEYSTORE_P12_PATH --pass $KEYSTORE_PASSWORD

9. Exit container::

    exit


Generating certificates for CertService and CMPv2 certificate provider
----------------------------------------------------------------------
CertService and CMPv2 certificate provider use mutual TLS for communication. Certificates are generated during CertService installation.

Docker mode:
^^^^^^^^^^^^

Certificates are mounted to containers by docker volumes:

    - CertService volumes are defined in certservice/docker-compose.yaml

All certificates are stored in *certservice/certs* directory. To recreate certificates go to *certservice/certs* directory and execute::

    make clear all

This will clear existing certs and generate new ones.

ONAP OOM installation:
^^^^^^^^^^^^^^^^^^^^^^

Certificates are stored in secrets, which are mounted to pods as volumes. For CMPv2 certificate provider, certificates are delivered in CMPv2Issuer as secrets name with corresponding keys.

Both secrets definitions are stored in *kubernetes/platform/components/oom-cert-service/values.yaml* as *secrets:* key.

During platform component deployment, certificates in secrets are generated automatically using *Certificate* resources from cert-manager.
Their definitions are stored in *kubernetes/platform/components/oom-cert-service/values.yaml* as *certificates:* key.


Using external certificates for CertService and CMPv2 certificate provider
--------------------------------------------------------------------------

This section describes how to use custom, external certificates for CertService and CMPv2 certificate provider communication in OOM installation.

1. Remove *certificates:* section from *kubernetes/platform/components/oom-cert-service/values.yaml*

2. Prepare secret for CertService. It must be provided before OOM installation. It must contain four files:

    - *keystore.jks*  - keystore in JKS format. Signed by some Root CA
    - *keystore.p12* - same keystore in PKCS#12 format
    - *truststore.jks* - truststore in JKS format, containing certificates of the Root CA that signed CertService Client certificate
    - *ca.crt* - certificate of the RootCA that signed Client certificate in CRT format

3. Name the secret properly - the name should match *tls.server.secret.name* value from *kubernetes/platform/components/oom-cert-service/values.yaml* file

4. Prepare secret for CMPv2 certificate provider. It must be provided before OOM installation. It must contain three files:

    - *tls.crt* - certificate in CRT format. Signed by some Root CA
    - *tls.key* - private key in KEY format
    - *ca.crt* - certificate of the RootCA that signed CertService certificate in CRT format

5. Name the secret properly - the name should match *global.oom.certService.client.secret.name* value from *kubernetes/onap/values.yaml* file

6. Provide keystore and truststore passwords (the same for both) for CertService. It can be done in two ways:

    - by inlining them into *kubernetes/platform/components/oom-cert-service/values.yaml*:

        - override *credentials.tls.certificatesPassword* value with keystore and truststore password

    - or by providing them as secrets:

        - uncomment *credentials.tls.certificatesPasswordExternalSecret* value and provide keystore and truststore password


Configuring EJBCA server for testing
------------------------------------

To instantiate an EJBCA server for testing purposes with an OOM deployment, cmpv2Enabled and cmpv2Testing have to be changed to true in oom/kubernetes/platform/values.yaml.

cmpv2Enabled has to be true to enable oom-cert-service to be instantiated and used with an external Certificate Authority to get certificates for secure communication.

If cmpv2Testing is enabled then an EJBCA test server will be instantiated in the OOM deployment as well, and will come pre-configured with a test CA to request a certificate from.

Currently the recommended mode is single-layer RA mode.


Default Values:

+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
|  Name               | Value                                                                                                                           |
+=====================+=================================================================================================================================+
| Request URL         | http://ejbca:8080/ejbca/publicweb/cmp/cmpRA                                                                                 |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| Response Type       | PKI Response                                                                                                                    |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| caMode              | RA                                                                                                                              |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+
| alias               | cmpRA                                                                                                                           |
+---------------------+---------------------------------------------------------------------------------------------------------------------------------+


If you wish to configure the EJBCA server, you can find Documentation for EJBCA here: https://doc.primekey.com/ejbca/

If you want to understand how CMP works on EJBCA in more detail, you can find Details here: https://download.primekey.com/docs/EJBCA-Enterprise/6_14_0/CMP.html

