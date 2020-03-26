.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Logging
=======

Certification Service API 
--------------------------


Certification Service logs are available in the Docker container

    docker exec -it aaf-certservice-api bash

Path to logs:

    /var/log/onap/aaf/certservice

Available log files:
    * audit.log
    * debug.log
    * error.log


Certification Service Client
----------------------------
To see logs use :

- Docker: 

.. code-block:: bash
   
   docker logs cert-service-client

- Kubernetes: 
  
.. code-block:: bash
   
   kubectl logs <pod-name> cert-service-client


Logs are stored inside container log path:

  /var/logs

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
