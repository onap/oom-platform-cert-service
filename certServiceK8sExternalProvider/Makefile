# ============LICENSE_START=======================================================
# oom-certservice-k8s-external-provider
# ================================================================================
# Copyright (C) 2020 Nokia. All rights reserved.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END=========================================================
#

ip_addr=$(shell hostname -I | awk '{print $$1}')
cmpv2_issuer_pod_id=$(shell kubectl -n onap get po | grep cmpv2issuer | awk '{print $$1}')

build:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=on go build -v -o bin/oom-certservice-cmpv2issuer

test: build
	go test ./...

build-docker-image:
	docker build -t onap/oom-certservice-cmpv2issuer:latest .

run-docker-image:
	docker run -it onap/oom-certservice-cmpv2issuer:latest

dev-copy-certs:
	mkdir -p dev/certs
	cp ../certs/cmpv2Issuer-cert.pem dev/certs/cmpv2Issuer-cert.pem
	cp ../certs/cmpv2Issuer-key.pem  dev/certs/cmpv2Issuer-key.pem
	cp ../certs/cacert.pem           dev/certs/cacert.pem

dev-build-docker-image: build dev-copy-certs
	docker build -t onap-dev/cmpv2issuer:latest . -f dev/Dockerfile.dev

dev-run-docker-image:
	docker run -it onap-dev/cmpv2issuer:latest

dev-update-docker-hosts-file:
	kubectl exec -it -n onap ${cmpv2_issuer_pod_id} -c kube-rbac-proxy -- sh -c "echo '$(ip_addr) oom-cert-service' >> /etc/hosts"
