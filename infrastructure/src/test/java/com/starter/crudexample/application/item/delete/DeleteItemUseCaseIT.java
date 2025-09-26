package com.starter.crudexample.application.item.delete;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

@IntegrationTest
public class DeleteItemUseCaseIT {
    @Autowired
    private DeleteItemUseCase useCase;

    @Autowired
    private ItemRepository itemRepository;

    @MockitoSpyBean
    private ItemGateway itemGateway;

    @Test
    public void givenAValidId_whenCallsDeleteItem_shouldDeleteIt() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var expectedId = aItem.getId();

        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        Assertions.assertEquals(1, this.itemRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // then
        Assertions.assertEquals(0, this.itemRepository.count());
        Assertions.assertTrue(this.itemRepository.findById(expectedId.getValue()).isEmpty());
    }

    @Test
    public void givenAValidId_whenCallsDeleteItemAndGatewayThrowsException_shouldReceiveException() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var expectedId = aItem.getId();

        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        Assertions.assertEquals(1, this.itemRepository.count());

        doThrow(new IllegalStateException("Gateway error"))
                .when(itemGateway).deleteById(any());

        // when
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        verify(itemGateway).deleteById(eq(expectedId));
        Assertions.assertEquals(1, this.itemRepository.count());
    }
}
