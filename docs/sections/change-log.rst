.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA


Change Log
==============

--------
Istanbul
--------

==============

Version: 2.4.0
--------------

:Release Date:

**New Features**

  N/A

**Bug Fixes**

  N/A

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

  CertService client is not supported since Istanbul release.

**Other**

==============

--------
Honolulu
--------

==============

Version: 2.3.3
--------------

:Release Date: 2021-01-27

**New Features**

  N/A

**Bug Fixes**

  Enhance CertServiceAPI response (include CMP server error messages).
  Fix KeyUsage extension sent to CMPv2 server

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

==============

Version: 2.3.2
--------------

:Release Date: 2020-12-28

**New Features**

  N/A

**Bug Fixes**

  Align Cert Service Api to RFC4210.
  Fix Cert Service Client CA_NAME validation.
  Fix Cert Service External Provider logging.

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

==============

Version: 2.3.1
--------------

:Release Date: 2020-12-02

**New Features**

  N/A

**Bug Fixes**

  Fix NullPointerException in CertService Client when SANs environment variable is not defined.

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

==============

Version: 2.3.0
--------------

:Release Date: 2020-12-01

**New Features**

* Extended CertService by support for new SANs types - IPs, E-mails, URIs

**Bug Fixes**

  N/A

**Known Issues**

  CertService Client exits unsuccessfully with code 99 when SANs environment variable is not defined, because of
  NullPointerException

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

==============

Version: 2.2.0
--------------

:Release Date:

**New Features**

* Added module **oom-certservice-k8s-external-provider** with following functionality:

  An external provider is a part of PKI infrastructure. It consumes CertificateRequest CRD from Cert-Manager and calls CertService API to enroll certificate from CMPv2 server.

  More information can be found on dedicated `wiki page <https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration>`_

**Bug Fixes**

  N/A

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**



=============

-------
Guilin
-------

=============

Version: 2.1.0
--------------

:Release Date:

**New Features**

* Added module **oom-certservice-post-processor** with following functionality:

  * appending CMPv2 certificates to CertMan truststore
  * replacing CertMan keystore with CMPv2 keystore

**Bug Fixes**

  N/A

**Known Issues**

  N/A

**Security Notes**

  N/A

*Fixed Security Issues*

  N/A

*Known Security Issues*

  N/A

*Known Vulnerabilities in Used Modules*

  N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

==============

Version: 2.0.0
--------------

:Release Date:

**New Features**

        - The same functionality as in aaf-certservice 1.2.0

**Bug Fixes**

        N/A

**Known Issues**

        N/A

**Security Notes**

        N/A

*Fixed Security Issues*

        N/A

*Known Security Issues*

        N/A

*Known Vulnerabilities in Used Modules*

        N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

===========

Version: 1.2.0
--------------

:Release Date:

**New Features**

        - Client creates subdirectories in given OUTPUT_PATH and place certificate into it.

**Bug Fixes**

        N/A

**Known Issues**

        N/A

**Security Notes**

        N/A

*Fixed Security Issues*

        N/A

*Known Security Issues*

        N/A

*Known Vulnerabilities in Used Modules*

        N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

===========

Version: 1.1.0
--------------

:Release Date: 2020-06-29

**New Features**

        - Added property to CertService Client to allow selection of output certificates type (One of: PEM, JKS, P12).

**Bug Fixes**

        - Resolved issue where created PKCS12 certificates had jks extension.

**Known Issues**

        N/A

**Security Notes**

        N/A

*Fixed Security Issues*

        N/A

*Known Security Issues*

        N/A

*Known Vulnerabilities in Used Modules*

        N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

===========

----------
Frankfurt
----------

===========

Version: 1.0.1
--------------

:Release Date: 2020-05-22

**New Features**

The Frankfurt Release is the first release of the Certification Service.


**Bug Fixes**

        - `AAF-1132 <https://jira.onap.org/browse/AAF-1132>`_ - CertService Client returns exit status 5 when TLS configuration fails

**Known Issues**

        - PKCS12 certificates have jks extension

**Security Notes**

        N/A

*Fixed Security Issues*

        N/A

*Known Security Issues*

        N/A

*Known Vulnerabilities in Used Modules*

        N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

===========

Version: 1.0.0
--------------

:Release Date: 2020-04-16

**New Features**

The Frankfurt Release is the first release of the Certification Service.

**Bug Fixes**

        - No new fixes were implemented for this release

**Known Issues**

        - `AAF-1132 <https://jira.onap.org/browse/AAF-1132>`_ - CertService Client returns exit status 5 when TLS configuration fails

        - PKCS12 certificates have jks extension

**Security Notes**

        N/A

*Fixed Security Issues*

        N/A

*Known Security Issues*

        N/A

*Known Vulnerabilities in Used Modules*

        N/A

**Upgrade Notes**

**Deprecation Notes**

**Other**

===========

End of Change Log
