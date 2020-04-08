.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA
.. _architecture:

Architecture
============

The micro-service called CertService is designed for requesting certificates
signed by external Certificate Authority (CA) using CMP over HTTP protocol. It uses CMPv2 client to send and receive CMPv2 messages.
CertService's client will be also provided so other ONAP components (aka end components) can easily get certificate from CertService.
End component is an ONAP component (e.g. DCAE collector or controller) which requires certificate from CMPv2 server
to protect external traffic and uses CertService's client to get it.
CertService's client communicates with CertService via REST API over HTTPS, while CertService with CMPv2 server via CMP over HTTP.

.. image:: resources/certservice_high_level.jpg
   :width: 855px
   :height: 178px
   :alt: Interaction between components
