FROM arm32v7/gradle:6.6.1-jdk14 as BUILD

COPY . /app
WORKDIR /app
RUN chmod +x gradlew
RUN gradle build --no-daemon --debug --stacktrace shadowJar

FROM arm32v7/adoptopenjdk:14.0.2_8-jre-hotspot

COPY --from=BUILD /app/build/libs/sync-listenbrainz-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]