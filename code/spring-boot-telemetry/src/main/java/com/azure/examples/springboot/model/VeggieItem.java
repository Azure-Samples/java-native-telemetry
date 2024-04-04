package com.azure.examples.springboot.model;

import lombok.Data;

@Data
public class VeggieItem {
    private Long id;
    private String name;
    private String description;
}
