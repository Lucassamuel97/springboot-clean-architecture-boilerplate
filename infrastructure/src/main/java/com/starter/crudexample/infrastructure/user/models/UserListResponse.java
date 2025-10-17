package com.starter.crudexample.infrastructure.user.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starter.crudexample.domain.user.Role;

public record UserListResponse(
    @JsonProperty("id") String id,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("roles") List<Role> roles,
    @JsonProperty("active") boolean active,
    @JsonProperty("created_at") String createdAt
) {
}
