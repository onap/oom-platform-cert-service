FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd certServiceClient && useradd -g certServiceClient certServiceClient

RUN chown -R certServiceClient:certServiceClient /var/log

USER certServiceClient:certServiceClient

COPY target/oom-certservice-client-${VERSION}.jar ./opt/onap/oom/certservice/oom-certservice-client.jar

ENTRYPOINT ["java","-jar","./opt/onap/oom/certservice/oom-certservice-client.jar"]
