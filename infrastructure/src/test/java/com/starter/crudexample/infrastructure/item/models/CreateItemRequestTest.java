package com.starter.crudexample.infrastructure.item.models;

import com.starter.crudexample.JacksonTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

@JacksonTest
public class CreateItemRequestTest {
        @Autowired
        private JacksonTester<CreateItemRequest> json;

        @Test
        public void testUnmarshall() throws Exception {
                final var expectedName = "nome teste";
                final var expectedDescription = "Item description";
                final var expectedPrice = 10.5;

                final var json = """
                                {
                                  "name": "%s",
                                  "description": "%s",
                                  "price": %s
                                }
                                """.formatted(expectedName, expectedDescription, expectedPrice);

                final var actualJson = this.json.parse(json);

                Assertions.assertThat(actualJson)
                                .hasFieldOrPropertyWithValue("name", expectedName)
                                .hasFieldOrPropertyWithValue("description", expectedDescription)
                                .hasFieldOrPropertyWithValue("price", expectedPrice);
        }
}
