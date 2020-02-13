# Cert service

### For developers
    * AAF Cert Service Api is a Spring Boot application
    * Code style
        Use Google code formatter in your IDE.
        For IntelliJ use [https://plugins.jetbrains.com/plugin/8527-google-java-format]
        For other IDEs use []https://github.com/google/google-java-format]

### Running Locally
    ```
     mvn spring-boot:run

    ```

### Project building
    ```
     mvn clean package

    ```
    
### Building Docker image manually
    ```
    docker build -t aaf-certservice-api .

    ```
    
### Install the package into the local repository
    ```
    mvn clean install
   
    ```     
    
### Building Docker image and  install the package into the local repository
    ```
    mvn clean install -P docker
   
    ```   

### Running Docker container
    ```
    docker run -p 8080:8080 --name aaf-certservice-api onap/aaf-certservice-api

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

### Running CSITs
Pull csit repository
    
    ```
     https://gerrit.onap.org/r/admin/repos/integration/csit
    
    ```
Go to created directory and run
    
    ```
     sudo ./run-csit.sh plans/aaf/cert-service
    
    ```
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
audit.log  error.log  trace.log

### Sonar results
    ```     
      https://sonarcloud.io/dashboard?id=onap_aaf-certservice
    ```

### RestAPI
API is described by Swagger ( OpenAPI 3.0 ) on endpoint /docs 
( endpoint is defined in properties as springdoc.swagger-ui.path )
  
    ```
    http://localchost:8080/docs
    
    ```

### Sonar results
    ```     
      https://sonarcloud.io/dashboard?id=onap_aaf-certservice
    ```
