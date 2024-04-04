package com.azure.examples.springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.azure.examples.springboot.service.VeggieService;

@SpringBootApplication
public class MySpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringBootApplication.class, args);
  }

  @Bean
  CommandLineRunner commandLineRunner(VeggieService veggieService) {
      return args -> {
          veggieService.initializeDatabase();          
      };
  }
  
}
