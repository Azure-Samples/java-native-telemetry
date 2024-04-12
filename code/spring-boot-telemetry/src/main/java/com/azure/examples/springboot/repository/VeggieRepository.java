package com.azure.examples.springboot.repository;

import com.azure.examples.springboot.model.Veggie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeggieRepository extends JpaRepository<Veggie, Long> {
}
