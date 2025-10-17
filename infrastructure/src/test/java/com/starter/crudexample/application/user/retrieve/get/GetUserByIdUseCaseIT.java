package com.starter.crudexample.application.user.retrieve.get;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@IntegrationTest
public class GetUserByIdUseCaseIT {
    @Autowired
    private GetUserByIdUseCase useCase;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenAValidId_whenCallsGetUser_shouldReturnIt() {
        // given
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "password123";
        final var expectedRoles = List.of(Role.USER, Role.ADMIN);
        final var expectedActive = true;

        final var aUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);
        final var expectedId = aUser.getId();

        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        Assertions.assertEquals(1, this.userRepository.count());

        // when
        final var actualUser = useCase.execute(GetUserByIdQuery.with(expectedId.getValue()));

        // then
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(expectedId.getValue(), actualUser.id());
        Assertions.assertEquals(expectedUsername, actualUser.username());
        Assertions.assertEquals(expectedEmail, actualUser.email());
        Assertions.assertEquals(expectedRoles.size(), actualUser.roles().size());
        Assertions.assertTrue(actualUser.roles().containsAll(expectedRoles));
        Assertions.assertEquals(expectedActive, actualUser.active());
        Assertions.assertEquals(aUser.getCreatedAt(), actualUser.createdAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), actualUser.updatedAt());
        Assertions.assertNull(actualUser.deletedAt());
    }

    @Test
    public void givenAInvalidId_whenCallsGetUserAndDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var expectedId = UserID.from("123");
        final var expectedErrorMessage = "User with ID 123 was not found";
        
         // when
        final var actualOutput = Assertions.assertThrows(NotFoundException.class, () -> {
            useCase.execute(GetUserByIdQuery.with(expectedId.getValue()));
        });

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());
    }
}
