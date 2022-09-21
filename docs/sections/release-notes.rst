.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA
.. Copyright 2022 Deutsche Telekom, NOKIA

.. _release_notes:

***************************************
OOM Certification Service Release Notes
***************************************

.. contents::
    :depth: 2
..

Version: 2.6.0
==============

Abstract
--------

This document provides the release notes for the Kohn release.

Summary
-------

Vulnerability Fix

Release Data
------------

+--------------------------------------+---------------------------------------------------------------------------------------+
| **Project**                          | OOM                                                                                   |
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Docker images**                    |  * onap/org.onap.oom.platform.cert-service.oom-certservice-api:2.6.0                  |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:2.6.0       |
|                                      |  * onap/org.onap.oom.platform.cert-service.oom-certservice-k8s-external-provider:2.6.0|
|                                      |                                                                                       |
+--------------------------------------+---------------------------------------------------------------------------------------+
| **Release designation**              | Kohn                                                                                  |
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

- `OOM-2903 <https://jira.onap.org/browse/OOM-2985>`_ PACKAGES UPGRADES IN DIRECT DEPENDENCIES FOR KOHN

**Known Security Issues**

None


Test Results
------------
Not applicable


References
----------

For more information on the ONAP release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_
