.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA
.. _release_notes:

***************************************
OOM Certification Service Release Notes
***************************************

.. contents::
    :depth: 2
..

Version: 2.5.0
==============

Abstract
--------

This document provides the release notes for the Jakarta release.

Summary
-------

Vulnerability Fix

Release Data
------------

+--------------------------------------+---------------------------------------------------------------------------------------+
| **Project**                          | OOM                                                                                   |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Docker images**                    |  * onap/org.onap.oom.platform.cert-service.oom-certservice-api:2.5.0                  |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:2.5.0       |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-k8s-external-provider:2.5.0|
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Release designation**              | Jakarta                                                                              |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+


New features
------------

**Bug fixes**

**Known Issues**

If Cert-Manager was down for some time and did not trigger certificate update on time, then updating an outdated certificate may require manual actions.
The required actions are described in :ref:`Troubleshooting section <troubleshooting>`

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~
Docker images mentioned in Release Date section.

Documentation Deliverables
~~~~~~~~~~~~~~~~~~~~~~~~~~

- :ref:`CMPv2 certificate provider description <cmpv2_cert_provider>`

Known Limitations, Issues and Workarounds
-----------------------------------------

System Limitations
~~~~~~~~~~~~~~~~~~

Any known system limitations.


Known Vulnerabilities
~~~~~~~~~~~~~~~~~~~~~

Any known vulnerabilities.


Workarounds
~~~~~~~~~~~

Any known workarounds.


Security Notes
--------------

**Fixed Security Issues**

- `OOM-2903 <https://jira.onap.org/browse/OOM-2903>`_ Fix Apache Vulnerability [CVE-2021-44228] in CertService

**Known Security Issues**

None


Test Results
------------
Not applicable


References
----------

For more information on the ONAP Istanbul release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

Version: 2.4.0
==============

Abstract
--------

This document provides the release notes for the Istanbul release.

Summary
-------

Certificate update use case is now available. For details go to:
:ref:`How to use instructions<how_to_use_certificate_update>`

Release Data
------------

+--------------------------------------+---------------------------------------------------------------------------------------+
| **Project**                          | OOM                                                                                   |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Docker images**                    |  * onap/org.onap.oom.platform.cert-service.oom-certservice-api:2.4.0                  |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:2.4.0       |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-k8s-external-provider:2.4.0|
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Release designation**              | Istanbul                                                                              |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+


New features
------------

- `OOM-2754 <https://jira.onap.org/browse/OOM-2754>`_ Implement certificate update in CMPv2 external issuer

- `OOM-2753 <https://jira.onap.org/browse/OOM-2753>`_ Implement certificate update in CMPv2 CertService

- `OOM-2744 <https://jira.onap.org/browse/OOM-2744>`_ Remove CertService Client mechanism from ONAP

- `OOM-2649 <https://jira.onap.org/browse/OOM-2649>`_ Update contrib/ejbca to 7.x

**Bug fixes**

- `OOM-2771 <https://jira.onap.org/browse/OOM-2771>`_ Fix CertificateRequest resource was not found issue in CMPv2 external issuer

- `OOM-2764 <https://jira.onap.org/browse/OOM-2764>`_ Fix sonar issues in CertService

**Known Issues**

If Cert-Manager was down for some time and did not trigger certificate update on time, then updating an outdated certificate may require manual actions.
The required actions are described in :ref:`Troubleshooting section <troubleshooting>`

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~
Docker images mentioned in Release Date section.

Documentation Deliverables
~~~~~~~~~~~~~~~~~~~~~~~~~~

- :ref:`CMPv2 certificate provider description <cmpv2_cert_provider>`

Known Limitations, Issues and Workarounds
-----------------------------------------

System Limitations
~~~~~~~~~~~~~~~~~~

Any known system limitations.


Known Vulnerabilities
~~~~~~~~~~~~~~~~~~~~~

Any known vulnerabilities.


Workarounds
~~~~~~~~~~~

Any known workarounds.


Security Notes
--------------

**Fixed Security Issues**

None

**Known Security Issues**

None


Test Results
------------
Not applicable


References
----------

For more information on the ONAP Istanbul release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_
