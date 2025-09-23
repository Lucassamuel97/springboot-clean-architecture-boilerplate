package com.starter.crudexample.domain.item;

import org.junit.jupiter.api.Test;

import com.starter.crudexample.domain.UnitTest;
import com.starter.crudexample.domain.exceptions.DomainException;

import org.junit.jupiter.api.Assertions;

class ItemTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallNewItem_thenInstantiateAItem() {
        // Given
        final var expectedName = "Notebooks";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        // When
        final var actualItem = Item.newItem(expectedName, expectedDescription, expectedPrice);

        // Then
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

        //When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        //Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

     @Test
    public void givenAnInvalidEmptyName_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = "";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = "ab";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameLengthMoreThan255_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                """;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedDescription = "A variety of notebooks";
        final var expectedPrice = 129.99;

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullDescription_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = "Notebooks";
        final String expectedDescription = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";
        final var expectedPrice = 129.99;

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));

        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullPrice_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = "Notebooks";
        final String expectedDescription = "A variety of notebooks";
        final Double expectedPrice = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be null";

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNegativePrice_whenCallNewItemAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = "Notebooks";
        final String expectedDescription = "A variety of notebooks";
        final Double expectedPrice = -1.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be negative";

        // When
        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> Item.newItem(expectedName, expectedDescription, expectedPrice));
                
        // Then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
