package com.azure.examples.quarkus.superhero.resource;

import com.azure.examples.quarkus.superhero.data.HeroItem;
import com.azure.examples.quarkus.superhero.model.Hero;
import com.azure.examples.quarkus.superhero.model.CapeType;
import com.azure.examples.quarkus.superhero.repository.HeroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static java.util.stream.Collectors.toList;

@Path("/heroes")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@ApplicationScoped
@Slf4j
public class HeroResource {

  @Inject
  HeroRepository repository;

  @GET
  public List<HeroItem> list(@QueryParam("legumeName") final String originalName,
                             @QueryParam("pageIndex") int pageIndex) {
    if (originalName == null || originalName.isBlank()) {
      log.info("someone asked for a list for index: " + pageIndex);
      return repository.listHeroes(pageIndex).stream()
        .map(this::getHeroItem)
        .collect(toList());
    } else {
      return findByOriginalName(originalName);
    }
  }

  @POST
  @Path("/legume")
  @Consumes(TEXT_PLAIN)
  @Transactional(REQUIRED)
  public Response add(final String legumeName) {
    final Hero hero = Hero.builder()
      .name("SUPER-" + legumeName)
      .originalName(legumeName)
      .capeType(CapeType.SUPERMAN)
      .build();

    final Hero createdHero = repository.create(hero);
    log.info("hero created: {}", createdHero);

    return Response.status(CREATED)
      .entity(HeroItem.builder()
        .id(createdHero.getId())
        .name(createdHero.getName())
        .originalName(createdHero.getOriginalName())
        .capeType(createdHero.getCapeType())
        .build())
      .build();
  }

  private List<HeroItem> findByOriginalName(final String originalName) {
    return repository.findByOriginalName(originalName).stream()
      .map(this::getHeroItem)
      .collect(toList());
  }

  private HeroItem getHeroItem(Hero h) {
    return HeroItem.builder()
      .name(h.getName())
      .originalName(h.getOriginalName())
      .id(h.getId())
      .capeType(h.getCapeType())
      .build();
  }
}
