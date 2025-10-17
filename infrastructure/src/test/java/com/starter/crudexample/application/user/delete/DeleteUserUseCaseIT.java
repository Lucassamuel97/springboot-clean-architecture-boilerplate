package com.starter.crudexample.application.user.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@IntegrationTest
public class DeleteUserUseCaseIT {
    @Autowired
    private DeleteUserUseCase useCase;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private UserGateway userGateway;

    @Test
    public void givenAValidId_whenCallsDeleteUser_shouldDeleteIt() {
        // given
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "password123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var aUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);
        final var expectedId = aUser.getId();

        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        Assertions.assertEquals(1, this.userRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // then
        Assertions.assertEquals(0, this.userRepository.count());
        Assertions.assertTrue(this.userRepository.findById(expectedId.getValue()).isEmpty());
    }

    @Test
    public void givenAValidId_whenCallsDeleteUserAndGatewayThrowsException_shouldReceiveException() {
        // given
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "password123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var aUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);
        final var expectedId = aUser.getId();

        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        Assertions.assertEquals(1, this.userRepository.count());

        doThrow(new IllegalStateException("Gateway error"))
                .when(userGateway).deleteById(any());

        // when
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        verify(userGateway).deleteById(eq(expectedId));
        Assertions.assertEquals(1, this.userRepository.count());
    }
}
