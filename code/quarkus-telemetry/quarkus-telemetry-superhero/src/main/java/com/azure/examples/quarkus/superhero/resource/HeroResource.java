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
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class HeroResource {

  private static final Logger log = Logger.getLogger(HeroResource.class);

  @Inject
  HeroRepository repository;

  @GET
  public List<HeroItem> list(@QueryParam("veggieName") final Optional<String> originalName,
                             @QueryParam("pageIndex") int pageIndex) {
    if (originalName.isEmpty()) {
      log.info("someone asked for a list for index: " + pageIndex);
      return repository.listHeroes(pageIndex).stream()
        .map(this::getHeroItem)
        .collect(Collectors.toList());
    } else {
      return findByOriginalName(originalName.get());
    }
  }

  @GET
  @Path("/i-want-a-raise")
  @Produces(MediaType.TEXT_PLAIN)
  public String generateException() {
    log.info("Someone asked for a raise...");
    throw new ConferenceException("This is a generated exception, just for you!");
  }

  @POST
  @Path("/veggie")
  @Consumes(MediaType.TEXT_PLAIN)
  @Transactional(Transactional.TxType.REQUIRED)
  public RestResponse<HeroItem> add(final String veggieName) {
    final Hero hero = Hero.builder()
      .name("SUPER-" + veggieName)
      .originalName(veggieName)
      .capeType(CapeType.SUPERMAN)
      .build();

    final Hero createdHero = repository.create(hero);
    log.info("hero created: " + createdHero);

    return RestResponse.status(Status.CREATED,
      HeroItem.builder()
      .id(createdHero.getId())
      .name(createdHero.getName())
      .originalName(createdHero.getOriginalName())
      .capeType(createdHero.getCapeType())
      .build());
  }

  private List<HeroItem> findByOriginalName(final String originalName) {
    return repository.findByOriginalName(originalName).stream()
      .map(this::getHeroItem)
      .collect(Collectors.toList());
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
