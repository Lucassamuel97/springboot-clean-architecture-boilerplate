package com.starter.crudexample.infrastructure.api.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.infrastructure.api.models.LoginRequest;
import com.starter.crudexample.infrastructure.configuration.WebServerConfig;
import com.starter.crudexample.infrastructure.security.BCryptPasswordHasher;

@SpringBootTest(classes = WebServerConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test-auth")
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserGateway userGateway;

    @Autowired
    private BCryptPasswordHasher passwordHasher;

    @Test
    public void givenValidCredentials_whenLogin_shouldReturnTokenAndUserInfo() throws Exception {
        // Given
        final var expectedUsername = "testuser";
        final var expectedPassword = "password123";
        final var expectedEmail = "test@example.com";
        final var expectedRoles = List.of(Role.USER);

        final var hashedPassword = passwordHasher.hash(expectedPassword);

        final var user = User.newUser(
            expectedUsername,
            expectedEmail,
            hashedPassword,
            expectedRoles,
            true
        );
        userGateway.create(user);

        final var request = new LoginRequest(expectedUsername, expectedPassword);

        // When & Then
        this.mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.mapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token", notNullValue()))
            .andExpect(jsonPath("$.token_type", equalTo("Bearer")))
            .andExpect(jsonPath("$.username", equalTo(expectedUsername)))
            .andExpect(jsonPath("$.email", equalTo(expectedEmail)))
            .andExpect(jsonPath("$.roles[0]", equalTo("ROLE_USER")));
    }

    @Test
    public void givenInvalidPassword_whenLogin_shouldReturn401() throws Exception {
        // Given
        final var expectedUsername = "testuser2";
        final var correctPassword = "correct123";
        final var wrongPassword = "wrong123";
        final var expectedEmail = "test2@example.com";

        final var hashedPassword = passwordHasher.hash(correctPassword);

        final var user = User.newUser(
            expectedUsername,
            expectedEmail,
            hashedPassword,
            List.of(Role.USER),
            true
        );
        userGateway.create(user);

        final var request = new LoginRequest(expectedUsername, wrongPassword);

        // When & Then
        this.mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.mapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenNonExistentUser_whenLogin_shouldReturn401() throws Exception {
        // Given
        final var request = new LoginRequest("nonexistent", "password");

        // When & Then
        this.mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.mapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenInactiveUser_whenLogin_shouldReturn401() throws Exception {
        // Given
        final var expectedUsername = "inactiveuser";
        final var expectedPassword = "password123";
        final var expectedEmail = "inactive@example.com";

        final var hashedPassword = passwordHasher.hash(expectedPassword);

        final var user = User.newUser(
            expectedUsername,
            expectedEmail,
            hashedPassword,
            List.of(Role.USER),
            false // Usuário inativo
        );
        userGateway.create(user);

        final var request = new LoginRequest(expectedUsername, expectedPassword);

        // When & Then
        this.mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.mapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
    }
}
