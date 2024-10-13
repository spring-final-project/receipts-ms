FROM maven:3.9-eclipse-temurin-22-alpine AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY ./src/main ./src/main

RUN mvn clean package -DskipTests


FROM openjdk:22-jdk-slim

WORKDIR /app

RUN apt-get update
RUN apt-get install libfreetype6-dev -y

COPY --from=build /app/target/receipts-ms-0.0.1.jar /app/receipts-ms-0.0.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/receipts-ms-0.0.1.jar"]