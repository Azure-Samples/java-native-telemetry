package com.azure.examples.quarkus.resource;


import com.azure.examples.quarkus.client.SuperHeroClient;
import com.azure.examples.quarkus.data.LegumeItem;
import com.azure.examples.quarkus.data.LegumeNew;
import com.azure.examples.quarkus.model.Legume;
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

@Path("/legumes")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@ApplicationScoped
@Slf4j
public class LegumeResource {

  @Inject
  EntityManager manager;

  @Inject
  Config config;

  @RestClient
  SuperHeroClient superHeroClient;

  @POST
  @Path("/init")
  public Response provision() {
    final LegumeNew carrot = LegumeNew.builder()
      .name("Carrot")
      .description("Root vegetable, usually orange")
      .build();
    final LegumeNew zucchini = LegumeNew.builder()
      .name("Zucchini")
      .description("Summer squash")
      .build();
    return Response.status(CREATED).entity(asList(
      add(carrot),
      add(zucchini))).build();
  }

  @POST
  @Transactional
  public Response add(final LegumeNew legumeNew) {
    return Response.status(CREATED).entity(addLegume(legumeNew)).build();
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(final String legumeId) {
    return find(legumeId)
      .map(legume -> {
        manager.remove(legume);
        return Response.status(NO_CONTENT).build();
      })
      .orElse(Response.status(NOT_FOUND).build());
  }

  @GET
  public List<LegumeItem> list() {
    log.info("someone asked for a list");
    return manager.createQuery("SELECT l FROM Legume l").getResultList();
  }

  private Optional<LegumeItem> find(final String legumeId) {
    return Optional.ofNullable(manager.find(Legume.class, legumeId))
      .map(legume -> LegumeItem.builder()
        .id(legume.getId())
        .name(legume.getName())
        .description(legume.getDescription())
        .build());
  }

  private LegumeItem addLegume(final LegumeNew legumeNew) {
    final Legume legumeToAdd = Legume.builder()
      .name(legumeNew.getName())
      .description((legumeNew.getDescription()))
      .build();

    final Legume addedLegume = manager.merge(legumeToAdd);

    final LegumeItem legumeItem = LegumeItem.builder()
      .id(addedLegume.getId())
      .name(addedLegume.getName())
      .description(addedLegume.getDescription())
      .build();

    superHeroClient.notifyAdd(addedLegume.getName());

    return legumeItem;
  }
}
