package com.starter.crudexample.application.user.retrieve.get;

import java.util.Objects;
import java.util.function.Supplier;

import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;

public class DefaultGetUserByIdUseCase extends GetUserByIdUseCase {

    private final UserGateway userGateway;

    public DefaultGetUserByIdUseCase(final UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public GetUserByIdOutput execute(final GetUserByIdQuery query) {
        final var anId = UserID.from(query.id());
        final var user = this.userGateway.findById(anId).orElseThrow(notFound(anId));
        return GetUserByIdOutput.from(user);
    }

    private Supplier<NotFoundException> notFound(final UserID anId) {
        return () -> NotFoundException.with(User.class, anId);
    }
}
