package com.starter.crudexample.application.user.update;

import java.util.Objects;
import java.util.function.Supplier;

import com.starter.crudexample.domain.Identifier;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.PasswordHasher;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.domain.validation.handler.Notification;

public non-sealed class DefaultUpdateUserUseCase extends UpdateUserUseCase {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public DefaultUpdateUserUseCase(final UserGateway userGateway, final PasswordHasher passwordHasher) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    @Override
    public UpdateUserOutput execute(final UpdateUserCommand aCommand) {
        final var anId = UserID.from(aCommand.id());
        final var aUsername = aCommand.username();
        final var anEmail = aCommand.email();
        final var newPasswordRaw = aCommand.password();
        final var roles = aCommand.roles();
        final var isActive = aCommand.active();

        final var aUser = this.userGateway.findById(anId).orElseThrow(notFound(anId));

        final var notification = Notification.create();

        // Primeiro atualiza com a senha antiga (caso não tenha nova) para validar os demais campos
        final var passwordToApply = newPasswordRaw == null ? aUser.getPassword() : newPasswordRaw;
        aUser.update(aUsername, anEmail, passwordToApply, roles, isActive);
        aUser.validate(notification);

        if (notification.hasError()) {
            notify(anId, notification);
        }

        // Se veio nova senha, gerar hash e aplicar novamente (sem nova validação estrutural)
        if (newPasswordRaw != null) {
            final var hashed = passwordHasher.hash(newPasswordRaw);
            aUser.update(aUser.getUsername(), aUser.getEmail(), hashed, aUser.getRoles(), aUser.isActive());
        }

        return UpdateUserOutput.from(this.userGateway.update(aUser));
    }

    private void notify(final Identifier anId, final Notification notification) {
        throw new NotificationException("Could not update Aggregate User %s".formatted(anId.getValue()), notification);
    }

    private Supplier<NotFoundException> notFound(final UserID anId) {
        return () -> NotFoundException.with(User.class, anId);
    }
}
