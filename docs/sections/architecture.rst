.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA
.. _architecture:

Architecture
============

Interaction between components
------------------------------

.. image:: resources/certservice_high_level.png
   :width: 855px
   :height: 223px
   :alt: Interaction between components


Simplified certificate enrollment flow
--------------------------------------

.. image:: resources/certService_cert_enrollment_flow.png
   :width: 1191px
   :height: 893px
   :alt: Simplified certificate enrollment flow

Security considerations
-----------------------

CertService's REST API is protected by mutual HTTPS, meaning server requests client's certificate and **authenticate** only requests with trusted certificate. After ONAP default installation only certificate from CertService's client is trusted. **Authorization** isn't supported in Frankfurt release.