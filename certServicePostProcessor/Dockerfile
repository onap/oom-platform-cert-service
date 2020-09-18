FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd onap && useradd -g onap certpostprocessor

RUN chown -R certpostprocessor:onap /var/log

USER certpostprocessor:onap

COPY target/oom-certservice-post-processor-${VERSION}.jar /opt/onap/oom/cert-service/post-processor/oom-certservice-post-processor.jar

#Run as root allow to manage certificates provided by other containers. It should be change in future
USER root

ENTRYPOINT ["java","-jar","/opt/onap/oom/cert-service/post-processor/oom-certservice-post-processor.jar"]
