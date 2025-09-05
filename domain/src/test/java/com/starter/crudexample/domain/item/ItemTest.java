package com.starter.crudexample.domain.item;

import org.junit.jupiter.api.Test;

import com.starter.crudexample.domain.UnitTest;
import com.starter.crudexample.domain.exceptions.DomainException;
import com.starter.crudexample.domain.validation.handler.ThrowsValidationHandler;

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

    @Test
    public void givenAnInvalidNullName_whenCallNewItemAndValidate_thenShouldReceiveError() {
        //Given
        final String expectedName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        final var actualItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualItem.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
