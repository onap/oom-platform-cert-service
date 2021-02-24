.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA
.. _release_notes:

***************************************
OOM Certification Service Release Notes
***************************************

Abstract
========

This document provides the release notes for the Honolulu release.

Summary
=======

Certification Service provides certificates signed by external CMPv2 server - such certificates are further called operators certificates. Operators certificates are meant to secure external ONAP traffic - traffic between network functions (xNFs) and ONAP.

This project was moved from Application Authorization Framework (AAF), to check previous release notes see,  `AAF CertService release notes <https://docs.onap.org/projects/onap-aaf-certservice/en/frankfurt/sections/release-notes.html>`_ .


Release Data
============

+--------------------------------------+---------------------------------------------------------------------------------------+
| **Project**                          | OOM                                                                                   |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Docker images**                    |  * onap/org.onap.oom.platform.cert-service.oom-certservice-api:2.3.3                  |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-client:2.3.3               |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:2.3.3       |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-k8s-external-provider:2.3.3|
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Release designation**              | Honolulu                                                                              |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+


New features
------------

- `OOM-2560 <https://jira.onap.org/browse/OOM-2560>`_ Integrated CMPv2 certificate provider with Cert-Manager

  An CMPv2 certificate provider is a part of PKI infrastructure. It consumes CertificateRequest custom resource from Cert-Manager and calls CertService API to enroll certificate from CMPv2 server.
  During ONAP deployment, the CMPv2 certificate provider is enabled when flags cmpv2Enabled, CMPv2CertManagerIntegration and platform.enabled equals true.

  More information can be found on dedicated `wiki page <https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration>`_

- `OOM-2632 <https://jira.onap.org/browse/OOM-2632>`_ Extended CertService API and clients to correctly support SANs parameters such as: e-mails, URIs and IP addresses.

**Bug fixes**

- `OOM-2656 <https://jira.onap.org/browse/OOM-2656>`_ Adjusted CertService API to RFC4210 - changed MAC protection algorithm and number of iteration for such algorithm.

- `OOM-2657 <https://jira.onap.org/browse/OOM-2657>`_ Enhanced CertServiceAPI response in order to include CMP server error messages.

- `OOM-2658 <https://jira.onap.org/browse/OOM-2658>`_ Fixed KeyUsage extension sent to CMPv2 server

**Known Issues**

None

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~
Docker images mentioned in Release Date section.

Documentation Deliverables
~~~~~~~~~~~~~~~~~~~~~~~~~~

- :doc:`CMPv2 certificate provider description <cmpv2-cert-provider>`

Known Limitations, Issues and Workarounds
=========================================

System Limitations
------------------

Any known system limitations.


Known Vulnerabilities
---------------------

Any known vulnerabilities.


Workarounds
-----------

Any known workarounds.


Security Notes
--------------

**Fixed Security Issues**

None

**Known Security Issues**

None


Test Results
============
Not applicable


References
==========

For more information on the ONAP Honolulu release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org
