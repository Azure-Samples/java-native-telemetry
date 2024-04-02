package com.azure.examples.quarkus.metrics;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class RequestContext {

    @Inject
    io.vertx.core.http.HttpServerRequest httpServerRequest;

    public String getSomethingFromRequest() {
        return httpServerRequest.uri();
    }
}
