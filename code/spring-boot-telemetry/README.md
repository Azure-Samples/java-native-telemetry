# Spring Boot

## Start the application locally with docker compose

Run `docker compose up` to start PostgreSQL locally and build the app locally within docker.


## Configure the Database Connection

Store the postgresql database connection details in an .env file at 'code/spring-boot-telemetry/.env'.

DATABASE_PASSWORD=db_password
DATABASE_URL=db_url
DATABASE_USERNAME=db_admin

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

## Enable the telemetry for GraalVM native

Add the [spring-cloud-azure-starter-monitor](https://central.sonatype.com/artifact/com.azure.spring/spring-cloud-azure-starter-monitor) dependency to the `pom.xml` file.

It is a Microsoft distribution of the [OpenTelemetry Spring Boot starter](https://opentelemetry.io/docs/languages/java/automatic/spring-boot/#opentelemetry-spring-boot-starter).

You also need to add the OpenTelemetry BOM to your `pom.xml` file:

```xml
<dependencyManagement>
    <dependencies>
      <dependency>
         <groupId>io.opentelemetry</groupId>           
         <artifactId>opentelemetry-bom</artifactId>                   
         <version>{opentelemetry.version}</version>           
         <type>pom</type>
      </dependency>     
    </dependencies>   
</dependencyManagement>
```

Check the last version of the OpenTelemetry BOM in the [Maven Central Repository](https://search.maven.org/artifact/io.opentelemetry/opentelemetry-bom).

Follow [these instructions](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/developer-guide-overview#configuring-spring-boot-3) related to Spring Boot 3 for signed JARs.

You have to configure a connection string to send the OpenTelemetry data to the Azure portal. 

[You can find the connection string in the Azure portal](./../../Azure-connection-string.md).

You can configure the connection string in the `application.properties` file:

```properties
applicationinsights.connection.string=InstrumentationKey=00000000-0000-0000-0000-000000000000
```

You can also use the `APPLICATIONINSIGHTS_CONNECTION_STRING` environment variable.
