## Cert Service k8s external provider

### General description

Cert Service k8s external provider a part of certificate distribution infrastructure in ONAP.
The main functionality of the provider is to forward Certificate Sing Requests (CSR) created by cert-mananger to CertServiceAPI. 

More details can found on this page:  https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration.

### Build project

There are two methods for building the project:
    
 - mvn clean install (used by CI)
 - make (used by DEV)

### Installation

Create secret with certificates for communication between CMPv2Issuer and Cert Service API:
```
kubectl create secret generic -n onap cmpv2-issuer-secret --from-file=<project-base-dir>/certs/cmpv2Issuer-key.pem
  --from-file=<project-base-dir>/certs/cmpv2Issuer-cert.pem --from-file=<project-base-dir>/certs/cacert.pem
```

Apply k8s files from 'deploy' directory in following order:
 
 - crd.yaml
 - roles.yaml
 - deployment.yaml
 - configuration.yaml (certRef, keyRef and cacertRef should match file names if secret was created with command listed 
 above)

**Note:** Files and installation are currently examples, which should be used as a guide for OOM Helm Charts implementation  

### Usage

To issue a certificate adjust and apply following k8s file:
 
 - certificate_example.yaml
 
#### Unsupported Certificate fields

Some fields present in Cert Manager Certificate are currently not supported by CertService API and because of that they are
filtered out from the Certificate Signing Request.

**Fields that are filtered out:**
 - subjectDN fields:
   - serialNumber
   - streetAddresses
   - postalCodes
 - isCa
 - ipAddresses
 - uris
 - emails
 - duration
 - usages
 
 #### Overridden Certificate fields
 
Some fields present in Cert Manager Certificate will be overridden by CertService API.

**Overridden fields:**
 - duration
 - usages
 
 
