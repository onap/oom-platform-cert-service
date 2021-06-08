.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA


Logging
=======

CertService API
---------------
To see CertService console logs use:

- Docker:

.. code-block:: bash

    docker logs <cert-service-container-name>

    e.g.
    docker logs oomcert-service

- Kubernetes:

.. code-block:: bash

    kubectl -n onap logs <cert-service-pod-name>

    e.g.
    kubectl -n onap logs $(kubectl -n onap get pods | grep cert-service | awk '{print $1}')

Console logs contains logs for logging levels from **DEBUG** to **ERROR**.

CertService logs for different logging levels are available in the container:

- Docker:

.. code-block:: bash

    docker exec -it <cert-service-container-name> bash

    e.g.
    docker exec -it oomcert-service bash

- Kubernetes:

.. code-block:: bash

    kubectl -n onap exec -it <cert-service-pod-name> bash

    e.g.
    kubectl -n onap exec -it $(kubectl -n onap get pods | grep cert-service | awk '{print $1}') bash

Path to logs:

    /var/log/onap/oom/certservice

Available log files:

    - audit.log - contains logs for **INFO** logging level
    - debug.log - contains logs for logging levels from **DEBUG** to **ERROR**
    - error.log - contains logs for **ERROR** logging level

User cannot change logging levels.

CMPv2 certificate provider
--------------------------
To see CMPv2 certificate provider console logs use :

.. code-block:: bash

    kubectl -n onap logs <cmpv2-certificate-provider-pod-name> provider

    e.g.
    kubectl -n onap logs $(kubectl -n onap get pods | grep cmpv2-cert-provider | awk '{print $1}') provider
