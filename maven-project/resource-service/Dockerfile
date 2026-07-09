FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY resource-service/pom.xml resource-service/pom.xml
RUN mvn -f resource-service/pom.xml dependency:go-offline
COPY resource-service/src resource-service/src
RUN mvn -f resource-service/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /resource-service
COPY --from=build /app/resource-service/target/*.jar resource-service.jar
EXPOSE 8080
CMD ["java", "-jar", "resource-service.jar"]