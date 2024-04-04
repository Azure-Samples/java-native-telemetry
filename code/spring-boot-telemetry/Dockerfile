# Stage 1: Build the application
FROM maven:3.8.2-openjdk-17-slim AS build

WORKDIR /workspace/app

COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN mvn package -Pnative -DskipTests

# Stage 2: Run the native application
FROM debian:stretch-slim
EXPOSE 8080
COPY --from=build /workspace/app/target/*-runner /app
ENTRYPOINT ["./app"]