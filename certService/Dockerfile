FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd certService && useradd -g certService certService

RUN apt-get update && apt-get install -y vim curl

RUN chown -R certService:certService /var/log

USER certService:certService

COPY target/oom-certservice-api-${VERSION}.jar ./opt/onap/oom/certservice/oom-certservice-api.jar

ENTRYPOINT ["java","-jar","./opt/onap/oom/certservice/oom-certservice-api.jar"]
