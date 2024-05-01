FROM maven:3.8.4-jdk-11 AS builder

WORKDIR ./builder

COPY . .

RUN mvn clean package

FROM openjdk:11-jdk-slim

WORKDIR ./app

COPY --from=builder ./builder/* .

CMD ["java", "-jar", "./target/telbot-1.0.0-SNAPSHOT.jar"]
