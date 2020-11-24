.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA
.. _release_notes:

***************************************
OOM Certification Service Release Notes
***************************************

Abstract
========

This document provides the release notes for the Guilin release.

Summary
=======

Certification Service provides certificates signed by external CMPv2 server - further on such certificates are called operators certificates. Operators certificates are meant to secure external ONAP traffic - traffic between network functions (xNFs) and ONAP.

This project was moved from Application Authorization Framework (AAF), to check previous release notes see,  `AAF CertService release notes <https://docs.onap.org/projects/onap-aaf-certservice/en/frankfurt/sections/release-notes.html>`_ .


Release Data
============

+--------------------------------------+--------------------------------------------------------------------------------+
| **Project**                          | OOM                                                                            |
|                                      |                                                                                |
+--------------------------------------+--------------------------------------------------------------------------------+
| **Docker images**                    |  * onap/org.onap.oom.platform.cert-service.oom-certservice-api:2.1.0           |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-client:2.1.0        |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:2.1.0|
|                                      |                                                                                |
+--------------------------------------+--------------------------------------------------------------------------------+
| **Release designation**              | Guilin                                                                         |
|                                      |                                                                                |
+--------------------------------------+--------------------------------------------------------------------------------+


New features
------------

- `AAF-1152 <https://jira.onap.org/browse/AAF-1152>`_ Added to CertService's client parameter which controls output type of certificates (JKS, PKCS12, PEM)

- `DCAEGEN2-2252 <https://jira.onap.org/browse/DCAEGEN2-2252>`_ Added new not existing subfolders creation in output path (CMPv2 Integration).

- `DCAEGEN2-2253 <https://jira.onap.org/browse/DCAEGEN2-2253>`_ Implemented CertServicePostprocessor, which allow merge truststores in different types and move keystore files.

- `OOM-2526 <https://jira.onap.org/browse/OOM-2526>`_ Moved project from AAF to OOM platform.

**Bug fixes**

- `OOM-2524 <https://jira.onap.org/browse/OOM-2524>`_ Fixed project makefile.

**Known Issues**

None

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~
Docker images mentioned in Release Date section.

Documentation Deliverables
~~~~~~~~~~~~~~~~~~~~~~~~~~
Documentation moved from AAF - `OOM Certification Service <https://docs.onap.org/projects/onap-oom-platform-cert-service/en/latest/index.html#master-index>`_ .

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

For more information on the ONAP Guilin release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org
