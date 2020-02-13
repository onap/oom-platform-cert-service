FROM docker.io/openjdk:11-jre-slim

ARG VERSION=1.0.0-SNAPSHOT

RUN groupadd certService && useradd -g certService certService

RUN chown -R certService:certService /var/log

USER certService:certService

COPY target/cert-service-${VERSION}.jar ./opt/onap/aaf/certservice/cert-service.jar

ENTRYPOINT ["java","-jar","./opt/onap/aaf/certservice/cert-service.jar"]
