# Cert service

### General description
More information about the project and all its functionalities you can find under the wiki page: 
    ```
    https://wiki.onap.org/display/DW/AAF+Certification+Service
    ``` 

### For developers
    * AAF Cert Service Api is a Spring Boot application
    * Code style
        Use Google code formatter in your IDE.
        For IntelliJ use [https://plugins.jetbrains.com/plugin/8527-google-java-format]
        For other IDEs use []https://github.com/google/google-java-format]

### Local project configuration
    * Create directory on your system /etc/onap/aaf/certservice
    * Copy sample configuration test/resources/cmpServers.json to that directory

### Running Locally
MANDATORY SEE 'Local project configuration' section
```
mvn spring-boot:run
```
  
### Running Locally with Developer Tools
MANDATORY SEE 'Local project configuration' section
```
mvn spring-boot:run -Pdev
```

### Project building
```
mvn clean package
```
    
### Building Docker image manually
Go to the certService subfolder and execute following statement (1.0.0-SNAPSHOT is related to a current project.version parameter):
```
docker build --build-arg VERSION=1.0.0-SNAPSHOT -t onap/org.onap.aaf.certservice.aaf-certservice-api .
```
    
### Install the package into the local repository
```
mvn clean install
```     
    
### Building Docker image and install the package into local repository
```
mvn clean install -P docker
```   

### Running Docker container local
```
docker run -p 8080:8080 --name aaf-certservice-api --mount type=bind,source=/<absolute_path>/cmpServers.json,target=/etc/onap/aaf/certservice/cmpServers.json onap/org.onap.aaf.certservice.aaf-certservice-api
```

### Running Docker container from nexus
```
docker run -p 8080:8080 --name aaf-certservice-api --mount type=bind,source=/<absolute_path>/cmpServers.json,target=/etc/onap/aaf/certservice/cmpServers.json nexus3.onap.org:10001/onap/org.onap.aaf.certservice.aaf-certservice-api:1.0.0
```
    
### Health Check
Browser:
```
http://<localhost>:8080/actuator/health
```
     
Curl:   
```
curl localhost:8080/actuator/health 
```   
 Should return {"status":"UP"}

### Logs locally

path: 
```
var/log/onap/aaf/certservice/
```    
### Logs in Docker container
```
docker exec -it aaf-certservice-api bash
```

path:
```
cd /var/log/onap/aaf/certservice
```
You should see:    
audit.log  error.log  debug.log

### RestAPI
API is described by Swagger ( OpenAPI 3.0 ) on endpoint /docs 
( endpoint is defined in properties as springdoc.swagger-ui.path )
```
http://localchost:8080/docs
```
