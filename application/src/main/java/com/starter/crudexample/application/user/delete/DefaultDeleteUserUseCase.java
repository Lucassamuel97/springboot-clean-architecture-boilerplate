package com.starter.crudexample.application.user.delete;

import java.util.Objects;

import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;

public non-sealed class DefaultDeleteUserUseCase extends DeleteUserUseCase {

    private final UserGateway userGateway;

    public DefaultDeleteUserUseCase(final UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public void execute(final String anIn) {
        this.userGateway.deleteById(UserID.from(anIn));
    }
}
