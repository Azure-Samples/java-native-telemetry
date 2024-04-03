package com.azure.examples.springboot.model;

public class Veggie {
    private Long id;
    private String name;
    private String description;

    public Veggie() {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return String.format("Veggie[id=%d, name='%s', description='%s']", id, name, description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
}
