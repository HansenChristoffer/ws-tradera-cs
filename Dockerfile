    FROM adoptopenjdk/openjdk11:jdk-11.0.9_11.1-alpine-slim
    COPY ./ ./
    COPY  /target/ws-tradera-cs.jar /categoryscraper.jar
    CMD ["java", "-jar", "/categoryscraper.jar"]