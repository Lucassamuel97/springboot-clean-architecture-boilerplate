package com.starter.crudexample.infrastructure.user.models;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starter.crudexample.application.user.retrieve.get.GetUserByIdOutput;
import com.starter.crudexample.domain.user.Role;

public record UserResponse(
    @JsonProperty("id") String id,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("roles") List<Role> roles,
    @JsonProperty("active") boolean active,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt,
    @JsonProperty("deleted_at") Instant deletedAt
) {
    public static UserResponse from(final GetUserByIdOutput output) {
        return new UserResponse(
            output.id(),
            output.username(),
            output.email(),
            output.roles(),
            output.active(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
        );
    }
}
