package com.starter.crudexample.infrastructure.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.starter.crudexample.application.user.create.CreateUserCommand;
import com.starter.crudexample.application.user.create.CreateUserUseCase;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.infrastructure.api.UserAPI;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;

@RestController
public class UserController implements UserAPI {

    private final CreateUserUseCase createUserUseCase;

    public UserController(final CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
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
}
