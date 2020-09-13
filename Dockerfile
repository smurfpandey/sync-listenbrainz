FROM arm32v7/adoptopenjdk:11.0.8_10-jre-hotspot

COPY ./build/libs/sync-listenbrainz-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]