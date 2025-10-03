package com.starter.crudexample.infrastructure.item.models;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.starter.crudexample.JacksonTest;

@JacksonTest
public class ItemResponseTest {
    @Autowired
    private JacksonTester<ItemResponse> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedId = "123e4567-e89b-12d3-a456-426614174000";
        final var expectedName = "Item Name";
        final var expectedDescription = "Item Description";
        final var expectedPrice = 99.99;
        final var expectedCreatedAt = Instant.now().toString();
        final var expectedUpdatedAt = Instant.now().toString();

        final var itemResponse = new ItemResponse(expectedId, expectedName, expectedDescription, expectedPrice, expectedCreatedAt, expectedUpdatedAt);

        final var actualJson = this.json.write(itemResponse);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.price", expectedPrice)
                .hasJsonPathValue("$.created_at", expectedCreatedAt)
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt);
    }
}
