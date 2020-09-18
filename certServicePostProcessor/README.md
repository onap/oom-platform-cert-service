# Certificate Post Processor

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
nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:latest
```

### Running application as standalone docker container

Exemplary config.env file with necessary envs
```
TRUSTSTORES_PATHS=/var/certs/truststore.jks:/var/certs/truststore.pem
TRUSTSTORES_PASSWORDS_PATHS=/var/certs/truststoreJks.pass:
KEYSTORE_SOURCE_PATHS=/var/certs/external/keystore.jks:/var/certs/external/keystore.pass
KEYSTORE_DESTINATION_PATHS=/var/certs/cert.jks:/var/certs/jks.pass
```
TRUSTSTORES_PATHS env indicates paths (separated by ":") where truststores files are located.

TRUSTSTORES_PASSWORDS_PATHS env indicates paths (separated by ":") where files with passwords to truststores are located.
PEM is not protected by password so its value should be empty

KEYSTORE_SOURCE_PATHS env (optional) indicates paths (separated by ":") where files to copy are located.

KEYSTORE_DESTINATION_PATHS env (optional) indicates paths (separated by ":") to files which should be replaced. Before keystore files override, destination files will be copied with addition of .bak extension.

Execute below command in order to run app as docker container
```
docker run \
    --name oom-certservice-post-processor \
    --env-file ./config.env \
    --mount type=bind,src=<src_path>,dst=/var/certs \
onap/org.onap.oom.platform.cert-service.oom-certservice-post-processor:latest
```
Before run replace <src_path> with absolute path where you located truststores to merge (eg. /certs/resources/)

Output from merger (when pointed more than one truststore to merge in TRUSTSTORES_PATHS env and provided optional envs) success execution should be:
1. Created backup file (with .bak ext) of first truststore pointed in TRUSTSTORES_PATHS env
2. Keystores files listed in KEYSTORE_SOURCE_PATHS env overrides corresponding to them files defined in KEYSTORE_DESTINATION_PATHS env.
3. Keystores listed in KEYSTORE_SOURCE_PATHS env are in locations taken from KEYSTORE_DESTINATION_PATHS env. Files listed in KEYSTORE_DESTINATION_PATHS env before application run, still exist with appended .bak extension.

Remove docker container:
```
docker rm oom-certservice-post-processor
```

### Logs locally

path:
```
var/log/onap/oom/cert-service/post-processor/application.log
```
### Logs in Docker container
```
docker logs oom-certservice-post-processor
```
###Exit codes
```
0   Success
1   Invalid paths in environment variables
2   Invalid merger configuration
3   Invalid truststore file-password pair
4   Cannot read password from file
5   Cannot create backup file
6   Cannot initialize keystore instance
7   Cannot load truststore file
8   Cannot operate on truststore data
9   Missing truststore certificates in provided file
10  Alias conflict detected
11  Cannot save truststore file
12  Cannot copy keystore file
13  Keystore file does not exist
99  Application exited abnormally
