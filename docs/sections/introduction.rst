.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA
.. _introduction:


Introduction
============

Overview
--------
The micro-service called CertService is designed for requesting certificates signed by external Certificate Authority (CA) using CMP over HTTP protocol. It uses CMPv2 client to send and receive CMPv2 messages.

CertService's client is also provided so other ONAP components (aka end components) can easily get certificate from CertService. End component is an ONAP component (e.g. DCAE collector or controller) which requires certificate from CMPv2 server to protect external traffic and uses CertService's client to get it.

CertService's client communicates with CertService via REST API over HTTPS, while CertService with CMPv2 server via CMP over HTTP.

To proof that CertService works Open Source CMPv2 server (EJBCA) is deployed and used in E2E tests.

It is planned that Network Functions (aka xNFs) will get certificates from the same CMPv2 server and the same CA hierarchy, but will use own means to get such certificates. Cause xNFs and ONAP will get certificates signed by the same root CA and will trust such root CA, both parties will automatically trust each other and can communicate with each other.


Context View
------------

.. image:: resources/cmpv2_context_view.png
   :width: 533px
   :height: 315px
   :alt: CMPV2 Context View


In Frankfurt release only `Initialization Request <https://tools.ietf.org/html/rfc4210#section-5.3.1>`_ with `ImplicitConfirm <https://tools.ietf.org/html/rfc4210#section-5.1.1.1>`_ is supported.
Request sent to CMPv2 server is authenticated by secret value (initial authentication key) and reference value (used to identify the secret value) as described in `RFC-4210 <https://tools.ietf.org/html/rfc4210#section-4.2.1.2>`_.
