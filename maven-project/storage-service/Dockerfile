FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY storage-service/pom.xml storage-service/pom.xml
RUN mvn -f storage-service/pom.xml dependency:go-offline
COPY storage-service/src storage-service/src
RUN mvn -f storage-service/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
RUN apk add --no-cache curl
WORKDIR /storage-service
COPY --from=build app/storage-service/target/*.jar storage-service.jar
EXPOSE 8088
CMD ["java", "-jar", "storage-service.jar"]