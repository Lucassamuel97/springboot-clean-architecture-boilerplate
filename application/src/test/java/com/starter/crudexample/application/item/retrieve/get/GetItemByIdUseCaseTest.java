package com.starter.crudexample.application.item.retrieve.get;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;

public class GetItemByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetItemByIdUseCase useCase;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(itemGateway);
    }

    @Test
    public void givenAValidId_whenCallsItem_shouldReturnIt() {
        // given
        final var expectedName = "Smartphone";
        final var expectedDescription = "Latest smartphone model";
        final var expectedPrice = 999.99;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var expectedId = aItem.getId();

        when(itemGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aItem));

        // when
        final var actualOutput = useCase.execute(expectedId.getValue());

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
        Assertions.assertEquals(expectedName, actualOutput.name());
        Assertions.assertEquals(expectedDescription, actualOutput.description());
        Assertions.assertEquals(expectedPrice, actualOutput.price());
        Assertions.assertEquals(aItem.getCreatedAt(), actualOutput.createdAt());

        verify(itemGateway).findById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetItemAndDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var expectedId = "123";

        when(itemGateway.findById(eq(ItemID.from(expectedId))))
                .thenReturn(Optional.empty());

        // when
        final var actualException = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId)
        );

        // then
        Assertions.assertEquals("Item with ID 123 was not found", actualException.getMessage());

        verify(itemGateway).findById(eq(ItemID.from(expectedId)));
    }
}
