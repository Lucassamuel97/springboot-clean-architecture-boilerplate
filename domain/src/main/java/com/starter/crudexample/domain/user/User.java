package com.starter.crudexample.domain.user;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


import com.starter.crudexample.domain.AggregateRoot;
import com.starter.crudexample.domain.utils.InstantUtils;
import com.starter.crudexample.domain.validation.ValidationHandler;

public class User extends AggregateRoot<UserID> {

    private String username;
    private String email;
    private String password;
    private List<Role> roles;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public User(
        final UserID anId,
        final String aUsername,
        final String anEmail,
        final String aPassword,
        final List<Role> aRoles,
        final boolean isActive,
        final Instant aCreatedAt,
        final Instant aUpdatedAt,
        final Instant aDeletedAt
    ) {
        super(anId);
        this.username = aUsername;
        this.email = anEmail;
        this.password = aPassword;
        this.roles = aRoles;
        this.active = isActive;
        this.createdAt = Objects.requireNonNull(aCreatedAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(aUpdatedAt, "'updatedAt' should not be null");
    this.deletedAt = aDeletedAt;
    }

    public static User newUser(
        final String aUsername,
        final String anEmail,
        final String aPassword,
        final List<Role> aRoles,
        final boolean isActive
    ) {
        final var id = UserID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = isActive ? null : now;
        return new User(
            id,
            aUsername,
            anEmail,
            aPassword,
            aRoles,
            isActive,
            now,
            now,
            deletedAt
        );
    }
    public static User with(
        final UserID anId,
        final String aUsername,
        final String anEmail,
        final String aPassword,
        final List<Role> aRoles,
        final boolean isActive,
        final Instant aCreatedAt,
        final Instant aUpdatedAt,
        final Instant aDeletedAt
    ) {
        return new User(
            anId,
            aUsername,
            anEmail,
            aPassword,
            aRoles,
            isActive,
            aCreatedAt,
            aUpdatedAt,
            aDeletedAt
        );
    }

    public static User with(final User aUser) {
        return new User(
            aUser.id,
            aUser.username,
            aUser.email,
            aUser.password,
            aUser.roles,
            aUser.active,
            aUser.createdAt,
            aUser.updatedAt,
            aUser.deletedAt
        );
    }
    
    @Override
    public void validate(final ValidationHandler handler) {
        new UserValidator(this, handler).validate();
    }

    public User deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public User activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles == null ? null : Collections.unmodifiableList(roles);
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public UserID getId() { return id; }

    public User update(
        final String aUsername,
        final String anEmail,
        final String aPassword,
        final List<Role> aRoles,
        final boolean isActive
    ) {
        this.username = aUsername;
        this.email = anEmail;
        this.password = aPassword;
        this.roles = aRoles;
        this.active = isActive;
        this.deletedAt = isActive ? null : (this.deletedAt == null ? InstantUtils.now() : this.deletedAt);
        this.updatedAt = InstantUtils.now();
        return this;
    }
}
