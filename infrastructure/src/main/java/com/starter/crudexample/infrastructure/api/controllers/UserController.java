package com.starter.crudexample.infrastructure.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.starter.crudexample.application.user.create.CreateUserCommand;
import com.starter.crudexample.application.user.create.CreateUserUseCase;
import com.starter.crudexample.application.user.delete.DeleteUserUseCase;
import com.starter.crudexample.application.user.retrieve.get.GetUserByIdQuery;
import com.starter.crudexample.application.user.retrieve.get.GetUserByIdUseCase;
import com.starter.crudexample.application.user.retrieve.list.ListUsersUseCase;
import com.starter.crudexample.application.user.update.UpdateUserCommand;
import com.starter.crudexample.application.user.update.UpdateUserUseCase;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.infrastructure.api.UserAPI;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;
import com.starter.crudexample.infrastructure.user.models.UpdateUserRequest;
import com.starter.crudexample.infrastructure.user.models.UserListResponse;
import com.starter.crudexample.infrastructure.user.presenter.UserPresenter;

@RestController
public class UserController implements UserAPI {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ListUsersUseCase listUsersUseCase;

    public UserController(
        final CreateUserUseCase createUserUseCase,
        final GetUserByIdUseCase getUserByIdUseCase,
        final UpdateUserUseCase updateUserUseCase,
        final DeleteUserUseCase deleteUserUseCase,
        final ListUsersUseCase listUsersUseCase
    ) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
        this.getUserByIdUseCase = Objects.requireNonNull(getUserByIdUseCase);
        this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase);
        this.deleteUserUseCase = Objects.requireNonNull(deleteUserUseCase);
        this.listUsersUseCase = Objects.requireNonNull(listUsersUseCase);
    }

    @Override
    public ResponseEntity<?> create(CreateUserRequest input) {
        final var roles = input.roles() == null ? List.<Role>of() : input.roles();
        final var active = input.active() == null ? true : input.active();
        final var aCommand = CreateUserCommand.with(
            input.username(),
            input.email(),
            input.password(),
            roles,
            active
        );

        final var output = this.createUserUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/users/" + output.id())).body(output);
    }

    @Override
    public ResponseEntity<?> getById(String id) {
        final var aQuery = GetUserByIdQuery.with(id);
        final var output = this.getUserByIdUseCase.execute(aQuery);
        return ResponseEntity.ok(UserPresenter.present(output));
    }

    @Override
    public ResponseEntity<?> updateById(String id, UpdateUserRequest aBody) {
        final var roles = aBody.roles() == null ? List.<Role>of() : aBody.roles();
        final var active = aBody.active() == null ? true : aBody.active();
        final var aCommand = UpdateUserCommand.with(
            id,
            aBody.username(),
            aBody.email(),
            aBody.password(),
            roles,
            active
        );

        final var output = this.updateUserUseCase.execute(aCommand);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(String id) {
        this.deleteUserUseCase.execute(id);
    }

    @Override
    public Pagination<UserListResponse> list(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction
    ) {
        return this.listUsersUseCase.execute(new SearchQuery(page, perPage, search, sort, direction))
                .map(UserPresenter::present);
    }
}
