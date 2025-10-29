package com.starter.crudexample.infrastructure.user.models;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.starter.crudexample.JacksonTest;
import com.starter.crudexample.domain.user.Role;

@JacksonTest
public class UserListResponseTest {

    @Autowired
    private JacksonTester<UserListResponse> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedId = "123e4567-e89b-12d3-a456-426614174000";
        final var expectedUsername = "johndoe";
        final var expectedEmail = "john.doe@example.com";
        final var expectedRoles = List.of(Role.ADMIN, Role.USER);
        final var expectedActive = true;
        final var expectedCreatedAt = "2024-01-01T10:15:30Z";

        final var response = new UserListResponse(
            expectedId,
            expectedUsername,
            expectedEmail,
            expectedRoles,
            expectedActive,
            expectedCreatedAt
        );

        final var actualJson = this.json.write(response);

        Assertions.assertThat(actualJson)
            .hasJsonPathValue("$.id", expectedId)
            .hasJsonPathValue("$.username", expectedUsername)
            .hasJsonPathValue("$.email", expectedEmail)
            .hasJsonPathValue("$.active", expectedActive)
            .hasJsonPathValue("$.created_at", expectedCreatedAt)
            .hasJsonPathValue("$.roles[0]", expectedRoles.get(0).name())
            .hasJsonPathValue("$.roles[1]", expectedRoles.get(1).name());
    }
}
