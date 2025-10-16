package com.starter.crudexample.application.user.update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.PasswordHasher;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;


@IntegrationTest
public class UpdateUserUseCaseIT {
    @Autowired
    private UpdateUserUseCase useCase;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private UserGateway userGateway;

    @MockitoSpyBean
    private PasswordHasher passwordHasher;

    
    @Test
    public void givenAValidCommand_whenCallsUpdateUser_shouldReturnItsIdentifier() {
        // given
        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.ADMIN, Role.USER);
        final var expectedActive = true;

        final var aUser = User.newUser("john", "john@old.com", "oldpass", List.of(Role.USER), true);
        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));
        
        final var expectedId = aUser.getId();

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualPersistedUser = this.userRepository.findById(expectedId.getValue()).get();
        
        Assertions.assertEquals(expectedId.getValue(), actualPersistedUser.getId());
        Assertions.assertEquals(expectedUsername, actualPersistedUser.getUsername());
        Assertions.assertEquals(expectedEmail, actualPersistedUser.getEmail());
        Assertions.assertEquals(expectedRoles.size(), actualPersistedUser.getRoles().size());
        Assertions.assertEquals(expectedActive, actualPersistedUser.isActive());
        Assertions.assertNotNull(actualPersistedUser.getCreatedAt());
        Assertions.assertTrue(actualPersistedUser.getUpdatedAt().isAfter(actualPersistedUser.getCreatedAt()));

        verify(userGateway).findById(any());
        verify(userGateway).update(any());
        verify(passwordHasher).hash(expectedPassword);
    }

    @Test
    public void givenAnInvalidUsername_whenCallsUpdateUser_shouldThrowsNotificationException() {
        // given
        final var aUser = User.newUser(
            "john",
            "john@example.com",
            "password123",
            List.of(Role.USER),
            true);
        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        final var expectedId = aUser.getId();
        final String invalidUsername = " ";
        final var expectedEmail = "johnny@example.com";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'username' should not be empty";

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                invalidUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    
        verify(userGateway).findById(any());
        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenAnInvalidEmail_whenCallsUpdateUser_shouldThrowsNotificationException() {
        // given
        final var aUser = User.newUser(
            "john",
            "john@example.com",
            "password123",
            List.of(Role.USER),
            true);
        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        final var expectedId = aUser.getId();
        final var expectedUsername = "johnny";
        final String invalidEmail = "invalid-email";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'email' should be a valid email address";

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                expectedUsername,
                invalidEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    
        verify(userGateway).findById(any());
        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenANullPasswordInUpdate_shouldNotRehashAndKeepOldPassword() {
        // given
        final var aUser = User.newUser(
            "john",
            "john@example.com",
            "oldpassword",
            List.of(Role.USER),
            true);
        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        final var expectedId = aUser.getId();
        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final String nullPassword = null;
        final var expectedRoles = List.of(Role.ADMIN);
        final var expectedActive = true;

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                expectedUsername,
                expectedEmail,
                nullPassword,
                expectedRoles,
                expectedActive
        );

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualPersistedUser = this.userRepository.findById(expectedId.getValue()).get();
        
        Assertions.assertEquals(expectedUsername, actualPersistedUser.getUsername());
        Assertions.assertEquals(expectedEmail, actualPersistedUser.getEmail());
        Assertions.assertEquals("oldpassword", actualPersistedUser.getPassword());
        Assertions.assertEquals(expectedRoles.size(), actualPersistedUser.getRoles().size());

        verify(userGateway).findById(any());
        verify(userGateway).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateUser_shouldThrowsNotFoundException() {
        // given
        final var expectedId = UserID.from("123");
        final var expectedUsername = "john";
        final var expectedEmail = "john@example.com";
        final var expectedPassword = "password123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var expectedErrorMessage = "User with ID 123 was not found";

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(userGateway).findById(any());
        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }

    @Test
    public void givenMultipleInvalidFields_whenCallsUpdateUser_shouldThrowsNotificationExceptionWithAllErrors() {
        // given
        final var aUser = User.newUser(
            "john",
            "john@example.com",
            "password123",
            List.of(Role.USER),
            true);
        this.userRepository.saveAndFlush(UserJpaEntity.from(aUser));

        final var expectedId = aUser.getId();
        final String invalidUsername = " ";
        final String invalidEmail = "invalid-email";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var expectedErrorCount = 2;

        final var aCommand = UpdateUserCommand.with(
                expectedId.getValue(),
                invalidUsername,
                invalidEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(userGateway).findById(any());
        verify(userGateway, times(0)).update(any());
        verify(passwordHasher, times(0)).hash(any());
    }
}
