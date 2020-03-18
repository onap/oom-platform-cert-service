# Cert service client

### Project building
```
mvn clean package
```
    
### Building Docker image manually
Go to the certServiceClient subfolder and execute following statement (1.0.0-SNAPSHOT is related to a current project.version parameter):
```
docker build --build-arg VERSION=1.0.0-SNAPSHOT -t onap/org.onap.aaf.certservice.aaf-certservice-client .
```
    
### Install the package into the local repository
```
mvn clean install
```     
    
### Building Docker image and  install the package into the local repository
```
mvn clean install -P docker
```   

### Running Docker container local
```
docker run --name aaf-certservice-client onap/org.onap.aaf.certservice.aaf-certservice-client
```

### Running Docker container from nexus
```
docker run --name aaf-certservice-client nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:1.0.0
```

### Running client as standalone docker container
```
AAFCERT_CLIENT_IMAGE=nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest
DOCKER_ENV_FILE= <path to envfile>
NETWORK_CERT_SERVICE= <docker network of cert service>
 
docker run --env-file $DOCKER_ENV_FILE --network $NETWORK_CERT_SERVICE $AAFCERT_CLIENT_IMAGE
```
Sample Environment file:
```aidl
#Client envs
REQUEST_TIMEOUT=1000
OUTPUT_PATH=/var/log
CA_NAME=RA
#Csr config envs
COMMON_NAME=onap.org
ORGANIZATION=Linux-Foundation
ORGANIZATION_UNIT=ONAP
LOCATION=San-Francisco
STATE=California
COUNTRY=US
SANS=example.com:example2.com
```

### Logs locally

path: 
```
var/log/onap/aaf/certservice-client/certservice-client.log
```    
### Logs in Docker container
```
docker logs aaf-certservice-client
```
###Exit codes
```
0	Success
1	Invalid client configuration
2	Invalid CSR configuration 
3	Fail in key pair generation
4	Fail in  CSR generation
5	CertService HTTP unsuccessful response
6	Internal HTTP Client connection problem
7	Fail in PKCS12 conversion
8	Fail in Private Key to PEM Encoding
```
