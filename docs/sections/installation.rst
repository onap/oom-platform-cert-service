.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA

Installation
=============


When enabling CMPv2, *kubernetes/onap/resources/overrides/aaf-cert-service-environment.yaml* file with override values need to be used during OOM installation.
CertService can be easily installed with OOM installation, simply by setting proper flag.
It's possible to also install EJBCA server for testing purposes. It also can be done by setting proper flag.



Enabling CertService
--------------------

In order to install CertService during OOM deployment, global flag *global.cmpv2Enabled* in *kubernetes/onap/resources/overrides/aaf-cert-service-environment.yaml* file must be set to true.


Enabling EJBCA - testing CMPV2 server
-------------------------------------

In order to install EJBCA server, global flag *global.addTestingComponents* in *kubernetes/onap/values.yaml* file or other file with override values must be set to true.

Setting this flag, will also cause CertService to load test configuration from *kubernetes/aaf/charts/aaf-cert-service/resources/test/cmpServers.json*
