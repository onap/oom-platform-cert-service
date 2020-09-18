FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd onap && useradd -g onap truststoreMerger

RUN chown -R truststoreMerger:onap /var/log

USER truststoreMerger:onap

COPY target/oom-truststore-merger-${VERSION}.jar ./opt/onap/oom/truststoremerger/oom-truststore-merger.jar

#Run as root allow to manage certificates provided by other containers. It should be change in future
USER root

ENTRYPOINT ["java","-jar","./opt/onap/oom/truststoremerger/oom-truststore-merger.jar"]
