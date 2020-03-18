all: build start stop
.PHONY: build

build:
	@echo "##### Build Cert Service images locally #####"
	mvn clean install -P docker
	@echo "##### DONE #####"
start:
	@echo "##### Start Cert Service #####"
	docker-compose up -d
	@echo "## Configure ejbca ##"
	docker exec aafcert-ejbca /opt/primekey/scripts/ejbca-configuration.sh
	@echo "##### DONE #####"
stop:
	@echo "##### Stop Cert Service #####"
	docker-compose down
	@echo "##### DONE #####"














