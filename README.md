# Cert service

### General description

More information about the project and all its functionalities you can find under the wiki page: 
    ```
    https://wiki.onap.org/display/DW/AAF+Certification+Service
    ``` 
  
Project consists of two submodules:
1. aaf-certservice-api
2. aaf-certservice-client

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
Docker-compose uses a local image of certservice-api and make run-client uses a local image of certservice-client
Build docker images locally before running docker compose command.
```
1. Build local images
make build
2. Start Cert Service with configured EJBCA
make start-backend
3. Run Cert Service Client
make run-client
3. Remove client container
make stop-client
4. Stop Cert Service and EJBCA
make stop-backend
```
    
### Running API with Helm
1. Use environment/server with installed kubernetes and helm.
2. Copy certService/helm/aaf-cert-service directory to that environment.
3. Enter that environment 
4. Run ```helm install ./aaf-cert-service```


### AAF CertService CSITs
#### CSIT repository
```
https://gerrit.onap.org/r/admin/repos/integration/csit
```

####How to run tests locally
1. Checkout CSIT repository
2. Configure CSIT local environment
3. Inside CSIT directory execute
```
sudo ./run-csit.sh plans/aaf/certservice
```

####Jenkins build
https://jenkins.onap.org/view/CSIT/job/aaf-master-csit-certservice/

### Sonar results
```     
https://sonarcloud.io/dashboard?id=onap_aaf-certservice
```
    
### Maven artifacts
All maven artifacts are deployed under nexus uri:
```
https://nexus.onap.org/content/repositories/snapshots/org/onap/aaf/certservice/
```
        
### Docker artifacts
All docker images are hosted under nexus3 uri:
```
https://nexus3.onap.org/repository/docker.snapshot/v2/onap/org.onap.aaf.certservice.aaf-certservice-api/
```