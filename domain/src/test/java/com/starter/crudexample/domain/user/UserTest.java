package com.starter.crudexample.domain.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import com.starter.crudexample.domain.UnitTest;
import com.starter.crudexample.domain.exceptions.DomainException;
import com.starter.crudexample.domain.validation.handler.ThrowsValidationHandler;

class UserTest  extends UnitTest {

    @Test
      public void givenAValidParams_whenCallsNewMember_thenInstantiateACastMember() {
        List<Role> roles = List.of(Role.ADMIN);
        User user = User.newUser("testuser", "test@example.com", "password", roles, true);

        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(Role.ADMIN));
    }


    @Test
    public void givenAnInvalidNullUsername_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'username' should not be null";
        final var expectedEmail = "test@example.com";
        final var expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyUsername_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'username' should not be empty";
        final var expectedEmail = "test@example.com";
        final var expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullEmail_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'email' should not be null";
        final String expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyEmail_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'email' should not be empty";
        final String expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmailFormat_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "invalid-email";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'email' should be a valid email address";
        final String expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidShortEmail_whenCallNewUserAndValidate_thenShouldReceiveError(){
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "@b.s";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'email' must be between 5 and 255 characters";
        final String expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullPassword_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'password' should not be null";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyPassword_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'password' should not be empty";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidShortPassword_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "short";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'password' must be between 6 and 255 characters";
        List<Role> roles = List.of(Role.ADMIN);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullRoles_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "password";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'roles' should not be null";
        List<Role> roles = null;

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyRoles_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "password";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'roles' should not be empty";
        List<Role> roles = List.of();

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullRoleInList_whenCallNewUserAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "password";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'role' should valid and not null";
        List<Role> roles = java.util.Arrays.asList(Role.ADMIN, null);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualUser.validate(new ThrowsValidationHandler()));

        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenValidRoles_whenCallNewUserAndValidate_thenShouldNotReceiveError() {
        //Given
        final String expectedUsername = "testuser";
        final String expectedEmail = "test@example.com";
        final String expectedPassword = "password";
        List<Role> roles = List.of(Role.ADMIN, Role.USER);

        //When
        final var actualUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, roles, true);

        //Then - Should not throw any exception
        Assertions.assertDoesNotThrow(() -> actualUser.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(2, actualUser.getRoles().size());
        Assertions.assertTrue(actualUser.getRoles().contains(Role.ADMIN));
        Assertions.assertTrue(actualUser.getRoles().contains(Role.USER));
    }
}