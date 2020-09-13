FROM openjdk:11.0.8-jdk as BUILD

COPY . /app
WORKDIR /app
RUN chmod +x gradlew
RUN ./gradlew --no-daemon --info --stacktrace shadowJar

FROM arm32v7/adoptopenjdk:11.0.8_10-jre-hotspot

COPY --from=BUILD /app/build/libs/sync-listenbrainz-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]