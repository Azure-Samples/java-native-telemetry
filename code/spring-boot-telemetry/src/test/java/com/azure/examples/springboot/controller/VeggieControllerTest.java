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

  @Test
  void testAddVeggie() {
    // given
    VeggieItem veggie = new VeggieItem();
    veggie.setName("Tomato");
    veggie.setDescription("Red");

    // when
    ResponseEntity<VeggieItem> response = testRestTemplate.postForEntity("/veggies", veggie, VeggieItem.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    VeggieItem veggieItem = response.getBody();
    assertThat(veggieItem.getName()).isEqualTo("Tomato");
    assertThat(veggieItem.getDescription()).isEqualTo("Red");
  }

  @Test
  void testGetAllVeggies() {
    // when
    ResponseEntity<VeggieItem[]> response = testRestTemplate.getForEntity("/veggies", VeggieItem[].class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    VeggieItem[] veggieItems = response.getBody();
    assertThat(veggieItems).hasSize(3);
    assertThat(veggieItems).extracting("name").contains("Carrot", "Broccoli", "Cauliflower");
  }
  @Test
  void testGetVeggieById() {
    // given
    Long id = 2L;

    // when
    ResponseEntity<VeggieItem> response = testRestTemplate.getForEntity("/veggies/{id}", VeggieItem.class, id);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    VeggieItem veggieItem = response.getBody();
    assertThat(veggieItem.getName()).isEqualTo("Broccoli");
    assertThat(veggieItem.getDescription()).isEqualTo("Green");
  }

  @Test
  void testDeleteVeggie() {
    // given
    Long id = 1L;

    // when
    testRestTemplate.delete("/veggies/{id}", id);

    // then
    ResponseEntity<VeggieItem[]> response = testRestTemplate.getForEntity("/veggies", VeggieItem[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    VeggieItem[] veggieItems = response.getBody();
    assertThat(veggieItems).hasSize(2);
    assertThat(veggieItems).extracting("name").contains("Broccoli", "Cauliflower");
  }
  @Test
  void testDeleteVeggieNotFound() {
    // given
    Long id = 99L;

    // when
    testRestTemplate.delete("/veggies/{id}", id);

    // then
    ResponseEntity<VeggieItem[]> response = testRestTemplate.getForEntity("/veggies", VeggieItem[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    VeggieItem[] veggieItems = response.getBody();
    assertThat(veggieItems).hasSize(3);
  }
}
