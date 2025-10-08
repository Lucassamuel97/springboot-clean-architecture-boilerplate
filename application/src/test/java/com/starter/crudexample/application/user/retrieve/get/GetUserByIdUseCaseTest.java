package com.starter.crudexample.application.user.retrieve.get;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;

public class GetUserByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetUserByIdUseCase useCase;

    @Mock
    private UserGateway userGateway;

    @Override
    protected java.util.List<Object> getMocks() {
        return java.util.List.of(userGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetUserById_shouldReturnIt() {
        final var user = User.newUser("john","john@example.com","pass", List.of(Role.USER), true);
        final var expectedId = user.getId();

        when(userGateway.findById(eq(expectedId))).thenReturn(Optional.of(user));

        final var output = useCase.execute(GetUserByIdQuery.with(expectedId.getValue()));

        Assertions.assertNotNull(output);
        Assertions.assertEquals(expectedId.getValue(), output.id());
        Assertions.assertEquals("john", output.username());
        Assertions.assertEquals("john@example.com", output.email());
        Assertions.assertTrue(output.active());
        Assertions.assertNotNull(output.createdAt());
    }

    @Test
    public void givenAnInvalidId_whenCallsGetUserById_shouldThrowNotFound() {
        final var id = UserID.unique();
        when(userGateway.findById(eq(id))).thenReturn(Optional.empty());

        final var ex = Assertions.assertThrows(NotFoundException.class, () ->
            useCase.execute(GetUserByIdQuery.with(id.getValue()))
        );

        Assertions.assertTrue(ex.getMessage().contains(id.getValue()));
    }
}
