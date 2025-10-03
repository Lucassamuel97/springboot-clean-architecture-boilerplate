package com.starter.crudexample.infrastructure.item.models;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.starter.crudexample.JacksonTest;

@JacksonTest
public class ItemListResponseTest {
    @Autowired
    private JacksonTester<ItemListResponse> json;

    @Test
    public void testMarshall() throws Exception {
        final var expectedId = "123e4567-e89b-12d3-a456-426614174000";
        final var expectedName = "Item Name";
        final var expectedDescription = "Item Description";
        final var expectedPrice = 99.99;
        final var expectedCreatedAt = Instant.now().toString();

        final var itemListResponse = new ItemListResponse(expectedId, expectedName, expectedDescription, expectedPrice,
                expectedCreatedAt);

        final var actualJson = this.json.write(itemListResponse);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.price", expectedPrice)
                .hasJsonPathValue("$.created_at", expectedCreatedAt);
    }
}
