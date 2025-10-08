package com.starter.crudexample.application.user.update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.PasswordHasher;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;

public class UpdateUserUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateUserUseCase useCase;

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @Override
    protected List<Object> getMocks() {
        return List.of(userGateway, passwordHasher);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateUser_shouldReturnItsId() {
        final var existing = User.newUser("john", "john@old.com", "oldpass", List.of(Role.USER), true);
        final var expectedId = existing.getId();

        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final var expectedPasswordRaw = "newpass123";
        final var expectedRoles = List.of(Role.ADMIN);
        final var expectedActive = true;

        final var aCommand = UpdateUserCommand.with(
            expectedId.getValue(),
            expectedUsername,
            expectedEmail,
            expectedPasswordRaw,
            expectedRoles,
            expectedActive
        );

        when(userGateway.findById(eq(expectedId))).thenReturn(Optional.of(existing));
        when(userGateway.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(passwordHasher.hash(expectedPasswordRaw)).thenReturn("HASH-" + expectedPasswordRaw);

        final var output = useCase.execute(aCommand);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(expectedId.getValue(), output.id());

        verify(userGateway).update(argThat(aUser ->
            Objects.equals(expectedId, aUser.getId()) &&
            Objects.equals(expectedUsername, aUser.getUsername()) &&
            Objects.equals(expectedEmail, aUser.getEmail()) &&
            Objects.equals("HASH-" + expectedPasswordRaw, aUser.getPassword()) &&
            Objects.equals(expectedRoles, aUser.getRoles()) &&
            aUser.isActive() == expectedActive
        ));
    }

    @Test
    public void givenANonExistingId_whenCallsUpdateUser_shouldThrowNotFound() {
        final var expectedId = UserID.unique();

        final var aCommand = UpdateUserCommand.with(
            expectedId.getValue(),
            "any",
            "any@example.com",
            null,
            List.of(Role.USER),
            true
        );

        when(userGateway.findById(eq(expectedId))).thenReturn(Optional.empty());

        final var ex = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));
        Assertions.assertTrue(ex.getMessage().contains(expectedId.getValue()));

        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenAnInvalidUsername_whenCallsUpdateUser_shouldThrowNotificationException() {
        final var existing = User.newUser("john", "john@old.com", "oldpass", List.of(Role.USER), true);
        final var expectedId = existing.getId();

        final var invalidUsername = " ";
        final var expectedEmail = "john@example.com";

        final var aCommand = UpdateUserCommand.with(
            expectedId.getValue(),
            invalidUsername,
            expectedEmail,
            null,
            List.of(Role.USER),
            true
        );

        when(userGateway.findById(eq(expectedId))).thenReturn(Optional.of(existing));

        final var ex = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));
        Assertions.assertEquals(1, ex.getErrors().size());
        Assertions.assertEquals("'username' should not be empty", ex.getErrors().get(0).message());

        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenANullPasswordInUpdate_shouldNotRehashAndKeepOldPassword() {
        final var existing = User.newUser("john", "john@old.com", "oldpass", List.of(Role.USER), true);
        final var expectedId = existing.getId();

        final var newUsername = "johnny";
        final var newEmail = "johnny@example.com";

        final var aCommand = UpdateUserCommand.with(
            expectedId.getValue(),
            newUsername,
            newEmail,
            null,
            List.of(Role.USER),
            true
        );

        when(userGateway.findById(eq(expectedId))).thenReturn(Optional.of(existing));
        when(userGateway.update(any())).thenAnswer(inv -> inv.getArgument(0));

        final var output = useCase.execute(aCommand);
        Assertions.assertNotNull(output);

        verify(passwordHasher, times(0)).hash(any());
        verify(userGateway).update(argThat(aUser -> Objects.equals("oldpass", aUser.getPassword())));
    }
}
