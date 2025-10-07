package com.starter.crudexample.application.user.create;

import java.util.List;

import com.starter.crudexample.domain.user.Role;

public record CreateUserCommand(
    String username,
    String email,
    String password,
    List<Role> roles,
    boolean active
) {
    public static CreateUserCommand with(
        final String aUsername,
        final String anEmail,
        final String aPassword,
        final List<Role> aRoles,
        final boolean isActive
    ) {
        return new CreateUserCommand(aUsername, anEmail, aPassword, aRoles, isActive);
    }
}
