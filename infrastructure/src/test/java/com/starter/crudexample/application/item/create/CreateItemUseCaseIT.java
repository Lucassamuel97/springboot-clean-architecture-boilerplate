package com.starter.crudexample.application.item.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

@IntegrationTest
public class CreateItemUseCaseIT {
    @Autowired
    private CreateItemUseCase useCase;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void givenAValidCommand_whenCallsCreateItem_shouldReturnIt() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualItem = itemRepository.findById(actualOutput.id()).get();

        assertEquals(expectedName, actualItem.getName());
        assertEquals(expectedDescription, actualItem.getDescription());
        assertEquals(expectedPrice, actualItem.getPrice());
        assertNotNull(actualItem.getCreatedAt());
        assertNotNull(actualItem.getUpdatedAt());
        assertNull(actualItem.getDeletedAt());
    }

    @Test
    public void givenAInvalidName_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = null;
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        assertEquals(0, itemRepository.count());
    }

    @Test
    public void givenAInvalidDescription_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final var expectedName = "Valid Item Name";
        final String expectedDescription = null;
        final var expectedPrice = 10.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        assertEquals(0, itemRepository.count());
    }

    @Test
    public void givenAInvalidPrice_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final Double expectedPrice = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        assertEquals(0, itemRepository.count());
    }

    @Test
    public void givenAInvalidCommand_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = null;
        final String expectedDescription = null;
        final Double expectedPrice = null;
        final var expectedErrorCount = 3;
        final var expectedErrorMessage1 = "'name' should not be null";
        final var expectedErrorMessage2 = "'description' should not be null";
        final var expectedErrorMessage3 = "'price' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        // then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage1, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorMessage2, actualException.getErrors().get(1).message());
        assertEquals(expectedErrorMessage3, actualException.getErrors().get(2).message());

        assertEquals(0, itemRepository.count());
    }
}