FROM maven:3.8.4-jdk-11 AS builder

WORKDIR ./builder

COPY . .

CMD ["mvn", "spring-boot:run"]
