package com.starter.crudexample.e2e.item;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.infrastructure.item.models.CreateItemRequest;
import com.starter.crudexample.infrastructure.item.models.UpdateItemRequest;

public interface MockDsl {

    MockMvc mvc();

    /**
     * Realiza login e retorna o token JWT
     */
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

    /**
     * Realiza requisição GET com autenticação
     */
    default ResultActions get(String url, String token) throws Exception {
        return mvc().perform(
            MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * Realiza requisição POST para criar item
     */
    default ResultActions createItem(CreateItemRequest request, String token) throws Exception {
        final var mapper = new ObjectMapper();
        return mvc().perform(
            MockMvcRequestBuilders.post("/items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        );
    }

    /**
     * Realiza requisição PUT para atualizar item
     */
    default ResultActions updateItem(String id, UpdateItemRequest request, String token) throws Exception {
        final var mapper = new ObjectMapper();
        return mvc().perform(
            MockMvcRequestBuilders.put("/items/{id}", id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        );
    }

    /**
     * Realiza requisição DELETE para deletar item
     */
    default ResultActions deleteItem(String id, String token) throws Exception {
        return mvc().perform(
            MockMvcRequestBuilders.delete("/items/{id}", id)
                .header("Authorization", "Bearer " + token)
        );
    }

    /**
     * Realiza requisição GET para listar items
     */
    default ResultActions listItems(String token, Integer page, Integer perPage, String search) throws Exception {
        var builder = MockMvcRequestBuilders.get("/items")
            .header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON);

        if (page != null) {
            builder.param("page", page.toString());
        }
        if (perPage != null) {
            builder.param("perPage", perPage.toString());
        }
        if (search != null && !search.isEmpty()) {
            builder.param("search", search);
        }

        return mvc().perform(builder);
    }
}
