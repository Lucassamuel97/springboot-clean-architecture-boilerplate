package com.starter.crudexample.application.user.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@IntegrationTest
public class CreateUserUseCaseIT {

	@Autowired
	private CreateUserUseCase useCase;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void givenAValidCommand_whenCallsCreateUser_shouldReturnIt() {
		// given
		final var expectedUsername = "johndoe";
		final var expectedEmail = "john.doe@example.com";
		final var expectedPassword = "123456"; // raw
		final var expectedRoles = List.of(Role.USER);
		final var expectedActive = true;

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualOutput = useCase.execute(aCommand);

		// then
		assertNotNull(actualOutput);
		assertNotNull(actualOutput.id());

		final var actualUser = userRepository.findById(actualOutput.id()).get();

		assertEquals(expectedUsername, actualUser.getUsername());
		assertEquals(expectedEmail, actualUser.getEmail());
		// senha deve estar hasheada, portanto diferente da raw
		final var persistedPassword = actualUser.getPassword();
		assertNotNull(persistedPassword);
		Assertions.assertNotEquals(expectedPassword, persistedPassword);
		// normalmente BCrypt começa com $2
		Assertions.assertTrue(persistedPassword.startsWith("$2"));

		assertEquals(expectedRoles, actualUser.getRoles());
		assertEquals(expectedActive, actualUser.isActive());
		assertNotNull(actualUser.getCreatedAt());
		assertNotNull(actualUser.getUpdatedAt());
		assertNull(actualUser.getDeletedAt());
	}

	@Test
	public void givenAnInvalidUsername_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given
		final String expectedUsername = null;
		final var expectedEmail = "john.doe@example.com";
		final var expectedPassword = "123456";
		final var expectedRoles = List.of(Role.USER);
		final var expectedActive = true;
		final var expectedErrorCount = 1;
		final var expectedErrorMessage = "'username' should not be null";

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(aCommand));

		// then
		assertNotNull(actualException);
		assertEquals(expectedErrorCount, actualException.getErrors().size());
		assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

		assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnInvalidEmail_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given
		final var expectedUsername = "johndoe";
		final String expectedEmail = null;
		final var expectedPassword = "123456";
		final var expectedRoles = List.of(Role.USER);
		final var expectedActive = true;
		final var expectedErrorCount = 1;
		final var expectedErrorMessage = "'email' should not be null";

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(aCommand));

		// then
		assertNotNull(actualException);
		assertEquals(expectedErrorCount, actualException.getErrors().size());
		assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

		assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnInvalidPassword_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given
		final var expectedUsername = "johndoe";
		final var expectedEmail = "john.doe@example.com";
		final String expectedPassword = null;
		final var expectedRoles = List.of(Role.USER);
		final var expectedActive = true;
		final var expectedErrorCount = 1;
		final var expectedErrorMessage = "'password' should not be null";

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(aCommand));

		// then
		assertNotNull(actualException);
		assertEquals(expectedErrorCount, actualException.getErrors().size());
		assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

		assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnInvalidRoles_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given
		final var expectedUsername = "johndoe";
		final var expectedEmail = "john.doe@example.com";
		final var expectedPassword = "123456";
		final List<Role> expectedRoles = null;
		final var expectedActive = true;
		final var expectedErrorCount = 1;
		final var expectedErrorMessage = "'roles' should not be null";

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(aCommand));

		// then
		assertNotNull(actualException);
		assertEquals(expectedErrorCount, actualException.getErrors().size());
		assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

		assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnInvalidCommand_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given
		final String expectedUsername = null;
		final String expectedEmail = null;
		final String expectedPassword = null;
		final List<Role> expectedRoles = null;
		final var expectedActive = true;
		final var expectedErrorCount = 4;
		final var expectedErrorMessage1 = "'username' should not be null";
		final var expectedErrorMessage2 = "'email' should not be null";
		final var expectedErrorMessage3 = "'password' should not be null";
		final var expectedErrorMessage4 = "'roles' should not be null";

		final var aCommand = CreateUserCommand.with(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(aCommand));

		// then
		assertNotNull(actualException);
		assertEquals(expectedErrorCount, actualException.getErrors().size());
		assertEquals(expectedErrorMessage1, actualException.getErrors().get(0).message());
		assertEquals(expectedErrorMessage2, actualException.getErrors().get(1).message());
		assertEquals(expectedErrorMessage3, actualException.getErrors().get(2).message());
		assertEquals(expectedErrorMessage4, actualException.getErrors().get(3).message());

		assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnExistingEmail_whenCallsCreateUser_shouldThrowsNotificationException() {
		// given: primeiro cria com sucesso
		final var username1 = "johndoe";
		final var email = "john.doe@example.com";
		final var password1 = "123456";
		final var roles = List.of(Role.USER);
		final var active = true;

		final var cmd1 = CreateUserCommand.with(username1, email, password1, roles, active);
		final var out1 = useCase.execute(cmd1);
		assertNotNull(out1);

		// tenta criar novamente com o mesmo e-mail
		final var username2 = "johnny";
		final var password2 = "abcdef";
		final var cmd2 = CreateUserCommand.with(username2, email, password2, roles, active);

		// when
		final var actualException = Assertions.assertThrows(
				NotificationException.class,
				() -> useCase.execute(cmd2));

		// then
		assertNotNull(actualException);
		assertEquals(1, actualException.getErrors().size());
		assertEquals("'email' is already in use", actualException.getErrors().get(0).message());

		// continua apenas 1 usuário persistido
		assertEquals(1, userRepository.count());
	}
}
