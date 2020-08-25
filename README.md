# Cert service

### General description

More information about the project and all its functionalities you can find under the wiki page: 
    ```
    https://wiki.onap.org/display/DW/OOM+Certification+Service
    ``` 
  
Project consists of three submodules:
1. oom-certservice-api
2. oom-certservice-client
3. oom-truststore-merger

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
4. Stop Cert Service and EJBCA
make stop-backend
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
https://sonarcloud.io/dashboard?id=onap_aaf-certservice
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
