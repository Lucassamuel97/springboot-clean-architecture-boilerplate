package com.starter.crudexample.application.user.create;

import java.util.Objects;

import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.PasswordHasher;
import com.starter.crudexample.domain.validation.handler.Notification;

public non-sealed class DefaultCreateUserUseCase extends CreateUserUseCase {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public DefaultCreateUserUseCase(final UserGateway userGateway, final PasswordHasher passwordHasher) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    @Override
    public CreateUserOutput execute(final CreateUserCommand aCommand) {
        final var aUsername = aCommand.username();
        final var anEmail = aCommand.email();
        final var rawPassword = aCommand.password();
        final var roles = aCommand.roles();
        final var isActive = aCommand.active();

        final var notification = Notification.create();
        final var userDraft = User.newUser(aUsername, anEmail, rawPassword, roles, isActive);
        userDraft.validate(notification);

        if (notification.hasError()) {
            notify(notification);
        }

        // Hash após validação bem-sucedida
        final var hashed = passwordHasher.hash(rawPassword);
        final var userToPersist = User.with(
            userDraft.getId(),
            userDraft.getUsername(),
            userDraft.getEmail(),
            hashed,
            userDraft.getRoles(),
            userDraft.isActive(),
            userDraft.getCreatedAt(),
            userDraft.getUpdatedAt(),
            userDraft.getDeletedAt()
        );

        return CreateUserOutput.from(this.userGateway.create(userToPersist));
    }

    private void notify(Notification notification) {
        throw new NotificationException("Could not create Aggregate User", notification);
    }
}
