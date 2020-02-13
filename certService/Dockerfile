FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${project.version}

RUN groupadd certService && useradd -g certService certService

RUN chown -R certService:certService /var/log

USER certService:certService

COPY target/aaf-certservice-api-${VERSION}.jar ./opt/onap/aaf/certservice/aaf-certservice-api.jar

ENTRYPOINT ["java","-jar","./opt/onap/aaf/certservice/aaf-certservice-api.jar"]
