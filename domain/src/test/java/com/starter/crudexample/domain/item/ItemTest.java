package com.starter.crudexample.domain.item;

import org.junit.jupiter.api.Test;

import com.starter.crudexample.domain.UnitTest;

import org.junit.jupiter.api.Assertions;

class ItemTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallNewItem_thenInstantiateAItem() {
        final var expectedName = "Notebooks";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        final var actualItem = Item.newItem(expectedName, expectedDescription, expectedPrice);

        Assertions.assertNotNull(actualItem);
        Assertions.assertNotNull(actualItem.getId());
        Assertions.assertEquals(expectedName, actualItem.getName());
        Assertions.assertEquals(expectedDescription, actualItem.getDescription());
        Assertions.assertEquals(expectedPrice, actualItem.getPrice());
        Assertions.assertNotNull(actualItem.getCreatedAt());
        Assertions.assertNotNull(actualItem.getUpdatedAt());
        Assertions.assertNull(actualItem.getDeletedAt());
    }
}
