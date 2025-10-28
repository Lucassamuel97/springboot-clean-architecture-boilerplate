package com.starter.crudexample.e2e.user;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;
import com.starter.crudexample.infrastructure.user.models.UpdateUserRequest;

public interface MockDsl {

    MockMvc mvc();

    default String login(String username, String password) throws Exception {
        final var loginRequest = String.format(
            "{\"username\":\"%s\",\"password\":\"%s\"}",
            username, password
        );

        final var result = mvc().perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        ).andReturn();

        final var statusCode = result.getResponse().getStatus();
        final var response = result.getResponse().getContentAsString();

        if (statusCode != 200) {
            throw new RuntimeException(
                String.format("Login failed with status %d. Response: %s", statusCode, response)
            );
        }

        final var mapper = new ObjectMapper();
        final var jsonNode = mapper.readTree(response);
        final var tokenNode = jsonNode.get("access_token");

        if (tokenNode == null) {
            throw new RuntimeException(
                String.format("Token not found in response. Response: %s", response)
            );
        }

        return tokenNode.asText();
    }

    default ResultActions get(String url, String token) throws Exception {
        return mvc().perform(
            MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    default ResultActions createUser(CreateUserRequest request, String token) throws Exception {
        final var mapper = new ObjectMapper();
        return mvc().perform(
            MockMvcRequestBuilders.post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        );
    }

    default ResultActions updateUser(String id, UpdateUserRequest request, String token) throws Exception {
        final var mapper = new ObjectMapper();
        return mvc().perform(
            MockMvcRequestBuilders.put("/users/{id}", id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        );
    }

    default ResultActions deleteUser(String id, String token) throws Exception {
        return mvc().perform(
            MockMvcRequestBuilders.delete("/users/{id}", id)
                .header("Authorization", "Bearer " + token)
        );
    }

    default ResultActions listUsers(String token, Integer page, Integer perPage, String search, String sort, String direction) throws Exception {
        final var builder = MockMvcRequestBuilders.get("/users")
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON);

        if (page != null) {
            builder.param("page", page.toString());
        }
        if (perPage != null) {
            builder.param("perPage", perPage.toString());
        }
        if (search != null && !search.isBlank()) {
            builder.param("search", search);
        }
        if (sort != null && !sort.isBlank()) {
            builder.param("sort", sort);
        }
        if (direction != null && !direction.isBlank()) {
            builder.param("dir", direction);
        }

        return mvc().perform(builder);
    }
}
