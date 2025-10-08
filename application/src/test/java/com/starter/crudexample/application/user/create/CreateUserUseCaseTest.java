package com.starter.crudexample.application.user.create;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.PasswordHasher;

public class CreateUserUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateUserUseCase useCase;

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @Override
    protected List<Object> getMocks() {
        return List.of(userGateway, passwordHasher);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateUser_shouldReturnIt() {
        // given
        final var expectedUsername = "john";
        final var expectedEmail = "john@example.com";
        final var expectedPassword = "secret123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedIsActive = true;

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);

    when(userGateway.create(any())).thenAnswer(returnsFirstArg());
    when(passwordHasher.hash(expectedPassword)).thenReturn("HASHED-" + expectedPassword);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(userGateway).create(argThat(aUser ->
            Objects.nonNull(aUser.getId())
                && Objects.equals(expectedUsername, aUser.getUsername())
                && Objects.equals(expectedEmail, aUser.getEmail())
                && Objects.equals("HASHED-" + expectedPassword, aUser.getPassword())
                && Objects.equals(expectedRoles, aUser.getRoles())
                && Objects.equals(expectedIsActive, aUser.isActive())
        ));
    }

    @Test
    public void givenAnInvalidNullUsername_whenCallsCreateUser_shouldThrowsNotificationException() {
        // given
        final String expectedUsername = null;
        final String expectedEmail = "john@example.com";
        final String expectedPassword = "secret123";
        final var expectedRoles = List.of(Role.USER);
        final boolean expectedIsActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'username' should not be null";

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(userGateway, times(0)).create(any());
    }

    @Test
    public void givenAnInvalidEmptyUsername_whenCallsCreateUser_shouldThrowsNotificationException() {
        // given
        final var expectedUsername = " ";
        final var expectedEmail = "john@example.com";
        final var expectedPassword = "secret123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedIsActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'username' should not be empty";

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(userGateway, times(0)).create(any());
    }

    @Test
    public void givenAnInvalidUsernameAndEmail_whenCallsCreateUser_shouldThrowsNotificationException() {
        // given
        final var expectedUsername = "";
        final var expectedEmail = "johnexample.com";
        final var expectedPassword = "secret123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedIsActive = true;

        final var expectedErrorCount = 2;
        final var expectedErrorMessage1 = "'username' should not be empty";
        final var expectedErrorMessage2 = "'email' should be a valid email address";

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage1, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessage2, actualException.getErrors().get(1).message());

        verify(userGateway, times(0)).create(any());
    }

    @Test
    public void givenAnInvalidRoles_whenCallsCreateUser_shouldThrowsNotificationException() {
        // given
        final var expectedUsername = "john";
        final var expectedEmail = "john@example.com";
        final var expectedPassword = "secret123";
        final List<Role> expectedRoles = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'roles' should not be null";

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));  

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(userGateway, times(0)).create(any());
    }

    @Test
    public void givenAnInvalidPassword_whenCallsCreateUser_shouldThrowsNotificationException() {
        // given
        final var expectedUsername = "john";
        final var expectedEmail = "john@example.com";
        final var expectedPassword = "123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;

        final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedIsActive);
        final var expectedErrorMessage = "'password' must be between 6 and 255 characters";

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));
        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(userGateway, times(0)).create(any());
    }
}
