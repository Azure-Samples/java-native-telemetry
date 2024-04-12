package com.azure.examples.quarkus.superhero.resource;

import com.azure.examples.quarkus.superhero.data.ConferenceException;
import com.azure.examples.quarkus.superhero.data.HeroItem;
import com.azure.examples.quarkus.superhero.model.CapeType;
import com.azure.examples.quarkus.superhero.model.Hero;
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
import org.jboss.logging.Logger;

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
public class HeroResource {

  private static final Logger log = Logger.getLogger(HeroResource.class);

  @Inject
  HeroRepository repository;

  @GET
  public List<HeroItem> list(@QueryParam("veggieName") final String originalName,
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

  @GET
  @Path("/i-want-a-raise")
  @Consumes(TEXT_PLAIN)
  public String generateException() {
    log.info("Someone asked for a raise...");
    throw new ConferenceException("This is a generated exception, just for you!");
  }

  @POST
  @Path("/veggie")
  @Consumes(TEXT_PLAIN)
  @Transactional(REQUIRED)
  public Response add(final String veggieName) {
    final Hero hero = Hero.builder()
      .name("SUPER-" + veggieName)
      .originalName(veggieName)
      .capeType(CapeType.SUPERMAN)
      .build();

    final Hero createdHero = repository.create(hero);
    log.info("hero created: " + createdHero);

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
