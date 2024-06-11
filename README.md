# Telemetry for GraalVM native images

This repository contains code demonstrating how to enable OpenTelemetry features for GraalVM native images on Azure:
* [Spring Boot](./code/spring-boot-telemetry/README.md)
* [Quarkus](./code/quarkus-telemetry/README.md)

Deploy the sample to Azure Container Apps with `azd`.

```bash
 azd auth login --use-device-code
 azd provision
 azd up
```

The Application Map in Azure Application Insights will look like this:

[!Application Map](application-map.png)

And give you traces like:

[!End to End Transaction - Trace](e2e-transaction.png)

Delete all resources with `azd down` afterwards.

