FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY resource-processor/pom.xml resource-processor/pom.xml
RUN mvn -f resource-processor/pom.xml dependency:go-offline
COPY resource-processor/src resource-processor/src
RUN mvn -f resource-processor/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /resource-processor
COPY --from=build /app/resource-processor/target/*.jar resource-processor.jar
EXPOSE 8087
CMD ["java", "-jar", "resource-processor.jar"]