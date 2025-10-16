package com.starter.crudexample.infrastructure.user.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starter.crudexample.domain.user.Role;

public record UpdateUserRequest(
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("password") String password,
    @JsonProperty("roles") List<Role> roles,
    @JsonProperty("active") Boolean active
) {
}
