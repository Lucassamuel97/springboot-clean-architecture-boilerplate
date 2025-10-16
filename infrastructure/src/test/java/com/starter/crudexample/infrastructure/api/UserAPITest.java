package com.starter.crudexample.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.ControllerTest;
import com.starter.crudexample.application.user.create.CreateUserOutput;
import com.starter.crudexample.application.user.create.DefaultCreateUserUseCase;
import com.starter.crudexample.application.user.retrieve.get.DefaultGetUserByIdUseCase;
import com.starter.crudexample.application.user.retrieve.get.GetUserByIdOutput;
import com.starter.crudexample.application.user.update.DefaultUpdateUserUseCase;
import com.starter.crudexample.application.user.update.UpdateUserOutput;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.domain.validation.Error;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;
import com.starter.crudexample.infrastructure.user.models.UpdateUserRequest;

@ControllerTest(controllers = UserAPI.class)
public class UserAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private DefaultCreateUserUseCase createUserUseCase;

    @MockitoBean
    private DefaultGetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private DefaultUpdateUserUseCase updateUserUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateUser_thenShouldReturnUserId() throws Exception {
        // Given
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "123456";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedId = "abc-123";

        final var aCommand = new CreateUserRequest(expectedUsername, expectedEmail, expectedPassword, expectedRoles,
                expectedActive);

        when(createUserUseCase.execute(any()))
                .thenReturn(new CreateUserOutput(expectedId));

        // When
        final var response = this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.equalTo("/users/" + expectedId)))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId)));

        verify(createUserUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())));
    }

    @Test
    public void givenAnInvalidUsername_whenCallsCreateUser_thenShouldReturnNotification() throws Exception {
        // Given
        final String expectedUsername = null;
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "123456";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedMessage = "'username' should not be null";

        final var aCommand = new CreateUserRequest(expectedUsername, expectedEmail, expectedPassword, expectedRoles,
                expectedActive);

        when(createUserUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedMessage)));

        // When
        final var response = this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

        verify(createUserUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())));
    }

    @Test
    public void givenAnExistingEmail_whenCallsCreateUser_thenShouldReturnNotification() throws Exception {
        // Given
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "123456";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedMessage = "'email' is already in use";

        final var aCommand = new CreateUserRequest(expectedUsername, expectedEmail, expectedPassword, expectedRoles,
                expectedActive);

        when(createUserUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedMessage)));

        // When
        final var response = this.mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

        verify(createUserUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())));
    }

    @Test
    public void givenAValidId_whenCallsGetUserById_thenShouldReturnUser() throws Exception {
        // Given
        final var expectedId = "abc-123";
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var output = new GetUserByIdOutput(
                expectedId,
                expectedUsername,
                expectedEmail,
                expectedRoles,
                expectedActive,
                expectedCreatedAt,
                expectedUpdatedAt,
                null
        );

        when(getUserByIdUseCase.execute(any()))
                .thenReturn(output);

        // When
        final var response = this.mvc.perform(get("/users/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.username", equalTo(expectedUsername)))
                .andExpect(jsonPath("$.email", equalTo(expectedEmail)))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.active", equalTo(expectedActive)))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        verify(getUserByIdUseCase, times(1)).execute(argThat(query ->
                Objects.equals(expectedId, query.id())
        ));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetUserById_thenShouldReturnNotFound() throws Exception {
        // Given
        final var expectedId = "invalid-id";
        final var expectedErrorMessage = "User with ID invalid-id was not found";

        when(getUserByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(User.class, UserID.from(expectedId)));

        // When
        final var response = this.mvc.perform(get("/users/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isNotFound());

        verify(getUserByIdUseCase, times(1)).execute(argThat(query ->
                Objects.equals(expectedId, query.id())
        ));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateUser_thenShouldReturnItsIdentifier() throws Exception {
        // Given
        final var expectedId = "abc-123";
        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.ADMIN, Role.USER);
        final var expectedActive = true;

        final var aCommand = new UpdateUserRequest(
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        when(updateUserUseCase.execute(any()))
                .thenReturn(UpdateUserOutput.from(UserID.from(expectedId)));

        // When
        final var response = this.mvc.perform(put("/users/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateUserUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id()) &&
                Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())
        ));
    }

    @Test
    public void givenAnInvalidUsername_whenCallsUpdateUser_thenShouldReturnNotification() throws Exception {
        // Given
        final var expectedId = "abc-123";
        final String invalidUsername = " ";
        final var expectedEmail = "johnny@example.com";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedMessage = "'username' should not be empty";

        final var aCommand = new UpdateUserRequest(
                invalidUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        when(updateUserUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedMessage)));

        // When
        final var response = this.mvc.perform(put("/users/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

        verify(updateUserUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id()) &&
                Objects.equals(invalidUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())
        ));
    }

    @Test
    public void givenAnInvalidEmail_whenCallsUpdateUser_thenShouldReturnNotification() throws Exception {
        // Given
        final var expectedId = "abc-123";
        final var expectedUsername = "johnny";
        final String invalidEmail = "invalid-email";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;
        final var expectedMessage = "'email' should be a valid email";

        final var aCommand = new UpdateUserRequest(
                expectedUsername,
                invalidEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        when(updateUserUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedMessage)));

        // When
        final var response = this.mvc.perform(put("/users/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

        verify(updateUserUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id()) &&
                Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(invalidEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())
        ));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateUser_thenShouldReturnNotFound() throws Exception {
        // Given
        final var expectedId = "invalid-id";
        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final var expectedPassword = "newpassword123";
        final var expectedRoles = List.of(Role.USER);
        final var expectedActive = true;

        final var aCommand = new UpdateUserRequest(
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles,
                expectedActive
        );

        when(updateUserUseCase.execute(any()))
                .thenThrow(NotFoundException.with(User.class, UserID.from(expectedId)));

        // When
        final var response = this.mvc.perform(put("/users/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isNotFound());

        verify(updateUserUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id()) &&
                Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(expectedPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())
        ));
    }

    @Test
    public void givenANullPassword_whenCallsUpdateUser_thenShouldNotUpdatePassword() throws Exception {
        // Given
        final var expectedId = "abc-123";
        final var expectedUsername = "johnny";
        final var expectedEmail = "johnny@example.com";
        final String nullPassword = null;
        final var expectedRoles = List.of(Role.ADMIN);
        final var expectedActive = true;

        final var aCommand = new UpdateUserRequest(
                expectedUsername,
                expectedEmail,
                nullPassword,
                expectedRoles,
                expectedActive
        );

        when(updateUserUseCase.execute(any()))
                .thenReturn(UpdateUserOutput.from(UserID.from(expectedId)));

        // When
        final var response = this.mvc.perform(put("/users/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateUserUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id()) &&
                Objects.equals(expectedUsername, cmd.username()) &&
                Objects.equals(expectedEmail, cmd.email()) &&
                Objects.equals(nullPassword, cmd.password()) &&
                Objects.equals(expectedRoles, cmd.roles()) &&
                Objects.equals(expectedActive, cmd.active())
        ));
    }
}
