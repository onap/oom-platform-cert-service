# Truststore merger

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
nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-truststore-merger:latest
```

### Running application as standalone docker container

Exemplary config.env file with necessary envs
```
TRUSTSTORES=/var/certs/truststore.jks:/var/certs/truststore.pem
TRUSTSTORES_PASSWORDS=/var/certs/truststoreJks.pass:
```
TRUSTSTORES env indicates paths (separated by ":") where truststores files are located.

TRUSTSTORES_PASSWORDS env indicates paths (separated by ":") where files with passwords to truststores are located.
PEM is not protected by password so its value should be empty

Execute below command in order to run app as docker container
```
docker run \
    --name oom-truststore-merger \
    --env-file ./config.env \
    --mount type=bind,src=<src_path>,dst=/var/certs \
onap/org.onap.oom.platform.cert-service.oom-truststore-merger:latest
```
Before run replace <src_path> with absolute path where you located truststores to merge (eg. /certs/resources/)

Output from merger (when pointed more than one truststore to merge in TRUSTSTORES env) success execution should be:
1. Created backup file (with .bak ext) of first truststore pointed in TRUSTSTORES env
2. First truststore pointed in TRUSTSTORES env contains merged certificates from all truststores mentioned in TRUSTSTORES env

Remove docker container:
```
docker rm oom-truststore-merger
```

### Logs locally

path:
```
var/log/onap/oom/truststore-merger/truststore-merger.log
```
### Logs in Docker container
```
docker logs oom-merger
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
