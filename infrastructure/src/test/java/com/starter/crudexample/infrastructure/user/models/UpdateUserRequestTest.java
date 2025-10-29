package com.starter.crudexample.infrastructure.user.models;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.starter.crudexample.JacksonTest;
import com.starter.crudexample.domain.user.Role;

@JacksonTest
public class UpdateUserRequestTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> json;

    @Test
    public void testUnmarshall() throws Exception {
        final var expectedUsername = "janedoe";
        final var expectedEmail = "jane.doe@example.com";
        final var expectedPassword = "654321";
        final var expectedRoles = List.of(Role.ADMIN);
        final Boolean expectedActive = false;

        final var json = """
            {
              "username": "%s",
              "email": "%s",
              "password": "%s",
              "roles": ["%s"],
              "active": %s
            }
            """.formatted(
                expectedUsername,
                expectedEmail,
                expectedPassword,
                expectedRoles.get(0).name(),
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
