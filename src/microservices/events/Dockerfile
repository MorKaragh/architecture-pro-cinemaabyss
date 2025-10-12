FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/events-service-1.0.0.jar app.jar
EXPOSE 8082
ENV PORT=8082
ENV KAFKA_BROKERS=kafka:9092
ENTRYPOINT ["java","-jar","/app/app.jar"]
