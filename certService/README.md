# Cert service

### For developers
    * AAF Cert Service is a Spring Boot application
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
    
### Building Docker image
    ```
    docker build -t cert-service .

    ```

### Running Docker container
    ```
    docker run -p 8080:8080 --name cert-service cert-service

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
