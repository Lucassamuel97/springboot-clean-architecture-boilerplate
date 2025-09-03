package com.starter.crudexample.domain.item;

import java.io.Serializable;
import java.util.UUID;

public class Item implements Serializable {

    private UUID id;
    private String name;
    private String description;

    public Item() {
    }

    public Item(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
