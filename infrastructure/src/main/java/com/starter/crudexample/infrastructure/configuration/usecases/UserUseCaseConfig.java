package com.starter.crudexample.infrastructure.configuration.usecases;

import java.util.Objects;

import com.starter.crudexample.application.user.retrieve.get.DefaultGetUserByIdUseCase;
import com.starter.crudexample.application.user.retrieve.get.GetUserByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.starter.crudexample.application.user.create.CreateUserUseCase;
import com.starter.crudexample.application.user.create.DefaultCreateUserUseCase;
import com.starter.crudexample.domain.user.PasswordHasher;
import com.starter.crudexample.domain.user.UserGateway;

@Configuration
public class UserUseCaseConfig {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public UserUseCaseConfig(final UserGateway userGateway, final PasswordHasher passwordHasher) {
        this.userGateway = Objects.requireNonNull(userGateway);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    @Bean
    public CreateUserUseCase createUserUseCase() {
        return new DefaultCreateUserUseCase(userGateway, passwordHasher);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase() {
        return new DefaultGetUserByIdUseCase(userGateway);
    }
}
