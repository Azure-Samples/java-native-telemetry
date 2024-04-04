package com.azure.examples.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringBootResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootResource.class);

  @GetMapping("/springboot")
  public String springBootGet() {
    LOGGER.info("Spring Boot: GET");
    return "Spring Boot: Hello World!";
  }
}
