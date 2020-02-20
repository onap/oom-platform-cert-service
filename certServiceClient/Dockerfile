FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd certServiceClient && useradd -g certServiceClient certServiceClient

RUN chown -R certServiceClient:certServiceClient /var/log

USER certServiceClient:certServiceClient

COPY target/aaf-certservice-client-${VERSION}.jar ./opt/onap/aaf/certservice/aaf-certservice-client.jar

ENTRYPOINT ["java","-jar","./opt/onap/aaf/certservice/aaf-certservice-client.jar"]
