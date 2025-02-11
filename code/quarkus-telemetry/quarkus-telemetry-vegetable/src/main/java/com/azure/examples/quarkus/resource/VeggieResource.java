package com.azure.examples.quarkus.resource;


import com.azure.examples.quarkus.client.SuperHeroClient;
import com.azure.examples.quarkus.data.VeggieItem;
import com.azure.examples.quarkus.data.VeggieNew;
import com.azure.examples.quarkus.model.Veggie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;


@Path("/veggies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class VeggieResource {

    private static final Logger log = Logger.getLogger(VeggieResource.class);

    @Inject
    EntityManager manager;

    @Inject
    Config config;

    @RestClient
    SuperHeroClient superHeroClient;

    @POST
    @Path("/init")
    @Transactional
    public RestResponse<List<VeggieItem>> provision() {
        final VeggieNew tomato = VeggieNew.builder()
            .name("Tomato")
            .description("Red")
            .build();
        final VeggieNew eggplant = VeggieNew.builder()
            .name("Eggplant")
            .description("Purple")
            .build();

        return RestResponse.status(Status.CREATED, asList(addVeggie(tomato), addVeggie(eggplant)));
    }

    @POST
    @Transactional
    public RestResponse<VeggieItem> add(final VeggieNew veggieNew) {
        return RestResponse.status(Status.CREATED, addVeggie(veggieNew));
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public RestResponse<Object> delete(final String veggieId) {
        return find(veggieId)
            .map(veggie -> {
                manager.remove(veggie);
                return RestResponse.status(Status.NO_CONTENT);
            })
            .orElse(RestResponse.status(Status.NOT_FOUND));
    }

    @GET
    public List<VeggieItem> list() {
        log.info("someone asked for a list");
        return manager.createQuery("SELECT l FROM Veggie l").getResultList();
    }

    private Optional<VeggieItem> find(final String veggieId) {
        return Optional.ofNullable(manager.find(Veggie.class, veggieId))
            .map(veggie -> VeggieItem.builder()
                .id(veggie.getId())
                .name(veggie.getName())
                .description(veggie.getDescription())
                .build());
    }

    private VeggieItem addVeggie(final VeggieNew veggieNew) {
        final Veggie veggieToAdd = Veggie.builder()
            .name(veggieNew.getName())
            .description((veggieNew.getDescription()))
            .build();

        final Veggie addedveggie = manager.merge(veggieToAdd);

        final VeggieItem veggieItem = VeggieItem.builder()
            .id(addedveggie.getId())
            .name(addedveggie.getName())
            .description(addedveggie.getDescription())
            .build();

        superHeroClient.notifyAdd(addedveggie.getName());

        return veggieItem;
    }
}
