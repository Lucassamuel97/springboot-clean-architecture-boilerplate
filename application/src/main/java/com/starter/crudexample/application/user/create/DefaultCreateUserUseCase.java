package com.starter.crudexample.application.user.create;

import java.util.Objects;

import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.validation.handler.Notification;

public non-sealed class DefaultCreateUserUseCase extends CreateUserUseCase {

    private final UserGateway userGateway;

    public DefaultCreateUserUseCase(final UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public CreateUserOutput execute(final CreateUserCommand aCommand) {
        final var aUsername = aCommand.username();
        final var anEmail = aCommand.email();
        final var aPassword = aCommand.password();
        final var roles = aCommand.roles();
        final var isActive = aCommand.active();

        final var notification = Notification.create();
        final var aUser = User.newUser(aUsername, anEmail, aPassword, roles, isActive);
        aUser.validate(notification);

        if (notification.hasError()) {
            notify(notification);
        }
        return CreateUserOutput.from(this.userGateway.create(aUser));
    }

    private void notify(Notification notification) {
        throw new NotificationException("Could not create Aggregate User", notification);
    }
}
