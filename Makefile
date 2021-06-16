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
	docker exec oomcert-ejbca /opt/primekey/scripts/ejbca-configuration.sh
	@echo "##### DONE #####"

run-client:
	@echo "##### Create Cert Service Client volume folder: `pwd`/compose-resources/client-volume/ #####"
	mkdir -p `pwd`/compose-resources/client-volume/
	@echo "##### Start Cert Service Client #####"
	docker run \
	    --rm \
	    --name oomcert-client \
	    --env-file ./compose-resources/client-configuration.env \
	    --network cert-service_certservice \
	    --mount type=bind,src=`pwd`/compose-resources/client-volume/,dst=/var/certs \
	    --volume `pwd`/certs/truststore.jks:/etc/onap/oom/certservice/certs/truststore.jks \
	    --volume `pwd`/certs/certServiceClient-keystore.jks:/etc/onap/oom/certservice/certs/certServiceClient-keystore.jks \
	    nexus3.onap.org:10001/onap/org.onap.oom.platform.cert-service.oom-certservice-client:2.3.3

stop-backend:
	@echo "##### Stop Cert Service #####"
	docker-compose down
	@echo "##### DONE #####"

send-initialization-request:
	@echo "##### Create folder for certificates from curl: `pwd`/compose-resources/certs-from-curl/ #####"
	mkdir -p `pwd`/compose-resources/certs-from-curl/
	@echo "##### Generate CSR and Key #####"
	openssl req -new -newkey rsa:2048 -nodes -keyout `pwd`/compose-resources/certs-from-curl/ir.key \
	    -out `pwd`/compose-resources/certs-from-curl/ir.csr \
	    -subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=onap.org" \
	    -addext "subjectAltName = DNS:test.onap.org"
	@echo "##### Send Initialization Request #####"
	curl -sN https://localhost:8443/v1/certificate/RA -H "PK: $$(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
	    -H "CSR: $$(cat ./compose-resources/certs-from-curl/ir.csr | base64 | tr -d \\n)" \
	    --cert `pwd`/certs/cmpv2Issuer-cert.pem \
	    --key `pwd`/certs/cmpv2Issuer-key.pem \
	    --cacert `pwd`/certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "ir"

send-key-update-request: verify-initialization-request-files-exist
	@echo "##### Generate CSR and Key #####"
	openssl req -new -newkey rsa:2048 -nodes -keyout `pwd`/compose-resources/certs-from-curl/kur.key \
	    -out `pwd`/compose-resources/certs-from-curl/kur.csr \
	    -subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=onap.org" \
	    -addext "subjectAltName = DNS:test.onap.org"
	@echo "##### Send Key Update Request #####"
	curl -sN https://localhost:8443/v1/certificate-update/RA -H "PK: $$(cat ./compose-resources/certs-from-curl/kur.key | base64 | tr -d \\n)" \
	    -H "CSR: $$(cat ./compose-resources/certs-from-curl/kur.csr | base64 | tr -d \\n)" \
	    -H "OLD_PK: $$(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
	    -H "OLD_CERT: $$(cat ./compose-resources/certs-from-curl/ir-cert.pem | base64 | tr -d \\n)" \
	    --cert `pwd`/certs/cmpv2Issuer-cert.pem \
	    --key `pwd`/certs/cmpv2Issuer-key.pem \
	    --cacert `pwd`/certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "kur"

send-certification-request: verify-initialization-request-files-exist
	@echo "##### Generate CSR and Key #####"
	openssl req -new -newkey rsa:2048 -nodes -keyout `pwd`/compose-resources/certs-from-curl/cr.key \
	    -out `pwd`/compose-resources/certs-from-curl/cr.csr \
	    -subj "/C=US/ST=California/L=San-Francisco/O=ONAP/OU=Linux-Foundation/CN=new-onap.org" \
	    -addext "subjectAltName = DNS:test.onap.org"
	@echo "##### Send Key Update Request #####"
	curl -sN https://localhost:8443/v1/certificate-update/RA -H "PK: $$(cat ./compose-resources/certs-from-curl/cr.key | base64 | tr -d \\n)" \
	    -H "CSR: $$(cat ./compose-resources/certs-from-curl/cr.csr | base64 | tr -d \\n)" \
	    -H "OLD_PK: $$(cat ./compose-resources/certs-from-curl/ir.key | base64 | tr -d \\n)" \
	    -H "OLD_CERT: $$(cat ./compose-resources/certs-from-curl/ir-cert.pem | base64 | tr -d \\n)" \
	    --cert `pwd`/certs/cmpv2Issuer-cert.pem \
	    --key `pwd`/certs/cmpv2Issuer-key.pem \
	    --cacert `pwd`/certs/cacert.pem | `pwd`/parseCertServiceResponse.sh "cr"

verify-initialization-request-files-exist:
  ifeq (,$(wildcard compose-resources/certs-from-curl/ir.key))
  ifeq (,$(wildcard compose-resources/certs-from-curl/ir-cert.pem))
			$(error Execute send-initialization-request first)
  endif
  endif
