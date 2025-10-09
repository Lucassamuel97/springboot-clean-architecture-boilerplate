package com.starter.crudexample.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.validation.Error;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;

@ControllerTest(controllers = UserAPI.class)
public class UserAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private DefaultCreateUserUseCase createUserUseCase;

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
}
