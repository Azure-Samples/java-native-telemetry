FROM ghcr.io/graalvm/native-image-community:17-muslib AS build
WORKDIR /
# install maven
RUN curl https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar zx
WORKDIR /app

COPY pom.xml .
COPY src src

# compile the native image
RUN --mount=type=cache,target=/root/.m2 /apache-maven-3.9.6/bin/mvn clean native:compile -Pnative

FROM alpine
EXPOSE 8080
COPY --from=build /app/target/spring-boot-telemetry .
CMD sleep 3 ; exec ./spring-boot-telemetry
