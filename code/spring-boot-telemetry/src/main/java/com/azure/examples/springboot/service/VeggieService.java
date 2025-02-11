package com.azure.examples.springboot.service;

import com.azure.examples.springboot.data.VeggieItem;
import com.azure.examples.springboot.model.Veggie;
import com.azure.examples.springboot.repository.VeggieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VeggieService {
  private final VeggieRepository veggieRepository;

  public VeggieService(VeggieRepository veggieRepository) {
    this.veggieRepository = veggieRepository;
  }

    @Transactional
    public void initializeDatabase() {
        Veggie carrot = new Veggie("Carrot", "Orange");
        Veggie broccoli = new Veggie("Broccoli", "Green");
        Veggie cauliflower = new Veggie("Cauliflower", "White");

        veggieRepository.save(carrot);
        veggieRepository.save(broccoli);
        veggieRepository.save(cauliflower);
    }

    @Transactional
    public VeggieItem addVeggie(VeggieItem veggie) {
        Veggie veggieEntity = new Veggie(veggie.getName(), veggie.getDescription());
        Veggie savedVeggie = veggieRepository.save(veggieEntity);
        return new VeggieItem(savedVeggie.getId(), savedVeggie.getName(), savedVeggie.getDescription());
    }

    @Transactional
    public void deleteVeggie(String id) {
        veggieRepository.deleteById(id);
    }

    public List<VeggieItem> getAllVeggies() {
        List<Veggie> veggies = veggieRepository.findAll();
        return veggies.stream().map(veggie -> new VeggieItem(veggie.getId(), veggie.getName(), veggie.getDescription()))
                .toList();
    }

    public VeggieItem getVeggieById(String id) {
        return veggieRepository.findById(id)
                .map(veggie -> new VeggieItem(veggie.getId(), veggie.getName(), veggie.getDescription()))
                .orElse(null);
    }
}
