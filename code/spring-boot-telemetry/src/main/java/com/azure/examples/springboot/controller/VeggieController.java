package com.azure.examples.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.azure.examples.springboot.data.VeggieItem;
import com.azure.examples.springboot.service.VeggieService;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/veggies")
public class VeggieController {
    private final Logger logger = LoggerFactory.getLogger(VeggieController.class);

    private final VeggieService veggieService;
    private final RestTemplate restTemplate;

    @Value("${client.superhero.url}")
    private String restClientUrl;

    VeggieController(VeggieService veggieService, RestTemplate restTemplate) {
        this.veggieService = veggieService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/init")
    public ResponseEntity<String> initializeDatabase() {
        veggieService.initializeDatabase();
        return ResponseEntity.ok("Database initialized with 3 veggie items");
    }

    @PostMapping
    public ResponseEntity<VeggieItem> addVeggie(@RequestBody VeggieItem veggie) {
        VeggieItem savedVeggie = veggieService.addVeggie(veggie);
        try {
            String url = restClientUrl + "/heroes/veggie";
            restTemplate.postForEntity(url, savedVeggie.getName(), String.class);
        } catch (Exception e) {
            logger.error("Failed to notify SuperHero API", e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVeggie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVeggie(@PathVariable("id") String id) {
        veggieService.deleteVeggie(id);
        return ResponseEntity.ok("Veggie deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<VeggieItem>> getAllVeggies() {
        List<VeggieItem> veggies = veggieService.getAllVeggies();
        return ResponseEntity.ok(veggies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeggieItem> getVeggieById(@PathVariable("id") String id) {
        VeggieItem veggie = veggieService.getVeggieById(id);
        if (veggie != null) {
            return ResponseEntity.ok(veggie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
