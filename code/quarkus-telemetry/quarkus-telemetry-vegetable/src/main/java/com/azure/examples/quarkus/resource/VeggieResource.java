package com.azure.examples.quarkus.resource;


import com.azure.examples.quarkus.client.SuperHeroClient;
import com.azure.examples.quarkus.data.VeggieItem;
import com.azure.examples.quarkus.data.VeggieNew;
import com.azure.examples.quarkus.model.Veggie;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.Arrays.asList;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/veggies")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@ApplicationScoped
@Slf4j
public class VeggieResource {

  @Inject
  EntityManager manager;

  @Inject
  Config config;

  @RestClient
  SuperHeroClient superHeroClient;

  @POST
  @Path("/init")
  public Response provision() {
    final VeggieNew carrot = VeggieNew.builder()
      .name("Carrot")
      .description("Root vegetable, usually orange")
      .build();
    final VeggieNew zucchini = VeggieNew.builder()
      .name("Zucchini")
      .description("Summer squash")
      .build();
    return Response.status(CREATED).entity(asList(
      add(carrot),
      add(zucchini))).build();
  }

  @POST
  @Transactional
  public Response add(final VeggieNew veggieNew) {
    return Response.status(CREATED).entity(addVeggie(veggieNew)).build();
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(final String veggieId) {
    return find(veggieId)
      .map(veggie -> {
        manager.remove(veggie);
        return Response.status(NO_CONTENT).build();
      })
      .orElse(Response.status(NOT_FOUND).build());
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
