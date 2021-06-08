# Cert service client *(deprecated)*

> Deprecated since Istanbul release in favor of Cert Manager certificates
> (for more details see certServiceK8sExternalProvider submodule).


### Project building
```
mvn clean package
```
    
    
### Install the package into the local repository
```
mvn clean install
```     
    
### Building Docker image and  install the package into the local repository
```
mvn clean install -P docker
```   

### Nexus container image
```
nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:2.3.3
```

### Running local client application as standalone docker container
CertService API and client must be running in same network.

You need certificate and trust anchors (in JKS format) to connect to CertService API via HTTPS. Information how to generate truststore and keystore files you can find in CertService main README.

To run CertService client as standalone docker container execute following steps:

1. Create file ‘$PWD/client.env’ with environment variables as in example below:
```
#Client envs
REQUEST_URL=<URL to CertService API>
REQUEST_TIMEOUT=10000
OUTPUT_PATH=/var/certs
CA_NAME=RA
OUTPUT_TYPE=P12

#CSR config envs
COMMON_NAME=onap.org
ORGANIZATION=Linux-Foundation
ORGANIZATION_UNIT=ONAP
LOCATION=San-Francisco
STATE=California
COUNTRY=US
SANS=test.onap.org,onap.com,onap@onap.org,127.0.0.1,onap://cluster.local/

#TLS config envs
KEYSTORE_PATH=/etc/onap/oom/certservice/certs/certServiceClient-keystore.jks
KEYSTORE_PASSWORD=<password to certServiceClient-keystore.jks>
TRUSTSTORE_PATH=/etc/onap/oom/certservice/certs/certServiceClient-truststore.jks
TRUSTSTORE_PASSWORD=<password to certServiceClient-truststore.jks>
```
2. Run docker container as in following example (API and client must be running in same network):
```
docker run \
--rm \
--name oomcert-client \
--env-file <$PWD/client.env (same as in step1)> \
--network <docker network of cert service> \
--mount type=bind,src=<path to local host directory where certificate and trust anchor will be created>,dst=<OUTPUT_PATH (same as in step 1)> \
--volume <local path to keystore in JKS format>:<KEYSTORE_PATH> \
--volume <local path to truststore in JKS format>:<TRUSTSTORE_PATH> \
nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:$VERSION
```
After successful creation of certifications, container exits with exit code 0.

### Logs locally

path: 
```
var/log/onap/oom/certservice-client/certservice-client.log
```    
### Logs in Docker container
```
docker logs oom-certservice-client
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
7	Fail in PEM conversion
8	Fail in Private Key to PEM Encoding
9	Wrong TLS configuration
10	File could not be created
99	Application exited abnormally
