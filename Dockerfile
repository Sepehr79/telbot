FROM maven:3.8.4-jdk-11 AS builder

WORKDIR ./builder

COPY . .

RUN mvn clean package

FROM openjdk:11-jdk-slim

WORKDIR ./app

COPY --from=builder target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
