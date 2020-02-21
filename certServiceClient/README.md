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

### Logs locally

path: 
```
var/log/onap/aaf/certservice-client/certservice-client.log
```    
### Logs in Docker container
```
docker logs aaf-certservice-client
```