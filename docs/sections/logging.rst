.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Logging
=======

Certification Service API 
--------------------------
To see console Certification Service logs use:

- Docker:

.. code-block:: bash

   docker logs <cert-service-container-name>

- Kubernetes:

.. code-block:: bash

   kubectl logs <cert-service-pod-name>

Console logs contain logs for logging levels from **DEBUG** to **ERROR**.

Certification Service logs for different logging levels are available in the container:

- Docker:

.. code-block:: bash

    docker exec -it <cert-service-container-name> bash

- Kubernetes:

.. code-block:: bash

    kubectl exec -it <cert-service-pod-name> bash

Path to logs:

    /var/log/onap/aaf/certservice

Available log files:

    - audit.log - contains logs for **INFO** logging level
    - debug.log - contains logs for logging levels from **DEBUG** to **ERROR**
    - error.log - contains logs for **ERROR** logging level

User cannot change logging levels.


Certification Service Client
----------------------------
To see console Certification Service Client logs use :

- Docker: 

.. code-block:: bash
   
   docker logs <cert-service-client-container-name>

- Kubernetes: 
  CertService Client is used as init container in other components. In the following example:
    - *<some-component-pod-name>* refers to the component that uses CertService Client as init container
    - *<cert-service-client-init-container-name>* refers to name of init container used by the mentioned component. It can be found by executing *'kubectl descrine pod <some-component-pod-name>'* and looking into 'Init Containers section'

.. code-block:: bash

   kubectl logs <some-component-pod-name> -c <cert-service-client-init-container-name>



| Container stops after execution, so all logs available are printed to console.
| User cannot change logging levels.

Client application exits with following exit codes:


+-------+------------------------------------------------+
| Code  | Information                                    |
+=======+================================================+
| 0     | Success                                        |
+-------+------------------------------------------------+
| 1     | Invalid client configuration                   |
+-------+------------------------------------------------+
| 2     | Invalid CSR configuration                      |
+-------+------------------------------------------------+
| 3     | Fail in key pair generation                    |
+-------+------------------------------------------------+
| 4     | Fail in CSR generation                         |
+-------+------------------------------------------------+
| 5     | CertService HTTP unsuccessful response         |
+-------+------------------------------------------------+
| 6     | Internal HTTP Client connection problem        |
+-------+------------------------------------------------+
| 7     | Fail in PKCS12 conversion                      |
+-------+------------------------------------------------+
| 8     | Fail in Private Key to PEM Encoding            |
+-------+------------------------------------------------+
| 9     | Wrong TLS configuration                        |
+-------+------------------------------------------------+
