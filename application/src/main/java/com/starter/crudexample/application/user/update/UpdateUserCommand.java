package com.starter.crudexample.application.user.update;

import java.util.List;

import com.starter.crudexample.domain.user.Role;

public record UpdateUserCommand(
    String id,
    String username,
    String email,
    String password, // pode ser null para não alterar
    List<Role> roles,
    boolean active
) {
    public static UpdateUserCommand with(
        final String id,
        final String username,
        final String email,
        final String password,
        final List<Role> roles,
        final boolean active
    ) {
        return new UpdateUserCommand(id, username, email, password, roles, active);
    }
}
