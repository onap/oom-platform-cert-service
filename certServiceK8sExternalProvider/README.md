## Cert Service k8s external cert signing provider

### Build project

There are two methods for building the project:
    
 - mvn clean install
 - make

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

Some of the fields present in Cert Manager Certificate are not currently correctly supported, because of that they are
filtered from the Certificate Signing Request.

**Filtered fields:**
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
 
