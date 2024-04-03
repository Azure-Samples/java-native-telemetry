package com.azure.examples.springboot.service;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.azure.examples.springboot.model.Veggie;

@Service
@Transactional
public class VeggieService {
    private final JdbcTemplate jdbcTemplate;

    public VeggieService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    RowMapper<Veggie> rowMapper = (rs, rowNum) -> {
        Veggie veggie = new Veggie();
        veggie.setId(rs.getLong("id"));
        veggie.setName(rs.getString("name"));
        veggie.setDescription(rs.getString("description"));
        return veggie;
    };

    public void initializeDatabase() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS veggies");
        jdbcTemplate.execute("CREATE TABLE veggies(id SERIAL, name VARCHAR(255), description VARCHAR(255))");
        jdbcTemplate.update("INSERT INTO veggies(name, description) VALUES (?, ?)", "Carrot", "Orange");
        jdbcTemplate.update("INSERT INTO veggies(name, description) VALUES (?, ?)", "Broccoli", "Green");
        jdbcTemplate.update("INSERT INTO veggies(name, description) VALUES (?, ?)", "Cauliflower", "White");
    }

    public Veggie addVeggie(Veggie veggie) {
        jdbcTemplate.update("INSERT INTO veggies(name, description) VALUES (?, ?)", veggie.getName(), veggie.getDescription());
        return veggie;
    }

    public void deleteVeggie(Long id) {
        jdbcTemplate.update("DELETE FROM veggies WHERE id = ?", id);
    }

    public List<Veggie> getAllVeggies() {
        return jdbcTemplate.query("SELECT * FROM veggies", rowMapper);
    }

    public Veggie getVeggieById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM veggies WHERE id = ?", rowMapper, id);
    }
}
