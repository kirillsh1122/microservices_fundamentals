FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY storage-client/pom.xml storage-client/pom.xml
RUN mvn -f storage-client/pom.xml dependency:go-offline
COPY storage-client/src storage-client/src
RUN mvn -f storage-client/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
RUN apk add --no-cache curl
WORKDIR /storage-client
COPY --from=build /app/storage-client/target/*.jar storage-client.jar
EXPOSE 8080
CMD ["java", "-jar", "storage-client.jar"]