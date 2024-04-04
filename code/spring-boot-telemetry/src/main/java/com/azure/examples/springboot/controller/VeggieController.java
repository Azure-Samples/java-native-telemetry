package com.azure.examples.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.azure.examples.springboot.model.VeggieItem;
import com.azure.examples.springboot.service.VeggieService;

import java.util.List;

@RestController
@RequestMapping("/veggies")
public class VeggieController {

    @Autowired
    private VeggieService veggieService;

    @PostMapping("/init")
    public ResponseEntity<String> initializeDatabase() {
        veggieService.initializeDatabase();
        return ResponseEntity.ok("Database initialized with 3 veggie items");
    }

    @PostMapping
    public ResponseEntity<VeggieItem> addVeggie(@RequestBody VeggieItem veggie) {
        VeggieItem savedVeggie = veggieService.addVeggie(veggie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVeggie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVeggie(@PathVariable("id") Long id) {
        veggieService.deleteVeggie(id);
        return ResponseEntity.ok("Veggie deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<VeggieItem>> getAllVeggies() {
        List<VeggieItem> veggies = veggieService.getAllVeggies();
        return ResponseEntity.ok(veggies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeggieItem> getVeggieById(@PathVariable("id") Long id) {
        VeggieItem veggie = veggieService.getVeggieById(id);
        if (veggie != null) {
            return ResponseEntity.ok(veggie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}