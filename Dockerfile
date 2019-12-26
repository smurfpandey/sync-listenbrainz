#FROM openjdk:11.0.5-jdk as BUILD
#
#COPY . /src
#WORKDIR /src
#RUN ./gradlew --no-daemon shadowJar

FROM arm32v7/adoptopenjdk:latest

COPY --from=BUILD /src/build/libs/sync-listenbrainz-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]