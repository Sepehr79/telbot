FROM maven:3.8.4-jdk-11 AS builder

WORKDIR ./builder

COPY . .

RUN mvn clean package

FROM openjdk:11-jdk-slim

WORKDIR ./app

COPY --from=builder ./builder/target/* app.jar
COPY --from=builder ./builder/lib/camel-telegram-3.18.9.jar ./lib/camel-telegram-3.18.9.jar

CMD ["java", "-jar", "app.jar"]
