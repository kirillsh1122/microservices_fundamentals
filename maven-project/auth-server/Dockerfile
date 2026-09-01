FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY auth-server/pom.xml auth-server/pom.xml
RUN mvn -f auth-server/pom.xml dependency:go-offline
COPY auth-server/src auth-server/src
RUN mvn -f auth-server/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
RUN apk add --no-cache curl
WORKDIR /auth-server
COPY --from=build /app/auth-server/target/*.jar auth-server.jar
EXPOSE 8080
CMD ["java", "-jar", "auth-server.jar"]