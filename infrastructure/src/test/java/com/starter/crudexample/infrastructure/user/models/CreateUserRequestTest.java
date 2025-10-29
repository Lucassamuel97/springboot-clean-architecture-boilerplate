package com.starter.crudexample.infrastructure.user.models;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.starter.crudexample.JacksonTest;
import com.starter.crudexample.domain.user.Role;

@JacksonTest
public class CreateUserRequestTest {

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedPassword = "123456";
        final var expectedRoles = List.of(Role.USER, Role.ADMIN);
        final var expectedActive = true;

        final var json = """
            {
              "username": "%s",
              "email": "%s",
              "password": "%s",
              "roles": ["%s", "%s"],
              "active": %s
            }
            """.formatted(
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles.get(0).name(),
                expectedRoles.get(1).name(),
                expectedActive
            );

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
            .hasFieldOrPropertyWithValue("username", expectedUsername)
            .hasFieldOrPropertyWithValue("email", expectedEmail)
            .hasFieldOrPropertyWithValue("password", expectedPassword)
            .hasFieldOrPropertyWithValue("roles", expectedRoles)
            .hasFieldOrPropertyWithValue("active", expectedActive);
    }
}
