package com.starter.crudexample.application.user.retrieve.list;

import java.util.Objects;

import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.UserGateway;

public non-sealed class DefaultListUsersUseCase extends ListUsersUseCase {

    private final UserGateway userGateway;

    public DefaultListUsersUseCase(final UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway);
    }

    @Override
    public Pagination<UserListOutput> execute(final SearchQuery aQuery) {
        return this.userGateway.findAll(aQuery)
            .map(UserListOutput::from);
    }
}
