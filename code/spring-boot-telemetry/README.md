# Spring Boot


## Run the application in a JVM mode

```shell script
mvn package
cd target
java -jar spring-boot-telemetry.jar
```


## Run the application with GraalVM native

```shell script
mvn -Pnative spring-boot:build-image -Dspring-boot.build-image.imageName=spring-boot-native
docker run -p 8080:8080 spring-boot-native
```
