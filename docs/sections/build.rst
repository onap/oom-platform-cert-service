.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Build
======

Jenkins
-------
#. JJB Master

    https://jenkins.onap.org/view/oom-platform-cert-service/job/oom-platform-cert-service-master-merge-java/

#. JJB Stage

    https://jenkins.onap.org/view/oom-platform-cert-service/job//oom-platform-cert-service-maven-docker-stage-master/

#. JJB Release

    https://jenkins.onap.org/view/oom-platform-cert-service/job/oom-platform-cert-service-maven-stage-master/
    https://jenkins.onap.org/view/oom-platform-cert-service/job/oom-platform-cert-service-release-merge/

#. JJB CSIT

    https://jenkins.onap.org/view/CSIT/job/oom-platform-cert-service-master-csit-certservice/

Environment
-----------

* Java 11
* Apache Maven 3.6.0
* Linux
* Docker 18.09.5
* Python 2.7.x

How to build images?
--------------------

#. Checkout the project from https://gerrit.onap.org/r/admin/repos/oom/platform/cert-service
#. Read information stored in README.md file
#. Use a Makefile to build images::

    make build

How to start service locally?
-----------------------------------------------
#. Start Cert Service with configured EJBCA::

    make start-backend

#. Run Cert Service Client::

    make run-client

#. Remove client container::

    make stop-client

#. Stop Cert Service and EJBCA::

    make stop-backend
