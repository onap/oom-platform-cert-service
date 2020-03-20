all: build start-backend run-client stop-client stop-backend
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
	    --name aafcert-client \
	    --env-file ./compose-resources/client-configuration.env \
	    --network certservice_certservice \
	    --mount type=bind,src=`pwd`/compose-resources/client-volume/,dst=/var/certs \
	    onap/org.onap.aaf.certservice.aaf-certservice-client:latest

stop-client:
	@echo "##### Remove Cert Service Client #####"
	@echo "Removing aafcert-client"
	@docker rm aafcert-client > /dev/null 2> /dev/null || true
	@echo "##### DONE #####"

stop-backend:
	@echo "##### Stop Cert Service #####"
	docker-compose down
	@echo "##### DONE #####"
