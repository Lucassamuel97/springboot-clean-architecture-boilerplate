package com.starter.crudexample.e2e.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.starter.crudexample.E2ETest;
import com.starter.crudexample.infrastructure.item.models.CreateItemRequest;
import com.starter.crudexample.infrastructure.item.models.UpdateItemRequest;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@E2ETest
@Testcontainers
public class ItemE2ETest implements MockDsl {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
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
        // Garante que os usuários de teste existem
        try {
            jdbcTemplate.execute("DELETE FROM user_roles");
            jdbcTemplate.execute("DELETE FROM users");
            
            // Inserir usuário ADMIN (senha: admin123)
            jdbcTemplate.execute(
                "INSERT INTO users (id, username, email, password, active, created_at, updated_at) VALUES " +
                "('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'admin', 'admin@example.com', " +
                "'$2a$10$zbFqVpnvJmEbLIalYsS2P.oHlL4y6QT5MiM8dS9wupteiefFHkxiK', true, NOW(6), NOW(6))"
            );
            
            jdbcTemplate.execute(
                "INSERT INTO user_roles (user_id, role) VALUES " +
                "('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'ADMIN')"
            );
            
            // Inserir usuário USER (senha: user123)
            jdbcTemplate.execute(
                "INSERT INTO users (id, username, email, password, active, created_at, updated_at) VALUES " +
                "('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'user', 'user@example.com', " +
                "'$2a$10$08y9cvFLywH25z3sy0yx1O1vh8iClNHHoAcA24C4s98/X9jiY9NU.', true, NOW(6), NOW(6))"
            );
            
            jdbcTemplate.execute(
                "INSERT INTO user_roles (user_id, role) VALUES " +
                "('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'USER')"
            );
        } catch (Exception e) {
            // Usuários já existem
        }
    }

    @Test
    public void asAnAdminIShouldBeAbleToCreateANewItemWithValidValues() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        final var expectedName = "Laptop Dell";
        final var expectedDescription = "Laptop Dell Inspiron 15";
        final var expectedPrice = 2500.00;

        final var request = new CreateItemRequest(expectedName, expectedDescription, expectedPrice);

        // When
        final var response = createItem(request, token);

        // Then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()));

        Assertions.assertEquals(1, itemRepository.count());

        final var item = itemRepository.findAll().iterator().next();
        Assertions.assertEquals(expectedName, item.getName());
        Assertions.assertEquals(expectedDescription, item.getDescription());
        Assertions.assertEquals(expectedPrice, item.getPrice());
    }

    @Test
    public void asAnAdminIShouldBeAbleToNavigateToAllItems() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        createItem(new CreateItemRequest("Item 1", "Description 1", 10.0), token);
        createItem(new CreateItemRequest("Item 2", "Description 2", 20.0), token);
        createItem(new CreateItemRequest("Item 3", "Description 3", 30.0), token);

        Assertions.assertEquals(3, itemRepository.count());

        // When & Then - Page 0
        listItems(token, 0, 1, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Item 1")));

        // When & Then - Page 1
        listItems(token, 1, 1, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Item 2")));

        // When & Then - Page 2
        listItems(token, 2, 1, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Item 3")));
    }

    @Test
    public void asAnAdminIShouldBeAbleToSearchBetweenAllItems() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        createItem(new CreateItemRequest("Laptop Dell", "Dell Inspiron 15", 2500.0), token);
        createItem(new CreateItemRequest("Mouse Logitech", "Mouse sem fio", 50.0), token);
        createItem(new CreateItemRequest("Teclado Mecânico", "Teclado RGB", 300.0), token);

        Assertions.assertEquals(3, itemRepository.count());

        // When & Then - Search "Dell"
        listItems(token, 0, 10, "Dell")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Laptop Dell")));

        // When & Then - Search "Mouse"
        listItems(token, 0, 10, "Mouse")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Mouse Logitech")));
    }

    @Test
    public void asAnAdminIShouldBeAbleToGetAItemByItsIdentifier() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        final var expectedName = "Monitor LG";
        final var expectedDescription = "Monitor 24 polegadas";
        final var expectedPrice = 800.0;

        final var createResponse = createItem(
            new CreateItemRequest(expectedName, expectedDescription, expectedPrice),
            token
        ).andReturn();

        final var itemId = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResponse.getResponse().getContentAsString())
            .get("id").asText();

        // When
        final var response = get("/items/" + itemId, token);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(itemId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.price", equalTo(expectedPrice)));
    }

    @Test
    public void asAnAdminIShouldBeAbleToUpdateAItemByItsIdentifier() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        final var createResponse = createItem(
            new CreateItemRequest("Item Original", "Descrição Original", 100.0),
            token
        ).andReturn();

        final var itemId = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResponse.getResponse().getContentAsString())
            .get("id").asText();

        final var expectedName = "Item Atualizado";
        final var expectedDescription = "Descrição Atualizada";
        final var expectedPrice = 150.0;

        // When
        final var response = updateItem(
            itemId,
            new UpdateItemRequest(expectedName, expectedDescription, expectedPrice),
            token
        );

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(itemId)));

        final var item = itemRepository.findById(itemId).get();
        Assertions.assertEquals(expectedName, item.getName());
        Assertions.assertEquals(expectedDescription, item.getDescription());
        Assertions.assertEquals(expectedPrice, item.getPrice());
    }

    @Test
    public void asAnAdminIShouldBeAbleToDeleteAItemByItsIdentifier() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("admin", "admin123");
        
        final var createResponse = createItem(
            new CreateItemRequest("Item Para Deletar", "Será removido", 50.0),
            token
        ).andReturn();

        final var itemId = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResponse.getResponse().getContentAsString())
            .get("id").asText();

        Assertions.assertEquals(1, itemRepository.count());

        // When
        final var response = deleteItem(itemId, token);

        // Then
        response.andExpect(status().isNoContent());
        Assertions.assertEquals(0, itemRepository.count());
    }

    @Test
    public void asAUserIShouldBeAbleToCreateANewItem() throws Exception {
        // Given
        Assertions.assertEquals(0, itemRepository.count());
        final var token = login("user", "user123");
        final var expectedName = "Item do User";
        final var expectedDescription = "Criado por um usuário comum";
        final var expectedPrice = 50.0;
        final var request = new CreateItemRequest(expectedName, expectedDescription, expectedPrice);

        // When
        final var response = createItem(request, token);

        // Then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()));
        
        Assertions.assertEquals(1, itemRepository.count());
        
        final var item = itemRepository.findAll().iterator().next();
        Assertions.assertEquals(expectedName, item.getName());
        Assertions.assertEquals(expectedDescription, item.getDescription());
        Assertions.assertEquals(expectedPrice, item.getPrice());
    }

    @Test
    public void asAUserIShouldBeAbleToListItems() throws Exception {
        // Given
        final var adminToken = login("admin", "admin123");
        createItem(new CreateItemRequest("Item 1", "Description 1", 10.0), adminToken);
        
        final var userToken = login("user", "user123");

        // When
        final var response = listItems(userToken, 0, 10, null);

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Item 1")));
    }

    @Test
    public void withoutAuthenticationIShouldReceive401() throws Exception {
        // When
        final var response = mvc().perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/items")
                .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andExpect(status().isUnauthorized());
    }
}
