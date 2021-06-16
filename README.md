# Cert service

### General description

More information about the project and all its functionalities you can find under the wiki page: 
    ```
    https://wiki.onap.org/display/DW/OOM+Certification+Service
    ``` 
  
Project consists of four submodules:
1. oom-certservice-api
2. *deprecated (no longer built)* oom-certservice-client
3. oom-certservice-post-processor
4. oom-certservice-k8s-external-provider

Detailed information about submodules can be found in ```README.md``` in their directories.

### Project building
```
mvn clean package
```

### Install the packages into the local repository
```
mvn clean install
```     
    
### Building Docker images and install packages into local repository
```
mvn clean install -P docker
or
make build
```   

### Generating certificates
There are example certificates already generated in certs/ directory.
In order to generate new certificates, first remove existing ones.
Then execute following command from certs(!) directory:
```
 make
```

### Running Docker containers from docker-compose with EJBCA
Docker-compose uses a local image of certservice-api and make run-client uses a released image of certservice-client
Build certservice-api docker image locally before running docker compose command.
```
1. Build local images
make build
2. Start Cert Service with configured EJBCA
make start-backend
3. Run Cert Service Client
make run-client
4. Stop Cert Service and EJBCA
make stop-backend
```

### Generating certificates via REST Api
#### Requirements
* OpenSSL
* cURL
* jq (for parseCertServiceResponse.sh script)
#### Initialization Request
1. Create Certificate Signing Request and Private Key
```
openssl req -new -newkey rsa:2048 -nodes -keyout ./compose-resources/certs-from-curl/ir.key \
	    -out ./compose-resources/certs-from-curl/ir.csr \
	    -subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=onap.org" \
	    -addext "subjectAltName = DNS:test.onap.org"
```
2. Send Initialization Request
```
curl -s https://localhost:8443/v1/certificate/RA -H "PK: $(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
        -H "CSR: $(cat ./compose-resources/certs-from-curl/ir.csr | base64 | tr -d \\n)" \
        --cert ./certs/cmpv2Issuer-cert.pem \
        --key ./certs/cmpv2Issuer-key.pem \
        --cacert ./certs/cacert.pem
```
to parse the response pipe the output to `parseCertserviceResponse.sh` script, providing prefix as argument
```
curl -sN https://localhost:8443/v1/certificate/RA -H "PK: $(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
        -H "CSR: $(cat ./compose-resources/certs-from-curl/ir.csr | base64 | tr -d \\n)" \
        --cert ./certs/cmpv2Issuer-cert.pem \
        --key ./certs/cmpv2Issuer-key.pem \
        --cacert ./certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "ir"
```

#### Update Request
1. Create Certificate Signing Request and Private Key - same as for Initialization Request.
When CSR data (like Subject and SANS) is unchanged, Key Update Request will be performed.
Otherwise Certification Request will be performed. 
Example for KUR:
```
openssl req -new -newkey rsa:2048 -nodes -keyout ./compose-resources/certs-from-curl/kur.key \
-out ./compose-resources/certs-from-curl/kur.csr \
-subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=onap.org" \
-addext "subjectAltName = DNS:test.onap.org"
```
Example for CR:
```
openssl req -new -newkey rsa:2048 -nodes -keyout ./compose-resources/certs-from-curl/cr.key \
-out ./compose-resources/certs-from-curl/cr.csr \
-subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=new-onap.org" \
-addext "subjectAltName = DNS:test.onap.org"
```
2. Send Update Request.
Example for KUR:
```
curl -sN https://localhost:8443/v1/certificate-update/RA -H "PK: $(cat ./compose-resources/certs-from-curl/kur.key | base64 | tr -d \\n)" \
	    -H "CSR: $(cat ./compose-resources/certs-from-curl/kur.csr | base64 | tr -d \\n)" \
	    -H "OLDPK: $(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
	    -H "OLDCERT: $(cat ./compose-resources/certs-from-curl/ir-cert.pem | base64 | tr -d \\n)" \
	    --cert ./certs/cmpv2Issuer-cert.pem \
	    --key ./certs/cmpv2Issuer-key.pem \
	    --cacert ./certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "kur"
```
Example CR:
```
curl -sN https://localhost:8443/v1/certificate-update/RA -H "PK: $$(cat ./compose-resources/certs-from-curl/cr.key | base64 | tr -d \\n)" \
	    -H "CSR: $$(cat ./compose-resources/certs-from-curl/cr.csr | base64 | tr -d \\n)" \
	    -H "OLD_PK: $$(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
	    -H "OLD_CERT: $$(cat ./compose-resources/certs-from-curl/ir-cert.pem | base64 | tr -d \\n)" \
	    --cert ./certs/cmpv2Issuer-cert.pem \
	    --key ./certs/cmpv2Issuer-key.pem \
	    --cacert ./certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "cr"
```

#### Using makefile
1. Perform Initialization Request:
```
make send-initialization-request
```
2. Perform Update Request:
```
make send-key-update-request
```
or:
```
make send-certification-request
```

### OOM CertService CSITs
#### CSIT repository
```
https://gerrit.onap.org/r/admin/repos/integration/csit
```

####How to run tests locally
1. Checkout CSIT repository
2. Configure CSIT local environment
3. Inside CSIT directory execute
```
sudo ./run-csit.sh plans/oom-platform-cert-service/certservice
```

####Jenkins build
https://jenkins.onap.org/view/CSIT/job/oom-platform-cert-service-master-csit-certservice/

### Sonar results
```     
https://sonarcloud.io/dashboard?id=onap_oom-platform-cert-service
```
    
### Maven artifacts
All maven artifacts are deployed under nexus uri:
```
https://nexus.onap.org/content/repositories/snapshots/org/onap/oom/certservice/
```
        
### Docker artifacts
All docker images are hosted under nexus3 uri:
```
https://nexus3.onap.org/repository/docker.snapshot/v2/onap/org.onap.oom.certservice.oom-certservice-api/
```

### How to release containers
```
https://github.com/lfit/releng-global-jjb/blob/master/docs/jjb/lf-release-jobs.rst
```
