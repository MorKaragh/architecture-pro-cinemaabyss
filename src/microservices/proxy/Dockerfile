FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/proxy-service-1.0.0.jar app.jar
EXPOSE 8000
ENV PORT=8000
ENV MONOLITH_URL=http://monolith:8080
ENV MOVIES_SERVICE_URL=http://movies-service:8081
ENV EVENTS_SERVICE_URL=http://events-service:8082
ENV GRADUAL_MIGRATION=true
ENV MOVIES_MIGRATION_PERCENT=50
ENTRYPOINT ["java","-jar","/app/app.jar"]
