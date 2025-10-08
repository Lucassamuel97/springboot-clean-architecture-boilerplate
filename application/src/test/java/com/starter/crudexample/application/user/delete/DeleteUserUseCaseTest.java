package com.starter.crudexample.application.user.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.domain.user.Role;

public class DeleteUserUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteUserUseCase useCase;

    @Mock
    private UserGateway userGateway;

    @Override
    protected List<Object> getMocks() { return List.of(userGateway); }

    @Test
    public void givenAValidId_whenCallsDeleteUser_shouldDeleteIt() {
        // given
        final var user = User.newUser("john","john@example.com","pwd", List.of(Role.USER), true);
        final var expectedId = user.getId();

        doNothing().when(userGateway).deleteById(any());

        // when / then
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(userGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteUser_shouldBeOk() {
        final var expectedId = UserID.from("invalid-id");
        doNothing().when(userGateway).deleteById(any());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(userGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsDeleteUserAndGatewayThrowsException_shouldReceiveException() {
        final var user = User.newUser("john","john@example.com","pwd", List.of(Role.USER), true);
        final var expectedId = user.getId();

        doThrow(new RuntimeException("Gateway error"))
            .when(userGateway).deleteById(any());

        Assertions.assertThrows(RuntimeException.class, () -> useCase.execute(expectedId.getValue()));
        verify(userGateway).deleteById(eq(expectedId));
    }
}
