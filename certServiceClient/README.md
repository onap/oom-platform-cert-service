# Cert service client

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
nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-client:latest
```

### Running local client application as standalone docker container
CertService API and client must be running in same network.

You need certificate and trust anchors (in JKS format) to connect to CertService API via HTTPS. Information how to generate truststore and keystore files you can find in CertService main README.

Information how to run you can find in CertService main README and official documentation, see [Read The Docs](https://onap-doc.readthedocs.io/projects/onap-aaf-certservice/en/latest/sections/usage.html)


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
9	Wrong TLS configuration
10	File could not be created
