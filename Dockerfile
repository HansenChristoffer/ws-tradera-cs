# FROM maven:3-openjdk-11 AS MAVEN_BUILD
# COPY ./ ./
# RUN mvn clean package
# FROM openjdk:11
# COPY -- /target/ws-tradera-cs-1.0-SNAPSHOT-shaded.jar /demo.jar
# EXPOSE 8080
# CMD ["java", "-jar", "/demo.jar"]
FROM adoptopenjdk/openjdk11:jdk-11.0.9_11.1-alpine-slim
COPY ./ ./
COPY  /target/ws-tradera-cs.jar /demo.jar
EXPOSE 8080
CMD ["java", "-jar", "/demo.jar"]