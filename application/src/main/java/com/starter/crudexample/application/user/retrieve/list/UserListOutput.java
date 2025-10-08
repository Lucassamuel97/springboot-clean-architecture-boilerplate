package com.starter.crudexample.application.user.retrieve.list;

import java.time.Instant;
import java.util.List;

import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;

public record UserListOutput(
    String id,
    String username,
    String email,
    List<Role> roles,
    boolean active,
    Instant createdAt
) {
    public static UserListOutput from(final User user) {
        return new UserListOutput(
            user.getId().getValue(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.isActive(),
            user.getCreatedAt()
        );
    }
}
