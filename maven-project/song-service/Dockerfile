FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY song-service/pom.xml song-service/pom.xml
RUN mvn -f song-service/pom.xml dependency:go-offline
COPY song-service/src song-service/src
RUN mvn -f song-service/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /song-service
COPY --from=build app/song-service/target/*.jar song-service.jar
EXPOSE 8081
CMD ["java", "-jar", "song-service.jar"]