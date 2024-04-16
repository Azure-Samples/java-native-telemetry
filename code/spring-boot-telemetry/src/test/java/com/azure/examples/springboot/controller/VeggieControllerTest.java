package com.azure.examples.springboot.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.azure.examples.springboot.SpringBootVeggieApplication;
import com.azure.examples.springboot.data.VeggieItem;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  classes = {SpringBootVeggieApplication.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class VeggieControllerTest {

  @Autowired
  private TestRestTemplate testRestTemplate;

}
