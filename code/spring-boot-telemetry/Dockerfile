FROM ghcr.io/graalvm/native-image-community:22-muslib AS build
WORKDIR /
# install maven
RUN curl https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar zx
WORKDIR /app

COPY pom.xml .
COPY src src

# set the environment variables
ENV DATABASE_URL=${DATABASE_URL}
ENV DATABASE_USERNAME=${DATABASE_USERNAME}
ENV DATABASE_PASSWORD=${DATABASE_PASSWORD}

# compile the native image
RUN --mount=type=cache,target=/root/.m2 /apache-maven-3.9.6/bin/mvn clean native:compile -Pnative -DDATABASE_PASSWORD=${DATABASE_PASSWORD} -DDATABASE_URL=${DATABASE_URL} -DDATABASE_USERNAME=${DATABASE_USERNAME} -DAPPLICATIONINSIGHTS_CONNECTION_STRING=${APPLICATIONINSIGHTS_CONNECTION_STRING}

FROM alpine
EXPOSE 8080
COPY --from=build /app/target/spring-boot-telemetry .
CMD sleep 3 ; exec ./spring-boot-telemetry
