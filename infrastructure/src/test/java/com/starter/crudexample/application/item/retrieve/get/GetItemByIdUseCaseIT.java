package com.starter.crudexample.application.item.retrieve.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Assertions;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

@IntegrationTest
public class GetItemByIdUseCaseIT {
    @Autowired
    private GetItemByIdUseCase useCase;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void givenAValidId_whenCallsGetItem_shouldReturnIt() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var expectedId = aItem.getId();

        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        Assertions.assertEquals(1, this.itemRepository.count());

        // when
        final var actualItem = useCase.execute(expectedId.getValue());

        // then
        Assertions.assertNotNull(actualItem);
        Assertions.assertEquals(expectedId.getValue(), actualItem.id());
        Assertions.assertEquals(expectedName, actualItem.name());
        Assertions.assertEquals(expectedDescription, actualItem.description());
        Assertions.assertEquals(expectedPrice, actualItem.price());
        Assertions.assertEquals(aItem.getCreatedAt(), actualItem.createdAt());
        Assertions.assertEquals(aItem.getUpdatedAt(), actualItem.updatedAt());
    }

    @Test
    public void givenAInvalidId_whenCallsGetItemAndDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var expectedId = ItemID.from("123");
        final var expectedErrorMessage = "Item with ID 123 was not found";
        
         // when
        final var actualOutput = Assertions.assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());
    }
}