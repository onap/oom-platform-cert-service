FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

RUN groupadd onap && useradd -g onap truststoreMerger

RUN chown -R truststoreMerger:onap /var/log

USER truststoreMerger:onap

COPY target/oom-truststore-merger-${VERSION}.jar ./opt/onap/oom/truststoremerger/oom-truststore-merger.jar

ENTRYPOINT ["java","-jar","./opt/onap/oom/truststoremerger/oom-truststore-merger.jar"]
