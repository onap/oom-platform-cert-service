## Cert Service K8s external provider

### General description

Cert Service K8s external provider ia a part of certificate distribution infrastructure in ONAP.
The main functionality of the provider is to forward Certificate Signing Requests (CSRs) created by cert-mananger (https://cert-manager.io) to CertServiceAPI.

More information can found on a dedicated page:  https://wiki.onap.org/display/DW/CertService+and+K8s+Cert-Manager+integration.

### Build project

There are two methods for building the project:
    
 - mvn clean install (used by CI)
 - make (used by DEV)

### Installation

#### Providing K8s secret containing TLS certificates

Create secret with certificates for communication between CMPv2Issuer and Cert Service API:
```
kubectl create secret generic -n onap cmpv2-issuer-secret --from-file=<project-base-dir>/certs/cmpv2Issuer-key.pem
  --from-file=<project-base-dir>/certs/cmpv2Issuer-cert.pem --from-file=<project-base-dir>/certs/cacert.pem
```

#### Deployment of the application

Apply K8s files from 'deploy' directory in following order:
 
 - crd.yaml
 - roles.yaml
 - deployment.yaml
 - configuration.yaml (certRef, keyRef and cacertRef should match file names if secret was created with command listed 
 above)

**Note:** Files and installation are currently examples, which should be used as a guide for OOM Helm Charts implementation  

#### Log level adjustment

Log level can be set during deployment as docker container argument --> see deployment.yaml file.
Here is an interesting part from the deployment.yaml file:

      - args:
        - --metrics-addr=127.0.0.1:8080
        - --log-level=debug
        command:
        - /oom-certservice-cmpv2issuer
        image: onap/oom-certservice-cmpv2issuer:1.0.0

Supported values of log-level flag (case-sensitive): debug, info, warn, error 

### Usage

To issue a certificate adjust and apply following K8s file:
 
 - certificate_example.yaml
 
#### Unsupported Certificate fields

Some fields present in Cert-Manager Certificate are currently not supported by CertService API and because of that they are
filtered out from the Certificate Signing Request.

**Fields that are filtered out:**
 - subjectDN fields:
   - serialNumber
   - streetAddresses
   - postalCodes
 - isCa
 - duration
 - usages
 
 #### Overridden Certificate fields
 
Some fields present in a Cert-Manager Certificate will be overridden by a CMPv2 server.

**Overridden fields:**
 - duration
 - usages
 
 
