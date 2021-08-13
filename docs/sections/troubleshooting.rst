.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020-2021 NOKIA
.. _troubleshooting:

Troubleshooting
================

Update an outdated certificate after Cert-Manager was down
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When a certificate expires because Cert-Manager was not able to trigger the update on time, for some CMPv2 servers, e.g.
EJBCA, there are manual actions required to perform the update.

Given the expired certificate status is *READY=False*:
1. Edit the cert resource. It can be e.g. a small change in SANs.
2. Use the cert-manager plugin *renew* command to trigger the update manually.
3. Edit the cert again to revert the changes.
4. Trigger the update manually.
The certificate should now be alive and updated correctly.
