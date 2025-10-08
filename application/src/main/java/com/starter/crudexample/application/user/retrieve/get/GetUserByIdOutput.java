package com.starter.crudexample.application.user.retrieve.get;

import java.time.Instant;
import java.util.List;

import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;

public record GetUserByIdOutput(
    String id,
    String username,
    String email,
    List<Role> roles,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {
    public static GetUserByIdOutput from(final User user) {
        return new GetUserByIdOutput(
            user.getId().getValue(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getDeletedAt()
        );
    }
}
