package com.starter.crudexample.infrastructure.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
    @JsonProperty("username") String username,
    @JsonProperty("password") String password
) {
}
