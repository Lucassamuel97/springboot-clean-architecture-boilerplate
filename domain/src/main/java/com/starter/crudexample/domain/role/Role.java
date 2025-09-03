package com.starter.crudexample.domain.role;

import java.io.Serializable;
import java.util.UUID;

public class Role implements Serializable {

    private UUID id;
    private RoleName name;

    public Role() {
    }

    public Role(RoleName name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}
