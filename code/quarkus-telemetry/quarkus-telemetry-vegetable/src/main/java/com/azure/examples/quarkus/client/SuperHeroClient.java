package com.azure.examples.quarkus.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Path;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;


@Path("/heroes")
@RegisterRestClient(configKey = "superhero-api")
//@RegisterProvider(MeterProvider.class)
public interface SuperHeroClient {

    @POST
    @Path("/veggie")
    @Consumes(TEXT_PLAIN)
    void notifyAdd(String veggieName);
}
