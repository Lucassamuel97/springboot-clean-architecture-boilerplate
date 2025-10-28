package com.starter.crudexample.e2e.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.E2ETest;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.infrastructure.user.models.CreateUserRequest;
import com.starter.crudexample.infrastructure.user.models.UpdateUserRequest;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@E2ETest
@Testcontainers
public class UserE2ETest implements MockDsl {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.36")
        .withPassword("123456")
        .withUsername("root")
        .withDatabaseName("testes_e2e");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM users");

        jdbcTemplate.execute(
            "INSERT INTO users (id, username, email, password, active, created_at, updated_at) VALUES " +
            "('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin', 'admin@example.com', " +
            "'$2a$10$zbFqVpnvJmEbLIalYsS2P.oHlL4y6QT5MiM8dS9wupteiefFHkxiK', true, NOW(6), NOW(6))"
        );

        jdbcTemplate.execute(
            "INSERT INTO user_roles (user_id, role) VALUES " +
            "('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'ADMIN')"
        );

        jdbcTemplate.execute(
            "INSERT INTO users (id, username, email, password, active, created_at, updated_at) VALUES " +
            "('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'user', 'user@example.com', " +
            "'$2a$10$08y9cvFLywH25z3sy0yx1O1vh8iClNHHoAcA24C4s98/X9jiY9NU.', true, NOW(6), NOW(6))"
        );

        jdbcTemplate.execute(
            "INSERT INTO user_roles (user_id, role) VALUES " +
            "('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'USER')"
        );
    }

    @Test
    public void asAnAdminIShouldBeAbleToCreateANewUserWithValidValues() throws Exception {
        Assertions.assertEquals(2L, userRepository.count());
        final var token = login("admin", "admin123");

        final var request = new CreateUserRequest(
            "alice",
            "alice@example.com",
            "alice123",
            List.of(Role.USER),
            true
        );

        final var result = createUser(request, token)
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", notNullValue()))
            .andExpect(jsonPath("$.id", notNullValue()))
            .andReturn();

        Assertions.assertEquals(3L, userRepository.count());

        final var createdId = MAPPER.readTree(result.getResponse().getContentAsString()).get("id").asText();
        final var createdUser = userRepository.findById(createdId).orElseThrow();

        Assertions.assertEquals("alice", createdUser.getUsername());
        Assertions.assertEquals("alice@example.com", createdUser.getEmail());
        Assertions.assertEquals(List.of(Role.USER), createdUser.getRoles());
        Assertions.assertTrue(createdUser.isActive());
    }

    @Test
    public void asAnAdminIShouldBeAbleToNavigateAllUsers() throws Exception {
        Assertions.assertEquals(2L, userRepository.count());
        final var token = login("admin", "admin123");

        createUser(new CreateUserRequest("alice", "alice@example.com", "alice123", List.of(Role.USER), true), token);
        createUser(new CreateUserRequest("bruno", "bruno@example.com", "bruno123", List.of(Role.ADMIN), true), token);

        Assertions.assertEquals(4L, userRepository.count());

        listUsers(token, 0, 2, null, null, null)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(2)))
            .andExpect(jsonPath("$.total", equalTo(4)))
            .andExpect(jsonPath("$.items", hasSize(2)))
            .andExpect(jsonPath("$.items[0].username", equalTo("admin")))
            .andExpect(jsonPath("$.items[1].username", equalTo("alice")));

        listUsers(token, 1, 2, null, null, null)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(2)))
            .andExpect(jsonPath("$.total", equalTo(4)))
            .andExpect(jsonPath("$.items", hasSize(2)))
            .andExpect(jsonPath("$.items[0].username", equalTo("bruno")))
            .andExpect(jsonPath("$.items[1].username", equalTo("user")));
    }

    @Test
    public void asAnAdminIShouldBeAbleToSearchUsers() throws Exception {
        Assertions.assertEquals(2L, userRepository.count());
        final var token = login("admin", "admin123");

        createUser(new CreateUserRequest("carol", "carol@example.com", "carol123", List.of(Role.USER), true), token);
        createUser(new CreateUserRequest("daniel", "daniel@example.com", "daniel123", List.of(Role.ADMIN), true), token);

        listUsers(token, 0, 10, "carol", null, null)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(10)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].username", equalTo("carol")));

        listUsers(token, 0, 10, "dan", null, null)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].username", equalTo("daniel")));
    }

    @Test
    public void asAnAdminIShouldBeAbleToGetAUserByItsIdentifier() throws Exception {
        final var token = login("admin", "admin123");

        final var createResult = createUser(
            new CreateUserRequest("carol", "carol@example.com", "carol123", List.of(Role.USER), true),
            token
        ).andReturn();

        final var userId = MAPPER.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        get("/users/" + userId, token)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(userId)))
            .andExpect(jsonPath("$.username", equalTo("carol")))
            .andExpect(jsonPath("$.email", equalTo("carol@example.com")))
            .andExpect(jsonPath("$.roles", hasSize(1)))
            .andExpect(jsonPath("$.roles[0]", equalTo("USER")))
            .andExpect(jsonPath("$.active", equalTo(true)))
            .andExpect(jsonPath("$.created_at", notNullValue()))
            .andExpect(jsonPath("$.updated_at", notNullValue()));
    }

    @Test
    public void asAnAdminIShouldBeAbleToUpdateAUserByItsIdentifier() throws Exception {
        final var token = login("admin", "admin123");

        final var createResult = createUser(
            new CreateUserRequest("eric", "eric@example.com", "eric123", List.of(Role.USER), true),
            token
        ).andReturn();

        final var userId = MAPPER.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        final var updateRequest = new UpdateUserRequest(
            "eric-admin",
            "eric.admin@example.com",
            "adminpass123",
            List.of(Role.ADMIN, Role.USER),
            false
        );

        updateUser(userId, updateRequest, token)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(userId)));

        final var updatedUser = userRepository.findById(userId).orElseThrow();
        Assertions.assertEquals("eric-admin", updatedUser.getUsername());
        Assertions.assertEquals("eric.admin@example.com", updatedUser.getEmail());
        Assertions.assertFalse(updatedUser.isActive());
        Assertions.assertEquals(2, updatedUser.getRoles().size());
        Assertions.assertTrue(updatedUser.getRoles().containsAll(List.of(Role.ADMIN, Role.USER)));
    }

    @Test
    public void asAnAdminIShouldBeAbleToDeleteAUserByItsIdentifier() throws Exception {
        Assertions.assertEquals(2L, userRepository.count());
        final var token = login("admin", "admin123");

        final var createResult = createUser(
            new CreateUserRequest("fiona", "fiona@example.com", "fiona123", List.of(Role.USER), true),
            token
        ).andReturn();

        final var userId = MAPPER.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        Assertions.assertEquals(3L, userRepository.count());

        deleteUser(userId, token)
            .andExpect(status().isNoContent());

        Assertions.assertEquals(2L, userRepository.count());
        Assertions.assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    public void asAUserIShouldNotBeAbleToAccessUserEndpoints() throws Exception {
        final var token = login("user", "user123");

        listUsers(token, 0, 10, null, null, null)
            .andExpect(status().isForbidden());
    }

    @Test
    public void withoutAuthenticationIShouldReceive401() throws Exception {
        mvc().perform(
            MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }
}
