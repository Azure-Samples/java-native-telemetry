package com.azure.examples.quarkus.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/heroes")
@RegisterRestClient(configKey = "superhero-api")
public interface SuperHeroClient {

    @POST
    @Path("/veggie")
    @Consumes(MediaType.TEXT_PLAIN)
    void notifyAdd(String veggieName);
}
