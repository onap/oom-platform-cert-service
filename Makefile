all: build start-backend run-client stop-backend
start-with-client: start-backend run-client
.PHONY: build

build:
	@echo "##### Build Cert Service images locally #####"
	mvn clean install -P docker
	@echo "##### DONE #####"

start-backend:
	@echo "##### Start Cert Service #####"
	docker-compose up -d
	@echo "## Configure ejbca ##"
	docker exec aafcert-ejbca /opt/primekey/scripts/ejbca-configuration.sh
	@echo "##### DONE #####"

run-client:
	@echo "##### Create Cert Service Client volume folder: `pwd`/compose-resources/client-volume/ #####"
	mkdir -p `pwd`/compose-resources/client-volume/
	@echo "##### Start Cert Service Client #####"
	docker run \
	    --rm \
	    --name aafcert-client \
	    --env-file ./compose-resources/client-configuration.env \
	    --network certservice_certservice \
	    --mount type=bind,src=`pwd`/compose-resources/client-volume/,dst=/var/certs \
	    --volume `pwd`/certs/truststore.jks:/etc/onap/aaf/certservice/certs/truststore.jks \
	    --volume `pwd`/certs/certServiceClient-keystore.jks:/etc/onap/aaf/certservice/certs/certServiceClient-keystore.jks \
	    onap/org.onap.aaf.certservice.aaf-certservice-client:latest

stop-backend:
	@echo "##### Stop Cert Service #####"
	docker-compose down
	@echo "##### DONE #####"
