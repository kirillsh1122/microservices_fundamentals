FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY gateway-service/pom.xml gateway-service/pom.xml
RUN mvn -f gateway-service/pom.xml dependency:go-offline
COPY gateway-service/src gateway-service/src
RUN mvn -f gateway-service/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine-3.23
WORKDIR /gateway-service
COPY --from=build /app/gateway-service/target/*.jar gateway-service.jar
EXPOSE 8085
CMD ["java", "-jar", "gateway-service.jar"]