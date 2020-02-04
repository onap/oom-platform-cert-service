FROM docker.io/openjdk:11-jre-slim

ARG VERSION=0.0.1-SNAPSHOT

COPY target/cert-service-${VERSION}.jar ./app/cert-service.jar

ENTRYPOINT ["java","-jar","./app/cert-service.jar"]
