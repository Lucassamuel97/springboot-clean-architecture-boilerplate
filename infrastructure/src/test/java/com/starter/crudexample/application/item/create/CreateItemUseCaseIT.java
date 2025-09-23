package com.starter.crudexample.application.item.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.IntegrationTest;
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
}
